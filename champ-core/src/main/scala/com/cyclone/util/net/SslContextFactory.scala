package com.cyclone.util.net

import java.security.GeneralSecurityException
import java.security.cert.X509Certificate
import javax.net.ssl.{SSLContext, TrustManager, X509TrustManager}

/**
  * Created by jeremy.stone on 23/11/2016.
  */
object SslContextFactory {
  /**
    * Creates an initialized SSLContext for creating client mode sockets that
    * trust all servers.
    *
    * @param sslProtocol the SSL protocol to use
    * @return the new SSLContext
    */
  @throws[GeneralSecurityException]
  def createTrustAllSSLContext(sslProtocol: String): SSLContext = {
    val sslContext = SSLContext.getInstance(sslProtocol)

    sslContext.init(null, Array[TrustManager](TrustAllTrustManager), null)
    sslContext
  }

  /**
    * TrustManager that trusts all certificates.
    *
    * @author Jeremy.Stone
    */
  private object TrustAllTrustManager extends X509TrustManager {
    final private val acceptedIssuers = Array.empty[X509Certificate]

    def checkClientTrusted(chain: Array[X509Certificate], authType: String) {
      // Nothing to do
    }

    def checkServerTrusted(chain: Array[X509Certificate], authType: String) {
      // Nothing to do
    }

    def getAcceptedIssuers: Array[X509Certificate] = acceptedIssuers
  }

}
