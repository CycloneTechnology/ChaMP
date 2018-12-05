package com.cyclone.wsman.subscription

import java.util.UUID

/**
  * Local subscription identifier
  */
case class SubscriptionId(id: String) extends AnyVal

object SubscriptionId {
  def newId: SubscriptionId = SubscriptionId(UUID.randomUUID().toString)
}