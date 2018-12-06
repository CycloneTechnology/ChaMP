package com.cyclone.wsman.impl.subscription

import com.cyclone.wsman.command.WSManInstance

sealed trait SubscriptionItem

object SubscriptionItem {

  object Subscribed extends SubscriptionItem

  case class Instance(instance: WSManInstance) extends SubscriptionItem

}
