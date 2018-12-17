package com.cyclone.wsman.impl.subscription.push
import akka.http.scaladsl.server.PathMatcher._
import akka.http.scaladsl.server.{PathMatcher, PathMatchers}

trait PushDeliveryResource {
  def pathMatcher: PathMatcher[Unit]
}

object PushDeliveryResource {
  private[wsman] case class ForString(pathMatcherString: String) extends PushDeliveryResource {
    def pathMatcher: PathMatcher[Unit] = PathMatchers.separateOnSlashes(pathMatcherString)
  }

  private[wsman] object Dummy extends PushDeliveryResource {
    def pathMatcher: PathMatcher[Unit] = PathMatchers.nothingMatcher
  }
}

trait PushDeliveryResourceComponent {
  def pushDeliveryResource: PushDeliveryResource
}

trait DefaultPushDeliveryResourceComponent extends PushDeliveryResourceComponent {
  lazy val pushDeliveryResource: PushDeliveryResource = new PushDeliveryResource {
    def pathMatcher: PathMatcher[Unit] = "wsman" / "event_receiver" / "receive"
  }
}
