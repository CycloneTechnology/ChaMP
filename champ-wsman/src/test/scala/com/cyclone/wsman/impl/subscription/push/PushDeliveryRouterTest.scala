package com.cyclone.wsman.impl.subscription.push

import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import com.cyclone.akka.{ActorMaterializerComponent, ActorSystemShutdown, TestKitSupport}
import com.cyclone.wsman.impl.WSManEnumInstance
import com.cyclone.wsman.impl.model.ManagedInstance
import com.cyclone.wsman.impl.subscription.WSManSubscriptionExpiryException
import com.cyclone.wsman.subscription.SubscriptionId
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._

class PushDeliveryRouterTest extends TestKitSupport with WordSpecLike with Matchers with ActorSystemShutdown {

  class Fixture
      extends DefaultPushDeliveryRouterComponent
      with KerberosStateHousekeeperComponent
      with GuavaKerberosTokenCacheComponent
      with TestActorSystemComponent
      with ActorMaterializerComponent {

    val subsIdA = SubscriptionId("A")
    val subsIdB = SubscriptionId("B")

    // WLOG
    def enumItem(elementName: String) = WSManEnumInstance(ManagedInstance(elementName, "namespace"))
  }

  "PushDeliveryRouter" must {
    "routes messages" in new Fixture {
      val sourceA = pushDeliveryRouter.newSubscriberSource(subsIdA, None)
      val sourceB = pushDeliveryRouter.newSubscriberSource(subsIdB, None)

      val subsA = sourceA.runWith(TestSink.probe)
      val subsB = sourceB.runWith(TestSink.probe)

      val itemA = enumItem("A")
      val itemB = enumItem("B")

      Source
        .single(
          List(
            PushedMessage.Item(itemA, subsIdA),
            PushedMessage.Item(itemB, subsIdB)
          )
        )
        .runWith(pushDeliveryRouter.inputSink)

      subsA.requestNext() shouldBe itemA
      subsB.requestNext() shouldBe itemB
    }

    "expire subscriptions when no data received within expiry at all" in new Fixture {
      lazy val itemA = enumItem("A")

      Source.maybe
        .runWith(pushDeliveryRouter.inputSink)

      val sourceB = pushDeliveryRouter.newSubscriberSource(subsIdB, Some(100.millis))

      val subsB = sourceB.runWith(TestSink.probe)

      // So that there is demand - otherwise will expire anyway
      subsB.request(1)

      subsB.expectError(WSManSubscriptionExpiryException)
    }

    "expire subscriptions when no data received within expiry after initial message" in new Fixture {
      lazy val itemB = enumItem("B")

      (Source.single(List(PushedMessage.Item(itemB, subsIdB))) ++ Source.maybe)
        .runWith(pushDeliveryRouter.inputSink)

      val sourceB = pushDeliveryRouter.newSubscriberSource(subsIdB, Some(100.millis))

      val subsB = sourceB.runWith(TestSink.probe)

      // So that there is demand - otherwise will expire anyway
      subsB.request(1)

      subsB.requestNext() shouldBe itemB
      subsB.expectError(WSManSubscriptionExpiryException)
    }

    "expire only those subscriptions where no data received within expiry" in new Fixture {
      lazy val itemA = enumItem("A")

      Source
        .repeat(List(PushedMessage.Item(itemA, subsIdA)))
        .runWith(pushDeliveryRouter.inputSink)

      val sourceA = pushDeliveryRouter.newSubscriberSource(subsIdA, Some(1.second))
      val sourceB = pushDeliveryRouter.newSubscriberSource(subsIdB, Some(100.millis))

      val subsA = sourceA.runWith(TestSink.probe)
      val subsB = sourceB.runWith(TestSink.probe)

      // So that there is demand - otherwise will expire anyway
      subsA.request(1)
      subsB.request(1)

      subsB.expectError(WSManSubscriptionExpiryException)
      subsA.requestNext() shouldBe itemA
    }

    "not expire subscriptions if heartbeat received before expiry" in new Fixture {
      lazy val itemA = enumItem("A")

      (Source.tick(15.millis, 15.millis, List(PushedMessage.Heartbeat(subsIdA))).take(10) ++
      Source.single(List(PushedMessage.Item(itemA, subsIdA))))
        .runWith(pushDeliveryRouter.inputSink)

      val sourceA = pushDeliveryRouter.newSubscriberSource(subsIdA, Some(100.millis))

      val subsA = sourceA.runWith(TestSink.probe)

      subsA.requestNext() shouldBe itemA
    }

    "not expire if no expiry set" in new Fixture {
      lazy val itemA = enumItem("A")

      Source
        .tick(150.millis, 10.millis, List(PushedMessage.Item(itemA, subsIdA)))
        .take(1)
        .runWith(pushDeliveryRouter.inputSink)

      val sourceA = pushDeliveryRouter.newSubscriberSource(subsIdA, None)

      val subsA = sourceA.runWith(TestSink.probe)

      subsA.requestNext() shouldBe itemA
    }
  }
}
