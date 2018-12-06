package com.cyclone.wsman.impl.subscription.pull

import com.cyclone.util.kerberos.TestKerberosDeployment
import com.cyclone.wsman.impl.subscription.WSManSubscriptionTest

class WSManSubscriptionPullTest extends WSManSubscriptionTest with TestKerberosDeployment {

  val deliveryHandler = PullDeliveryHandler()
}
