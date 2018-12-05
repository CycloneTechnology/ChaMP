package com.cyclone.wsman.impl.subscription.pull

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.cyclone.wsman.WSManOperationContext
import com.cyclone.wsman.impl.WSManEnumerator.WSManEnumeratorConfig
import com.cyclone.wsman.impl._
import com.cyclone.wsman.impl.model.ManagedReference
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

/**
  * [[DeliveryHandler]] for pulling notifications.
  */
case class PullDeliveryHandler(enumerationParameters: EnumerationParameters)
  extends DeliveryHandler with LazyLogging {
  type R = PullEventSubscriptionRegistration

  class PullEventSubscriptionRegistration(
    subscriptionRef: ManagedReference,
    subscriptionDescriptor: SubscriptionDescriptor,
    ctx: String)(implicit context: WSManOperationContext)
    extends EventSubscriptionRegistration(subscriptionRef, subscriptionDescriptor) {

    val enumerator: WSManEnumerator =
      new WSManEnumerator(subscriptionRef, ctx) with WSManEnumeratorConfig {
        // Do nothing on close - handled by separate explicit unsubscribe
        protected[wsman] def release(enumContext: String): Future[_] = {
          Future.successful("CLOSE SKIPPED")
        }

        def parameters: EnumerationParameters = enumerationParameters
      }

    def pullEnumerate: Source[Batch, NotUsed] = enumerator.enumerate
  }

  val deliveryModeString: String = EventMode.EVENT_MODE_PULL

  def createRegistration(
    ref: ManagedReference,
    subscriptionDescriptor: SubscriptionDescriptor,
    ctx: String)(implicit context: WSManOperationContext): PullEventSubscriptionRegistration =
    new PullEventSubscriptionRegistration(ref, subscriptionDescriptor, ctx)

  def setupDelivery(context: WSManOperationContext, subscriptionRegistration: PullEventSubscriptionRegistration): Source[WSManEnumItem, NotUsed] = {
    subscriptionRegistration.pullEnumerate
      .flatMapConcat { batch =>
        Source(batch.items)
      }
  }
}
