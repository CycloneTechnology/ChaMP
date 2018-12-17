package com.cyclone.wsman.impl.subscription.push

import akka.stream.KillSwitches
import akka.stream.scaladsl.{Keep, Sink}
import com.cyclone.util.kerberos.TestKerberosDeployment
import com.cyclone.util.net.HttpUrl
import com.cyclone.wsman.RequiresRealWsman
import com.cyclone.wsman.impl.subscription.{WSManSubscriptionExpiryException, WSManSubscriptionTest}
import com.cyclone.wsman.subscription.DeliveryExpiryParams

import scala.concurrent.duration._
import scala.language.postfixOps

class WSManSubscriptionPushTest
    extends WSManSubscriptionTest
    with TestKerberosDeployment
    with AkkaHttpTestWebServerComponent
    with EventService
    with GuavaKerberosTokenCacheComponent
    with DefaultPushEventXmlParserComponent {

  def route = eventServiceRoute

  lazy val deliveryHandler: PushDeliveryHandler = {
    testWebServer.start()

    PushDeliveryHandler(
      HttpUrl.fromParts(hostAndPort = testWebServer.hostAndPort, resource = "/wsman/event_receiver/receive"),
      DeliveryExpiryParams.Expiring(10.seconds, 2)
    )
  }

  "push subscription handling" must {
    "error if no heartbeat within expiry" taggedAs RequiresRealWsman in {
      lazy val expiringHandler =
        deliveryHandler.copy(
          expiryParams = new DeliveryExpiryParams {
            val heartbeat = Some(20 seconds)
            val expiry = Some(1 seconds)
          }
        )

      val (subscribed, source) = doSubscribe(fileCreationSubsDefn, expiringHandler)
      val (kill, completion) = source.viaMat(KillSwitches.single)(Keep.right).toMat(Sink.ignore)(Keep.both).run

      subscribed.futureValue

      try completion.failed.futureValue shouldBe WSManSubscriptionExpiryException
      finally kill.shutdown()
    }

    "not expire subscription when no events" taggedAs RequiresRealWsman in {
      lazy val expiringHandler =
        deliveryHandler.copy(
          expiryParams = new DeliveryExpiryParams {
            val heartbeat = Some(1 seconds)
            val expiry = Some(2 seconds)
          }
        )

      val (subscribed, source) = doSubscribe(fileCreationSubsDefn, expiringHandler)
      val (kill, completion) = source.viaMat(KillSwitches.single)(Keep.right).toMat(Sink.ignore)(Keep.both).run

      subscribed.futureValue

      try {
        Thread.sleep(5000)

        completion.isCompleted shouldBe false
      } finally kill.shutdown()
    }

    // Test for NP-2377
    "remove token from cache on unsubscribe" taggedAs RequiresRealWsman in {
      val (subscribed, source) = doSubscribe(fileCreationSubsDefn)
      val ((localSubscriptionId, kill), completion) =
        source.viaMat(KillSwitches.single)(Keep.both).toMat(Sink.ignore)(Keep.both).run

      subscribed.futureValue

      kerberosTokenCache.getTokenFor(localSubscriptionId) shouldBe a[Some[_]]

      kill.shutdown()
      Thread.sleep(200)
      kerberosTokenCache.getTokenFor(localSubscriptionId) shouldBe None
    }
  }
}
