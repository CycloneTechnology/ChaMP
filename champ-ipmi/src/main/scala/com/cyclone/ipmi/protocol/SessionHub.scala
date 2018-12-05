package com.cyclone.ipmi.protocol

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.IpmiExceptionError
import com.cyclone.ipmi.codec.Coder
import com.cyclone.ipmi.protocol.SessionHub._
import com.cyclone.ipmi.protocol.Transport.Factory
import com.cyclone.ipmi.protocol.packet.{IpmiRequestPayload, IpmiResponsePayload, IpmiVersion, SeqNo}

import scalaz.{-\/, \/-}

object SessionHub {

  trait SessionHubFactory {
    def createHub(context: ActorContext,
      sessionManager: ActorRef,
      transportFactory: Transport.Factory): ActorRef
  }

  object DefaultSessionHubFactory extends SessionHubFactory {
    def createHub(context: ActorContext, sessionManager: ActorRef, transportFactory: Factory): ActorRef =
      context.actorOf(props(sessionManager, transportFactory))
  }

  def props(sessionManager: ActorRef, transportFactory: Transport.Factory) =
    Props(new SessionHub(sessionManager, transportFactory))

  case class RegisterRequestHandler(requestHandler: ActorRef, seqNo: SeqNo)

  case object RequestHandlerRegistered

  case class UnregisterRequestHandler(requestHandler: ActorRef)

  case object RequestHandlerUnregistered

  case class SendIpmi[P <: IpmiRequestPayload](
    payload: P,
    ipmiVersion: IpmiVersion,
    sessionContext: SessionContext)
    (implicit val coder: Coder[P])

  case class ReceivedIpmi(payloadOrError: IpmiErrorOr[IpmiResponsePayload])

  // For testing
  private[protocol] case object GetRequestHandlerCount

  private[protocol] case class RequestHandlerCount(numRequesters: Int)

}

/**
  * Actor responsible for forwarding messages from the connection to an appropriate requester
  * or else to the [[SessionManager]] actor.
  */
class SessionHub(sessionManager: ActorRef, transportFactory: Transport.Factory) extends Actor with ActorLogging {
  var seqNoToRequestHandlerMap = Map.empty[SeqNo, ActorRef]

  val transport: ActorRef = transportFactory.createTransport(self)

  def receive = LoggingReceive {
    case RegisterRequestHandler(requestHandler, seqNo) =>
      seqNoToRequestHandlerMap += (seqNo -> requestHandler)
      sender() ! RequestHandlerRegistered

    case UnregisterRequestHandler(requestHandler) =>
      unregisterRequestHandler(requestHandler)
      sender() ! RequestHandlerUnregistered

    case Transport.ReceivedIpmi(payloadOrError, inSession) =>
      payloadOrError match {
        case \/-(p) =>
          seqNoToRequestHandlerMap
            .get(p.seqNo)
            .foreach { requestHandler =>
              requestHandler ! ReceivedIpmi(payloadOrError)
            }

        case -\/(e) =>
          // If we get a decode error we will not have a sequence number.
          // If there is no session we assume there will be one requester and tell it about the error
          // otherwise we ignore it and allow the corresponding requester to time out
          // (can we do better - assume that everything has gone pair shaped)
          // TODO can we do better than this ^^?
          if (inSession) {
            e match {
              case IpmiExceptionError(ex) => log.error(ex, "Ignoring error with no sequence number: {}", e)
              case _                      => log.warning("Ignoring error with no sequence number: {}", e)
            }
          }
          else {
            seqNoToRequestHandlerMap
              .values
              .foreach { requestHandler =>
                requestHandler ! ReceivedIpmi(payloadOrError)
              }
          }
      }

    case s@SendIpmi(payload, version, sessionContext) =>
      implicit val coder: Coder[IpmiRequestPayload] = s.coder
      transport ! Transport.SendIpmi(payload, version, sessionContext)

    case msg: Transport.SetSessionContext =>
      transport forward msg

    case GetRequestHandlerCount =>
      sender ! RequestHandlerCount(seqNoToRequestHandlerMap.size)
  }

  private def unregisterRequestHandler(requestHandler: ActorRef): Unit =
    seqNoToRequestHandlerMap = seqNoToRequestHandlerMap.filter(_._2 != requestHandler)
}

