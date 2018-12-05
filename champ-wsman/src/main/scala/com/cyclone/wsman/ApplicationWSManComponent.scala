package com.cyclone.wsman

import com.cyclone.akka.{ActorSystemComponent, MaterializerComponent}
import com.cyclone.util.net.DnsLookupComponent
import com.cyclone.wsman.impl.http.{DefaultWSManConnectionFactoryComponent, DefaultWSManNetworkingComponent}
import com.cyclone.wsman.impl.model.OperationsReferenceResolverComponent
import com.cyclone.wsman.impl.subscription.push.{DefaultPushDeliveryRouterComponent, KerberosStateHousekeeperComponent, KerberosTokenCacheComponent}

trait ApplicationWSManComponent
  extends DefaultWSManComponent
    with DefaultWSManContextFactoryComponent
    with DefaultPushDeliveryRouterComponent
    with KerberosStateHousekeeperComponent
    with OperationsReferenceResolverComponent
    with DefaultWSManConnectionFactoryComponent
    with DefaultWSManNetworkingComponent {
  self: ActorSystemComponent
    with MaterializerComponent
    with KerberosTokenCacheComponent
    with DnsLookupComponent =>
}
