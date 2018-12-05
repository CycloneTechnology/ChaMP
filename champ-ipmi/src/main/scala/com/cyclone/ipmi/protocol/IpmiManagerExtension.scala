package com.cyclone.ipmi.protocol

import akka.actor.{ActorRef, ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}

/**
  * Actor extension providing a singleton [[IpmiManager]].
  */
class IpmiManagerExtensionImpl(actorSystem: ActorSystem) extends Extension {
  val ipmiManager: ActorRef = actorSystem.actorOf(IpmiManager.props(), "IpmiManager")
}

object IpmiManagerExtension
  extends ExtensionId[IpmiManagerExtensionImpl]
    with ExtensionIdProvider {

  def lookup(): ExtensionId[IpmiManagerExtensionImpl] = IpmiManagerExtension

  def createExtension(system: ExtendedActorSystem) =
    new IpmiManagerExtensionImpl(system)
}
