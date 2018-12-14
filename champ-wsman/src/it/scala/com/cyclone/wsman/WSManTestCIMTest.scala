package com.cyclone.wsman

import com.cyclone.akka.{ActorMaterializerComponent, ActorSystemShutdown, TestKitSupport}
import com.cyclone.command.{OperationDeadline, Selector, SelectorClause, TimeoutContext}
import com.cyclone.util.kerberos.TestKerberosDeployment
import com.cyclone.util.net.JavaNamingDnsLookupComponent
import com.cyclone.wsman.WSMan.httpUrlFor
import com.cyclone.wsman.command.{EnumerateBySelector, EnumerateByWQL, Get, Identify}
import com.cyclone.wsman.impl.subscription.push.GuavaKerberosTokenCacheComponent
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Inside, Matchers, WordSpecLike}
import scalaz.\/-

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Tests for connecting to an openwsman server configured to talk to a CIM provider (e.g. for a storage device).
  */
class WSManTestCIMTest
    extends TestKitSupport
    with WordSpecLike
    with Matchers
    with ScalaFutures
    with Inside
    with IntegrationPatience
    with WSManTestProperties
    with WSManTestFileCreation
    with TestKerberosDeployment
    with TestWSManComponent
    with GuavaKerberosTokenCacheComponent
    with ActorMaterializerComponent
    with JavaNamingDnsLookupComponent
    with ActorSystemShutdown {
  self =>

  val ssl = false
  val securityContext = basicAuthSecurityContext
  val httpUrl = httpUrlFor(hostAndPort, ssl)
  implicit val timeoutContext: TimeoutContext = TimeoutContext(OperationDeadline.reusableTimeout(10.seconds))
  val target = WSManTarget(httpUrl, securityContext)

  "wsman" must {
    "do identify" taggedAs RequiresRealWsman in {
      inside(wsman.executeCommandOrError(target, Identify).futureValue) {
        case \/-(result) => result.productVendor shouldBe Some("Openwsman Project")
      }
    }
    "do enumeration" taggedAs RequiresRealWsman in {
      val query = EnumerateBySelector(
        ResourceUri("http://lsissi.test/LSISSI_DriveFirmwareIdentity"),
        cimNamespace = Some("root/LsiArray13")
      )

      wsman.executeCommandOrError(target, query).futureValue
    }

    "do enumeration with selector filter" taggedAs RequiresRealWsman in {
      val query = EnumerateBySelector(
        ResourceUri("http://lsissi.test/LSISSI_DriveFirmwareIdentity"),
        selectorClause = SelectorClause(
          Set(Selector("InstanceID", "600A0B800018491D0000000051524A62_220C000A3303E0FE_Drive_Firmware_Identity"))
        ),
        cimNamespace = Some("root/LsiArray13")
      )

      inside(wsman.executeCommandOrError(target, query).futureValue) {
        case \/-(result) => result.instances should have size 1
      }
    }

    "do enumeration with wql" taggedAs RequiresRealWsman in {
      val query = EnumerateByWQL(
        "select * from LSISSI_DriveFirmwareIdentity " +
        "where InstanceID=\"600A0B800018491D0000000051524A62_220C000A3303E0FE_Drive_Firmware_Identity\"",
        baseResourceUri = ResourceUri("http://schemas.dmtf.org/wbem/wscim/1"),
        cimNamespace = Some("root/LsiArray13")
      )

      inside(wsman.executeCommandOrError(target, query).futureValue) {
        case \/-(result) => result.instances should have size 1
      }
    }

    "do get" taggedAs RequiresRealWsman in {
      val query = Get(
        ResourceUri("http://lsissi.test/LSISSI_DriveFirmwareIdentity"),
        selectorClause = SelectorClause(
          Set(Selector("InstanceID", "600A0B800018491D0000000051524A62_220C000A3303E0FE_Drive_Firmware_Identity"))
        ),
        cimNamespace = Some("root/LsiArray13")
      )

      wsman.executeCommandOrError(target, query).futureValue
    }

    "allow root/cimv2 class" taggedAs RequiresRealWsman in {
      val query = EnumerateBySelector(ResourceUri("http://cim.test/CIM_ComputerSystem"))

      inside(wsman.executeCommandOrError(target, query).futureValue) {
        case \/-(result) =>
          result.instances.head.stringProperty("Description").get shouldBe "WBEM-enabled computer system"
      }
    }

    // Seem to get more info??
    "allow root/cimv2 from provider namespace" taggedAs RequiresRealWsman in {
      val query =
        EnumerateBySelector(ResourceUri("http://cim.test/CIM_ComputerSystem"), cimNamespace = Some("root/LsiArray13"))

      inside(wsman.executeCommandOrError(target, query).futureValue) {
        case \/-(result) =>
          result.instances.head.stringProperty("Caption").get should include("Dixie")
      }
    }

  }
}
