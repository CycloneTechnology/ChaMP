package com.cyclone.wsman.impl.http

import com.cyclone.util.net.SslContextFactory
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider
import com.ning.http.client.{AsyncHttpClient, AsyncHttpClientConfig}

import scala.concurrent.duration._

trait WSManNetworking {
  def connectTimeout: FiniteDuration

  def defaultRequestTimeout: FiniteDuration

  def minimumRequestTimeout: FiniteDuration

  def asyncHttpClient: AsyncHttpClient
}

trait WSManNetworkingComponent {
  def wsmanNetworking: WSManNetworking
}

trait DefaultWSManNetworkingComponent extends WSManNetworkingComponent {

  lazy val wsmanNetworking: WSManNetworking = new WSManNetworking {

    val connectTimeout: FiniteDuration = 10.seconds
    val defaultRequestTimeout: FiniteDuration = 10.seconds
    val minimumRequestTimeout: FiniteDuration = 2.seconds

    assert(defaultRequestTimeout >= minimumRequestTimeout)

    lazy val asyncHttpClient: AsyncHttpClient = {
      val clientConfBuilder = new AsyncHttpClientConfig.Builder()

      clientConfBuilder.setSSLContext(sslContext)
      clientConfBuilder.setConnectTimeout(connectTimeout.toMillis.toInt)

      new AsyncHttpClient(new NettyAsyncHttpProvider(clientConfBuilder.build()))
    }

    private lazy val sslContext = SslContextFactory.createTrustAllSSLContext("ssl")
  }
}
