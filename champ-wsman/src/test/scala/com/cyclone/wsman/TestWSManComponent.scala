package com.cyclone.wsman
import com.cyclone.akka.{ActorSystemComponent, MaterializerComponent}
import com.cyclone.util.net.DnsLookupComponent
import com.cyclone.wsman.impl.http.settings.ConfigHttpSettingsComponent
import com.cyclone.wsman.impl.http.{DefaultWSManConnectionFactoryComponent, DefaultAsyncHttpClientComponent}
import com.cyclone.wsman.impl.model.OperationsReferenceResolverComponent
import com.cyclone.wsman.impl.subscription.push.{
  DefaultPushDeliveryRouterComponent,
  KerberosStateHousekeeperComponent,
  KerberosTokenCacheComponent
}

trait TestWSManComponent
    extends DefaultWSManComponent
    with DefaultWSManContextFactoryComponent
    with DefaultPushDeliveryRouterComponent
    with KerberosStateHousekeeperComponent
    with OperationsReferenceResolverComponent
    with DefaultWSManConnectionFactoryComponent
    with DefaultAsyncHttpClientComponent
    with ConfigHttpSettingsComponent {
  self: ActorSystemComponent with MaterializerComponent with KerberosTokenCacheComponent with DnsLookupComponent =>
}
