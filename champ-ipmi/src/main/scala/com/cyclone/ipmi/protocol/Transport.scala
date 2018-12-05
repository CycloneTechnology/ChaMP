package com.cyclone.ipmi.protocol

import akka.actor.ActorRef
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.codec.Coder
import com.cyclone.ipmi.protocol.packet.{IpmiVersion, _}

/**
  * Messages for [[UDPTransport]] (and maybe others later).
  */
object Transport {

  trait Factory {
    def createTransport(hub: ActorRef): ActorRef
  }

  case class SendIpmi[P <: IpmiRequestPayload](
    payload: P,
    ipmiVersion: IpmiVersion, sessionContext: SessionContext)
    (implicit val coder: Coder[P])

  case class ReceivedIpmi(payloadOrError: IpmiErrorOr[IpmiResponsePayload], inSession: Boolean)

  case class SetSessionContext(sessionContext: SessionContext)

  case object SetSessionContextAck

}


