package com.cyclone.wsman

import com.cyclone.akka.{ActorMaterializerComponent, ActorSystemShutdown, TestKitSupport}
import com.cyclone.command.{Selector, SelectorClause, TimeoutContext}
import com.cyclone.util.OperationDeadline
import com.cyclone.util.kerberos.TestKerberosDeployment
import com.cyclone.util.net.JavaNamingDnsLookupComponent
import com.cyclone.wsman.WSMan.httpUrlFor
import com.cyclone.wsman.command.{EnumerateBySelector, EnumerateByWQL, Get, Identify}
import com.cyclone.wsman.impl.subscription.push.GuavaKerberosTokenCacheComponent
import org.junit.Test
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.junit.JUnitSuiteLike
import org.scalatest.{Inside, Matchers}
import scalaz.\/-

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Tests for connecting to an openwsman server configured to talk to a CIM provider (e.g. for a storage device).
  */
class WSManTestCIMTest
  extends TestKitSupport
    with JUnitSuiteLike
    with Matchers
    with ScalaFutures
    with Inside
    with IntegrationPatience
    with WSManTestProperties
    with WSManTestFileCreation
    with TestKerberosDeployment
    with ApplicationWSManComponent
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

  @Test
  def identify(): Unit = {
    inside(wsman.executeCommandOrError(target, Identify).futureValue) {
      case \/-(result) => result.productVendor shouldBe Some("Openwsman Project")
    }
  }

  @Test
  def accessRootCimv2Class(): Unit = {
    val query = EnumerateBySelector(ResourceUri("http://cim.test/CIM_ComputerSystem"))

    inside(wsman.executeCommandOrError(target, query).futureValue) {
      case \/-(result) =>
        result.instances.head.stringProperty("Description").get shouldBe "WBEM-enabled computer system"
    }
  }

  // Seem to get more info??
  @Test
  def accessRootCimv2ClassFromProviderNamespace(): Unit = {
    val query = EnumerateBySelector(
      ResourceUri("http://cim.test/CIM_ComputerSystem"),
      cimNamespace = Some("root/LsiArray13"))

    inside(wsman.executeCommandOrError(target, query).futureValue) {
      case \/-(result) =>
        result.instances.head.stringProperty("Caption").get should include("Dixie")
    }
  }

  @Test
  def accessProviderClass_enumeration(): Unit = {
    val query = EnumerateBySelector(
      ResourceUri("http://lsissi.test/LSISSI_DriveFirmwareIdentity"),
      cimNamespace = Some("root/LsiArray13"))

    wsman.executeCommandOrError(target, query).futureValue
  }

  @Test
  def accessProviderClass_enumeration_withSelectorFilter(): Unit = {
    val query = EnumerateBySelector(
      ResourceUri("http://lsissi.test/LSISSI_DriveFirmwareIdentity"),
      selectorClause = SelectorClause(Set(Selector("InstanceID", "600A0B800018491D0000000051524A62_220C000A3303E0FE_Drive_Firmware_Identity"))),
      cimNamespace = Some("root/LsiArray13"))

    inside(wsman.executeCommandOrError(target, query).futureValue) {
      case \/-(result) => result.instances should have size 1
    }
  }

  @Test
  def accessProviderClass_wqlEnumeration(): Unit = {
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

  @Test
  def accessProviderClass_get(): Unit = {
    val query = Get(
      ResourceUri("http://lsissi.test/LSISSI_DriveFirmwareIdentity"),
      selectorClause = SelectorClause(Set(Selector("InstanceID", "600A0B800018491D0000000051524A62_220C000A3303E0FE_Drive_Firmware_Identity"))),
      cimNamespace = Some("root/LsiArray13"))

    wsman.executeCommandOrError(target, query).futureValue
  }
}