package com.cyclone.wsman.impl.subscription.pull

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.cyclone.util.OperationDeadline
import com.cyclone.wsman.WSManOperationContext
import com.cyclone.wsman.impl._
import com.cyclone.wsman.impl.model.ManagedReference
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._

/**
  * [[DeliveryHandler]] for pulling notifications.
  *
  * @param maxElementsPerPull the maximum number of elements to include in each pull
  * @param pullTimeout        the time to wait for each pull to complete
  */
case class PullDeliveryHandler(maxElementsPerPull: Int = 10, pullTimeout: FiniteDuration = 5.seconds)
    extends DeliveryHandler
    with LazyLogging {
  type R = PullEventSubscriptionRegistration

  class PullEventSubscriptionRegistration(
    subscriptionRef: ManagedReference,
    subscriptionDescriptor: SubscriptionDescriptor,
    initialEnumerationContext: String
  )(implicit context: WSManOperationContext)
      extends EventSubscriptionRegistration(subscriptionRef, subscriptionDescriptor) {

    val enumerator: WSManEnumerator =
      WSManEnumerator(
        subscriptionRef,
        initialEnumerationContext,
        EnumerationParameters(maxElementsPerPull, OperationDeadline.reusableTimeout(pullTimeout)),
        releaseOnClose = false // Because we unsubscribe when the stream completes
      )

    def pullEnumerate: Source[Batch, NotUsed] = enumerator.enumerate
  }

  val deliveryModeString: String = EventMode.EVENT_MODE_PULL

  def createRegistration(
    ref: ManagedReference,
    subscriptionDescriptor: SubscriptionDescriptor,
    initialEnumerationContext: String
  )(implicit context: WSManOperationContext): PullEventSubscriptionRegistration =
    new PullEventSubscriptionRegistration(ref, subscriptionDescriptor, initialEnumerationContext)

  def setupDelivery(
    context: WSManOperationContext,
    subscriptionRegistration: PullEventSubscriptionRegistration
  ): Source[WSManEnumItem, NotUsed] = {
    subscriptionRegistration.pullEnumerate
      .flatMapConcat { batch =>
        Source(batch.items)
      }
  }
}
