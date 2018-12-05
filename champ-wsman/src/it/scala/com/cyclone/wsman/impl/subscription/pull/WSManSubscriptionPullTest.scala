package com.cyclone.wsman.impl.subscription.pull

import com.cyclone.util.ResettingDeadline
import com.cyclone.util.kerberos.TestKerberosDeployment
import com.cyclone.wsman.impl.EnumerationParameters
import com.cyclone.wsman.impl.subscription.WSManSubscriptionTest

import scala.concurrent.duration._

class WSManSubscriptionPullTest
  extends WSManSubscriptionTest
    with TestKerberosDeployment {

  val deliveryHandler = PullDeliveryHandler(
    EnumerationParameters(10, ResettingDeadline(5.seconds)))
}