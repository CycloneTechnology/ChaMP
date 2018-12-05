package com.cyclone.wsman.impl.http

import com.cyclone.util.net.{DnsLookup, DnsLookupComponent, HttpUrl, SecurityContext}

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
  self: WSManNetworkingComponent
    with DnsLookupComponent =>

  implicit lazy val dns: DnsLookup = dnsLookup

  lazy val wsManConnectionFactory: WSManConnectionFactory =
    (httpUrl: HttpUrl, securityContext: SecurityContext) =>
      new DefaultWSManConnection(httpUrl, securityContext, wsmanNetworking)
}
