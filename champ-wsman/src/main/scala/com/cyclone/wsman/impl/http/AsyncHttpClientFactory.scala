package com.cyclone.wsman.impl.http

import com.cyclone.util.net.SslContextFactory
import com.cyclone.wsman.impl.http.settings.HttpSettingsComponent
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider
import com.ning.http.client.{AsyncHttpClient, AsyncHttpClientConfig}

trait AsyncHttpClientComponent {
  def asyncHttpClient: AsyncHttpClient
}

trait DefaultAsyncHttpClientComponent extends AsyncHttpClientComponent {
  self: HttpSettingsComponent =>

  private lazy val sslContext = SslContextFactory.createTrustAllSSLContext("ssl")

  lazy val asyncHttpClient: AsyncHttpClient = {
    val clientConfBuilder = new AsyncHttpClientConfig.Builder()

    clientConfBuilder.setSSLContext(sslContext)
    clientConfBuilder.setConnectTimeout(httpSettings.connectTimeout.toMillis.toInt)

    new AsyncHttpClient(new NettyAsyncHttpProvider(clientConfBuilder.build()))
  }

}
