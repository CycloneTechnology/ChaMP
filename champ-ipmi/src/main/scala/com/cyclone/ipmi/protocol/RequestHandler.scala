package com.cyclone.ipmi.protocol

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import akka.event.LoggingReceive
import com.cyclone.command.{RequestTimeouts, TimeoutContext}
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.command.global.DeviceAddress
import com.cyclone.ipmi.protocol.RequestHandler._
import com.cyclone.ipmi.protocol.packet._
import com.cyclone.ipmi.{DeadlineReached, TimeoutTooManyAttempts}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.Scalaz._
import scalaz._

object RequestHandler {
  def props(
    hub: ActorRef,
    version: IpmiVersion,
    sessionContext: SessionContext,
    timeoutContext: TimeoutContext) =
    Props(new RequestHandler(hub, version, sessionContext, timeoutContext))

  case class SendRequest[Cmd <: IpmiCommand, Res <: IpmiCommandResult](
    seqNo: SeqNo, command: Cmd, targetAddress: DeviceAddress = DeviceAddress.BmcAddress)
    (implicit val codec: CommandResultCodec[Cmd, Res])

  case class RequestResult(responseOrError: IpmiErrorOr[IpmiCommandResult])

  private case object RequestAttemptTimeout

  private case object DeadlineReachedTimeout

  private case object RetryNow

}

/**
  * Actor that handles a single a request and receives a response for it.
  */
class RequestHandler(
  hub: ActorRef,
  version: IpmiVersion,
  sessionContext: SessionContext,
  timeoutContext: TimeoutContext) extends Actor with ActorLogging {

  var deadlineTimer = Option.empty[Cancellable]

  def receive: Receive = awaitingRequest

  def awaitingRequest: Receive = LoggingReceive.withLabel(s"awaiting request") {
    case req: SendRequest[c, r] =>
      hub ! SessionHub.RegisterRequestHandler(self, req.seqNo)

      context become registeringWithHub(sender, req)
  }

  def registeringWithHub[Cmd <: IpmiCommand, Res <: IpmiCommandResult](
    client: ActorRef,
    request: SendRequest[Cmd, Res]): Receive = LoggingReceive.withLabel(s"registering with hub for seqNo ${request.seqNo}") {
    case SessionHub.RequestHandlerRegistered =>
      implicit val codec: CommandResultCodec[Cmd, Res] = request.codec

      val messageToSend = request.command match {
        case req: IpmiStandardCommand =>
          SessionHub.SendIpmi(
            StandardCommandWrapper.RequestPayload(
              req.networkFunction,
              req.commandCode,
              request.seqNo,
              request.targetAddress,
              commandData = codec.coder.encode(request.command)),
            version, sessionContext)

        case req: IpmiSessionActivationCommand =>
          SessionHub.SendIpmi(
            SessionActivationCommandWrapper.RequestPayload(
              req.payloadType,
              request.seqNo,
              commandData = codec.coder.encode(request.command)),
            version, sessionContext)
      }

      deadlineTimer = Some(context.system.scheduler.scheduleOnce(
        timeoutContext.deadline.timeRemaining, self, DeadlineReachedTimeout))

      doSendRequest(client, request.seqNo, timeoutContext.requestTimeouts, messageToSend)
  }

  def doSendRequest[Cmd <: IpmiCommand, Res <: IpmiCommandResult](
    client: ActorRef, seqNo: SeqNo, timeouts: RequestTimeouts,
    messageToSend: SessionHub.SendIpmi[_ <: IpmiRequestPayload])
    (implicit codec: CommandResultCodec[Cmd, Res]): Unit = {

    val (timeout, optTimeouts) = timeouts.next
    log.debug("Sending request for seq no {}, timeout {}, remaining timeouts {}: {}", seqNo, timeout, optTimeouts, messageToSend)

    hub ! messageToSend

    val timer = context.system.scheduler.scheduleOnce(timeout, self, RequestAttemptTimeout)

    context become awaitingResponse(client, seqNo, optTimeouts, Some(timer), messageToSend)
  }

  def awaitingResponse[Cmd <: IpmiCommand, Res <: IpmiCommandResult](
    client: ActorRef, seqNo: SeqNo, optTimeouts: Option[RequestTimeouts],
    attemptTimer: Option[Cancellable], messageToSend: SessionHub.SendIpmi[_ <: IpmiRequestPayload])
    (implicit codec: CommandResultCodec[Cmd, Res]): Receive = {
    def sendResultAndUnregister(resultOrError: IpmiErrorOr[Res]): Unit = {
      client ! RequestResult(resultOrError)
      deadlineTimer.foreach(_.cancel())
      attemptTimer.foreach(_.cancel())
      hub ! SessionHub.UnregisterRequestHandler(self)
      context become unregistering(seqNo)
    }

    LoggingReceive.withLabel("awaiting response") {
      case SessionHub.ReceivedIpmi(payloadOrError) =>
        attemptTimer.foreach(_.cancel())
        val resultOrError = for {
          payload <- payloadOrError
          _ <- codec.statusCodeTranslator.lookupStatusCode(payload.statusCode).toLeftDisjunction()
          response <- codec.decoder.handleExceptions.decode(payload.resultData)
        } yield response

        log.debug("Received response for seq no {}, remaining timeouts {}: {}", seqNo, optTimeouts, resultOrError)
        resultOrError match {
          case -\/(err) =>
            err.retryAfter match {
              case Some(duration) => optTimeouts match {
                case Some(timeouts) =>
                  context.system.scheduler.scheduleOnce(duration, self, RetryNow)
                  context.become({
                    case RetryNow =>
                      doSendRequest(client, seqNo, timeouts, messageToSend)
                  })
                case None           => sendResultAndUnregister(resultOrError)
              }

              // No retry reqd
              case None => sendResultAndUnregister(resultOrError)
            }

          case _ => sendResultAndUnregister(resultOrError)
        }

      case RequestAttemptTimeout =>
        optTimeouts match {
          case Some(timeouts) => doSendRequest(client, seqNo, timeouts, messageToSend)
          case None           => sendResultAndUnregister(TimeoutTooManyAttempts.left)
        }

      case DeadlineReachedTimeout => sendResultAndUnregister(DeadlineReached.left)
    }
  }

  def unregistering(seqNo: SeqNo): Receive = LoggingReceive.withLabel(s"unregistering with hub for seqNo $seqNo") {
    case SessionHub.RequestHandlerUnregistered => context stop self
  }

}
