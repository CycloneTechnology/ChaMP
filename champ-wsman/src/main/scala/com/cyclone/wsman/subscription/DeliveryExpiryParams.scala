package com.cyclone.wsman.subscription

import scala.concurrent.duration._

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

  val default: DeliveryExpiryParams = DeliveryExpiryParams.Expiring(10.seconds, 2)

  case class Expiring(heartbeatDuration: FiniteDuration, expiryFactor: Int) extends DeliveryExpiryParams {
    def heartbeat = Some(heartbeatDuration)

    val expiry = Some(heartbeatDuration * expiryFactor)
  }

}
