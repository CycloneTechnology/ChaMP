package com.cyclone.wsman.subscription
import akka.actor.ActorSystem
import com.cyclone.wsman.impl.subscription.push.{KerberosTokenCache, PushDeliveryResource, PushDeliveryRouter}

/**
  * Configures push subscription delivery and ties up the web service
  * listening for events with the subscriptions that use push delivery.
  */
trait PushDeliveryConfig {

  private[wsman] def pushDeliveryRouter: PushDeliveryRouter

  private[wsman] def kerberosTokenCache: KerberosTokenCache

  private[wsman] def pushDeliveryResource: PushDeliveryResource
}

object PushDeliveryConfig {

  private[wsman] object Dummy extends PushDeliveryConfig {
    private[wsman] def pushDeliveryRouter = PushDeliveryRouter.Dummy
    private[wsman] def kerberosTokenCache = KerberosTokenCache.Dummy
    private[wsman] def pushDeliveryResource = PushDeliveryResource.Dummy
  }

  /**
    * Creates a [[PushDeliveryConfig]] where events are delivered to a specific local web resource.
    *
    * @param eventResource the web resource where events are to be delivered
    */
  def create(eventResource: String)(implicit actorSystem: ActorSystem): PushDeliveryConfig =
    new PushDeliveryConfig {
      private[wsman] lazy val pushDeliveryRouter = PushDeliveryRouter.create
      private[wsman] lazy val kerberosTokenCache = KerberosTokenCache.create
      private[wsman] lazy val pushDeliveryResource = PushDeliveryResource.ForString(eventResource)
    }
}
