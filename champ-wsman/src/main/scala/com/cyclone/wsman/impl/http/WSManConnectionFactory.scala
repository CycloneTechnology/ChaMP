package com.cyclone.wsman.impl.http

import com.cyclone.util.net.{DnsLookupComponent, HttpUrl, SecurityContext}
import com.cyclone.wsman.impl.http.settings.HttpSettingsComponent

/**
  * Factory for [[WSManConnection]]s
  */
trait WSManConnectionFactory {
  def createConnection(httpUrl: HttpUrl, securityContext: SecurityContext): WSManConnection
}

trait WSManConnectionFactoryComponent {
  def wsManConnectionFactory: WSManConnectionFactory
}

trait DefaultWSManConnectionFactoryComponent extends WSManConnectionFactoryComponent {
  self: AsyncHttpClientComponent with DnsLookupComponent with HttpSettingsComponent =>

  lazy val wsManConnectionFactory: WSManConnectionFactory =
    (httpUrl: HttpUrl, securityContext: SecurityContext) =>
      new DefaultWSManConnection(httpUrl, securityContext, asyncHttpClient, dnsLookup, httpSettings)
}
