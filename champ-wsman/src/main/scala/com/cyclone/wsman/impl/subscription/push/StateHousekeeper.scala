package com.cyclone.wsman.impl.subscription.push

import akka.Done
import com.cyclone.wsman.subscription.SubscriptionId

import scala.concurrent.Future

/**
  * Used to clean up state for a subscription.
  *
  * A (very simple) non-specific trait is used because
  * the [[PushDeliveryRouter]] should not be aware of the details of the state stored
  * at the web service level in the [[EventService]].
  *
  * If this becomes more complex, a callback registration scheme based approach may be required.
  */
trait StateHousekeeper {
  def cleanupStateFor(id: SubscriptionId): Future[Done]
}

trait StateHousekeeperComponent {
  def stateHousekeeper: StateHousekeeper
}

trait KerberosStateHousekeeperComponent extends StateHousekeeperComponent {
  self: KerberosTokenCacheComponent =>

  lazy val stateHousekeeper: StateHousekeeper =
    (id: SubscriptionId) => {
      kerberosTokenCache.deleteTokenFor(id)
      Future.successful(Done)
    }
}
