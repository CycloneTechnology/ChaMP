package com.cyclone.akka

import akka.actor.ActorSystem

trait ActorSystemComponent {
  implicit def actorSystem: ActorSystem
}
