package com.cyclone.ipmi.protocol

import java.net.InetAddress

import akka.actor.{Actor, ActorRef, Props}
import com.cyclone.ipmi.protocol.IpmiManager.{CreateSessionManagerFor, SessionManagerCreated}
import com.cyclone.ipmi.protocol.packet.SeqNo

object IpmiManager {
  def props() = Props(new IpmiManager)

  case class CreateSessionManagerFor(inetAddress: InetAddress, port: Int)

  case class SessionManagerCreated(actorRef: ActorRef)

}

/**
  * Entry point to creating IPMI sessions.
  */
class IpmiManager extends Actor {
  val seqNoManagerFactory: ActorRef = context.actorOf(SeqNoManagerFactory.props(SeqNo.allSeqNos))

  def receive: PartialFunction[Any, Unit] = {
    case CreateSessionManagerFor(address, port) =>
      val sessionManager = context.actorOf(SessionManager.props(seqNoManagerFactory, address, port))
      sender() ! SessionManagerCreated(sessionManager)

  }
}
