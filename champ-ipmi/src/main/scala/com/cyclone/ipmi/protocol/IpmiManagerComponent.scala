package com.cyclone.ipmi.protocol

import akka.actor.ActorRef
import com.cyclone.akka.ActorSystemComponent

/**
  * Trait that provides an [[com.cyclone.ipmi.protocol.IpmiManager]] instance.
  */
trait IpmiManagerComponent {
  def ipmiManager: ActorRef
}

trait ExtensionIpmiManagerComponent extends IpmiManagerComponent {
  self: ActorSystemComponent =>
  lazy val ipmiManager: ActorRef = IpmiManagerExtension(actorSystem).ipmiManager
}
