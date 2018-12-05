package com.cyclone.wsman.subscription

import scala.concurrent.duration.FiniteDuration

/**
  * Parameters for (push) delivery expiry and heartbeats
  */
trait DeliveryExpiryParams {
  def heartbeat: Option[FiniteDuration]

  def expiry: Option[FiniteDuration]
}

object DeliveryExpiryParams {

  object NonExpiring extends DeliveryExpiryParams {
    val heartbeat: Option[FiniteDuration] = None
    val expiry: Option[FiniteDuration] = None
  }

  case class Expiring(heartbeatDuration: FiniteDuration, expiryFactor: Int) extends DeliveryExpiryParams {
    def heartbeat = Some(heartbeatDuration)

    val expiry = Some(heartbeatDuration * expiryFactor)
  }

}
