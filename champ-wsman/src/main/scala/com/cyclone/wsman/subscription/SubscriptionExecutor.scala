package com.cyclone.wsman.subscription

import akka.stream.scaladsl.Source
import com.cyclone.wsman.WSManOperationContext
import com.cyclone.wsman.impl.subscription.SubscriptionItem
import com.cyclone.wsman.impl.DeliveryHandler

trait SubscriptionExecutor[SubscriptionDefn <: WSManSubscriptionDefn] {

  def source(sub: SubscriptionDefn, deliveryHandler: DeliveryHandler)(
    implicit context: WSManOperationContext
  ): Source[SubscriptionItem, SubscriptionId]
}
