package com.cyclone.ipmi.protocol

import java.util.UUID

import com.cyclone.akka.ActorSystemComponent

/**
  * For testing where actor system shutdown interferes with the extension
  */
trait TestIpmiManagerComponent extends IpmiManagerComponent {
  self: ActorSystemComponent =>
  lazy val ipmiManager = actorSystem.actorOf(IpmiManager.props(), "IpmiManager-" + UUID.randomUUID())
}
