package com.cyclone.wsman.impl.subscription.push

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.cyclone.util.net.HttpUrl
import com.cyclone.wsman.WSManOperationContext
import com.cyclone.wsman.impl._
import com.cyclone.wsman.impl.model.ManagedReference
import com.cyclone.wsman.subscription.{DeliveryExpiryParams, SubscriptionId}

import scala.xml.{Elem, NodeSeq}

case class PushDeliveryHandler(deliverTo: HttpUrl, expiryParams: DeliveryExpiryParams = DeliveryExpiryParams.default) extends DeliveryHandler {
  type R = EventSubscriptionRegistration

  val deliveryModeString: String = EventMode.EVENT_MODE_PUSH_SINGLE

  override def deliveryParameterElements(localSubscriptionId: SubscriptionId): NodeSeq =
    heartbeatElements

  def createRegistration(
    ref: ManagedReference,
    subscriptionDescriptor: SubscriptionDescriptor,
    ctx: String
  )(implicit context: WSManOperationContext) =
    new EventSubscriptionRegistration(ref, subscriptionDescriptor: SubscriptionDescriptor)

  override def notifyElements(localSubscriptionId: SubscriptionId): Elem =
    // @formatter:off
    <wse:NotifyTo>
      <a:Address>{deliverTo.urlString + "?pushId=" + localSubscriptionId.id}</a:Address>
    </wse:NotifyTo>

  // @formatter:on

  def setupDelivery(
    context: WSManOperationContext,
    subscriptionRegistration: EventSubscriptionRegistration
  ): Source[WSManEnumItem, NotUsed] =
    context.pushDeliveryHub
      .newSubscriberSource(subscriptionRegistration.localSubscriptionId, expiryParams.expiry)

  protected def heartbeatElements: NodeSeq =
    // @formatter:off
    expiryParams.heartbeat match {
      case Some(duration) => <w:Heartbeats>PT{duration.toSeconds}.000000S</w:Heartbeats>
      case None           => NodeSeq.Empty
    }

  // @formatter:on
}
