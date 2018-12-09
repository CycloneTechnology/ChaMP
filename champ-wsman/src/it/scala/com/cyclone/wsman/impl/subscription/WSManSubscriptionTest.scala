package com.cyclone.wsman.impl.subscription

import java.io.File
import java.lang.management.ManagementFactory

import akka.Done
import akka.stream.scaladsl.{Sink, Source}
import com.cyclone.akka.{ActorMaterializerComponent, ActorSystemShutdown, TestKitSupport}
import com.cyclone.command.{Selector, SelectorClause}
import com.cyclone.util.net.JavaNamingDnsLookupComponent
import com.cyclone.wsman.WSMan._
import com.cyclone.wsman._
import com.cyclone.wsman.command.{WSManInstance, WSManPropertyValue}
import com.cyclone.wsman.impl.DeliveryHandler
import com.cyclone.wsman.impl.subscription.push.GuavaKerberosTokenCacheComponent
import com.cyclone.wsman.subscription._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Inside, Matchers, WordSpecLike}

import scala.concurrent.{Future, Promise}

/**
  * Tests for event subscription.
  *
  * Note wsman.user and wsman.password and wsman.host system properties are required to run this test.
  *
  * @author Jeremy.Stone
  */
trait WSManSubscriptionTest
    extends TestKitSupport
    with WordSpecLike
    with Matchers
    with TestWSManComponent
    with JavaNamingDnsLookupComponent
    with GuavaKerberosTokenCacheComponent
    with ActorMaterializerComponent
    with WSManTestProperties
    with WSManTestFileCreation
    with ScalaFutures
    with Inside
    with IntegrationPatience
    with ActorSystemShutdown {

  val extractFilenameFromResult: WSManInstance => String = { instance =>
    val optName = for {
      WSManPropertyValue.ForInstance(inst) <- instance.properties.get("TargetInstance")
      name                                 <- inst.stringProperty("Name")
    } yield name

    optName.get.toString.split('\\').last
  }

  val extractFilename: File => String = _.getName()

  val deliveryHandler: DeliveryHandler

  val ssl = false
  val securityContext = kerberosSecurityContext
  val httpUrl = httpUrlFor(hostAndPort, ssl)
  val target = WSManTarget(httpUrl, securityContext)

  protected def doSubscribe[S <: WSManSubscriptionDefn: SubscriptionExecutor](
    defn: S,
    deliveryHandler: DeliveryHandler = deliveryHandler
  ): (Future[Done], Source[WSManInstance, SubscriptionId]) = {
    val promise = Promise[Done]

    val source =
      wsman
        .subscribe(target, defn, deliveryHandler)
        .map {
          case x @ SubscriptionItem.Subscribed =>
            promise.success(Done)
            x
          case x => x
        }
        .collect {
          case SubscriptionItem.Instance(instance) => instance
        }

    (promise.future, source)
  }

  protected def fileCreationSubsDefn =
    SubscribeByWQL(
      "SELECT * FROM __InstanceOperationEvent WITHIN 1" +
      " Where Targetinstance Isa 'CIM_DataFile'" +
      " And TargetInstance.Drive='C:'" +
      " And TargetInstance.Path='" + tempDirWMI + "'"
    )

  protected def continuousEventStreamSubscription =
    SubscribeBySelector.fromClassName("Win32_ThreadTrace")

  protected def processIndicationSubscription =
    SubscribeByWQL(
      "SELECT * FROM CIM_ProcessIndication",
      baseResourceUri = ResourceUri("http://schemas.dmtf.org/wbem/wscim/1/*"),
      cimNamespace = Some("root/cimv2")
    )

  protected def evengLogDefn =
    SubscribeByWQL(
      "SELECT * FROM __InstanceCreationEvent WITHIN 1" +
      " WHERE TargetInstance ISA 'Win32_NTLogEvent'"
    )

  protected def evengLogUserDefn =
    SubscribeByWQL(
      "SELECT * FROM __InstanceCreationEvent WITHIN 1" +
      " WHERE TargetInstance ISA 'Win32_NTLogEventUser'"
    )

  private def processID: String = {
    val runtime = ManagementFactory.getRuntimeMXBean
    val jvm = runtime.getClass.getDeclaredField("jvm")
    jvm.setAccessible(true)

    val mgmt = jvm.get(runtime)
    val pid_method = mgmt.getClass.getDeclaredMethod("getProcessId")
    pid_method.setAccessible(true)

    String.valueOf(pid_method.invoke(mgmt).asInstanceOf[Int])
  }

  "subscription handling" must {
    "subscribe successfully" in {
      val (subscribed, source) = doSubscribe(continuousEventStreamSubscription)

      val events = source.take(1).runWith(Sink.seq)

      subscribed.futureValue
    }

    "resolve references" ignore {
      // FIXME what query to use to get data back ?
      fail("TODO: check that instances are resolved")
    }

    // FIXME request looks ok but get response error:
    // "The WS-Management service cannot process the request.
    // The WS-Management service cannot accept subscriptions to an indication class, when either the filter or the dialect is specified.
    //  Retry with filter removed."
    "filter based on selector" ignore {
      val defn = SubscribeBySelector.fromClassName(
        "Win32_ThreadTrace",
        selectorClause = SelectorClause(Set(Selector("ProcessID", processID)))
      )

      val (subscribed, source) = doSubscribe(defn)
      val events = source.take(1).runWith(Sink.seq)

      subscribed.futureValue
      events.futureValue should not be empty
    }

    "receive multiple events" in {
      val numItems = 30

      val (subscribed, source) = doSubscribe(fileCreationSubsDefn)
      val events = source.take(numItems).runWith(Sink.seq)

      subscribed.futureValue
      createTempFiles(numItems)

      events.futureValue should have size numItems
    }

    "receive events in the order they occur" in {
      val numItems = 10

      val (subscribed, source) = doSubscribe(fileCreationSubsDefn)
      val events = source.take(numItems).runWith(Sink.seq)

      subscribed.futureValue
      val files = createTempFiles(numItems)

      val resultFilenames = events.futureValue.map(extractFilenameFromResult)

      val filenames = files map extractFilename

      resultFilenames shouldBe filenames
    }
  }
}
