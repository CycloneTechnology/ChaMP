package com.cyclone.wsman.impl.subscription

import com.cyclone.wsman.impl.model.ManagedInstance

sealed trait SubscriptionItem

object SubscriptionItem {

  object Subscribed extends SubscriptionItem

  case class Instance(instance: ManagedInstance) extends SubscriptionItem

}
