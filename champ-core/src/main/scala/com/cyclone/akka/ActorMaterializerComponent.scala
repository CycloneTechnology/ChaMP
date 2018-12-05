package com.cyclone.akka

import akka.stream.{ActorMaterializer, ActorMaterializerSettings}

/**
  * MaterializerComponent that uses an actor materializer
  *
  * @author Jeremy.Stone
  */
trait ActorMaterializerComponent extends MaterializerComponent {
  self: ActorSystemComponent =>

  implicit lazy val materializer: ActorMaterializer =
    ActorMaterializer(ActorMaterializerSettings(actorSystem))(actorSystem)
}
