package com.cyclone.wsman.impl.subscription.push

import com.cyclone.util.spnego.Token
import com.cyclone.wsman.subscription.SubscriptionId
import com.google.common.cache.{Cache, CacheBuilder, RemovalNotification}

/**
  * Cache for kerberos tokens.
  *
  * Required because authentication tokens,
  * required to perform decryption of event messages, are not included in
  * requests other than for the initial authentication exchange.
  */
trait KerberosTokenCache {
  def getTokenFor(subscriptionId: SubscriptionId): Option[Token]

  def putTokenFor(subscriptionId: SubscriptionId, token: Token): Unit

  def deleteTokenFor(subscriptionId: SubscriptionId): Unit
}

object KerberosTokenCache {

  /**
    * Creates a KerberosTokenCache
    */
  def create: KerberosTokenCache = {
    new GuavaKerberosTokenCacheComponent {}.kerberosTokenCache
  }

  private[wsman] object Dummy extends KerberosTokenCache {
    def getTokenFor(subscriptionId: SubscriptionId): Option[Token] = None
    def putTokenFor(subscriptionId: SubscriptionId, token: Token): Unit = ()
    def deleteTokenFor(subscriptionId: SubscriptionId): Unit = ()
  }
}

trait KerberosTokenCacheComponent {
  def kerberosTokenCache: KerberosTokenCache
}

trait GuavaKerberosTokenCacheComponent extends KerberosTokenCacheComponent {
  private val cache: Cache[String, Token] =
    CacheBuilder
      .newBuilder()
      .removalListener { notification: RemovalNotification[String, Token] =>
        notification.getValue.dispose()
      }
      .build()

  lazy val kerberosTokenCache: KerberosTokenCache = new KerberosTokenCache {

    def getTokenFor(subscriptionId: SubscriptionId): Option[Token] =
      Option(cache.getIfPresent(subscriptionId.id))

    def putTokenFor(subscriptionId: SubscriptionId, token: Token): Unit =
      cache.put(subscriptionId.id, token)

    def deleteTokenFor(subscriptionId: SubscriptionId): Unit =
      cache.invalidate(subscriptionId.id)
  }
}
