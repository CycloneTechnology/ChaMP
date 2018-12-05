package com.cyclone.ipmi.protocol

import java.net.{InetAddress, InetSocketAddress}

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props, Stash}
import akka.event.LoggingReceive
import akka.io.{IO, Udp}
import com.cyclone.ipmi.IpmiDecodeError
import com.cyclone.ipmi.codec.{Coder, RichByteString}
import com.cyclone.ipmi.protocol.Transport._
import com.cyclone.ipmi.protocol.packet._
import scalaz.Scalaz._

object UDPTransport {

  def props(address: InetAddress, port: Int, hub: ActorRef) =
    Props(new UDPTransport(address, port, hub))

  def factory(context: ActorContext, address: InetAddress, port: Int): Factory = new Factory {
    def createTransport(hub: ActorRef): ActorRef = context.actorOf(props(address, port, hub))
  }

}

// TODO implement SessionSequenceNumber windowing: see appendix in spec.
// ...Send indication to hub as to whether
// too many messages have been missed or whether a message is a resend.
// BUT: what does this give us? We are not receiving requests to which we must reply

/**
  * Actor responsible for sending messages to the BMC over UDP.
  *
  * Handles wrapping and unwrapping of ICMP (request and response) payloads.
  */
class UDPTransport(address: InetAddress, port: Int, hub: ActorRef) extends Actor with Stash with ActorLogging {

  import context.system

  IO(Udp) ! Udp.Bind(self, new InetSocketAddress("0.0.0.0", 0))

  val socketAddress: InetSocketAddress = new InetSocketAddress(address, port)

  var sessionSequenceNumber = Option.empty[SessionSequenceNumber]
  var receiveSessionContext: SessionContext = SessionContext.NoSession

  def receive: Receive = unbound

  def unbound: Receive = LoggingReceive {
    case Udp.Bound(_) =>
      unstashAll()
      context become bound(sender())

    case _ => stash()
  }

  def bound(outbound: ActorRef): Receive = {
    LoggingReceive {
      case SetSessionContext(ctx) =>
        receiveSessionContext = ctx
        sessionSequenceNumber = Some(ctx.initialSendSequenceNumber)
        sender ! SetSessionContextAck

      case s @ SendIpmi(payload, version, ctx) =>
        implicit val coder: Coder[IpmiRequestPayload] = s.coder

        val wrapper =
          IpmiSessionWrapper.wrapperFor(payload, version, nextSessionSequenceNumber, ctx)
        val rmcpMessage = RmcpMessage(IpmiSessionWrapper.encode(wrapper), MessageClass.Ipmi)

        val data = RmcpMessage.encode(rmcpMessage)

        log.debug(s"Sending $payload as \n${data.toHexString()}")
        outbound ! Udp.Send(data, socketAddress)

      case Udp.Received(data, _) =>
        log.debug(s"Received ${data.toHexString()}")

        val payloadOrError = for {
          rmcpMessage <- RmcpMessage.decode(data)
          wrapper     <- IpmiSessionWrapper.decode(rmcpMessage.sessionWrapper, receiveSessionContext)
          _ <- if (rmcpMessage.messageClass == MessageClass.Ipmi) ().right
          else
            IpmiDecodeError(s"Unsupported message class ${rmcpMessage.messageClass}").left
          payload <- IpmiPayload.decode(wrapper.payload, wrapper.payloadType)
        } yield payload

        hub ! ReceivedIpmi(payloadOrError, inSession)
    }
  }

  def inSession: Boolean = sessionSequenceNumber.isDefined

  def nextSessionSequenceNumber: SessionSequenceNumber =
    sessionSequenceNumber match {
      case None => SessionSequenceNumber.NoSession
      case Some(seqNo) =>
        sessionSequenceNumber = Some(seqNo + 1)
        seqNo
    }
}
