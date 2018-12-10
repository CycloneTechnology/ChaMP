package com.cyclone.wsman

import akka.testkit.ImplicitSender
import com.cyclone.akka.{ActorMaterializerComponent, ActorSystemShutdown, TestKitSupport}
import com.cyclone.command.{OperationDeadline, TimeoutContext}
import com.cyclone.util.PasswordCredentials
import com.cyclone.util.kerberos.TestKerberosDeployment
import com.cyclone.util.net.{AuthenticationMethod, JavaNamingDnsLookupComponent, PasswordSecurityContext}
import com.cyclone.wsman.WSMan.httpUrlFor
import com.cyclone.wsman.command.{EnumerateBySelector, Identify}
import com.cyclone.wsman.impl.subscription.push.GuavaKerberosTokenCacheComponent
import org.junit.Test
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitSuiteLike
import org.scalatest.{Inside, Matchers}
import scalaz.{-\/, \/-}

import scala.concurrent.duration._
import scala.language.postfixOps

class WSManSSLTest
    extends TestKitSupport
    with JUnitSuiteLike
    with ActorSystemShutdown
    with WSManTestProperties
    with Matchers
    with Inside
    with WSManTestFileCreation
    with TestWSManComponent
    with GuavaKerberosTokenCacheComponent
    with ActorMaterializerComponent
    with ScalaFutures
    with TestKerberosDeployment
    with ImplicitSender
    with JavaNamingDnsLookupComponent {

  val ssl = false
  val httpUrl = httpUrlFor(hostAndPort, ssl)
  val securityContext = basicAuthSecurityContext
  implicit val timeoutContext: TimeoutContext = TimeoutContext(deadline = OperationDeadline.reusableTimeout(10.seconds))
  val target = WSManTarget(httpUrl, securityContext)

  @Test
  def identify(): Unit = {

    inside(wsman.executeCommandOrError(target, Identify).futureValue) {
      case \/-(result) =>
        result.productVendor shouldBe Some("Openwsman Project")
    }
  }

  @Test
  def failsToConnectIfNotSetForSSL(): Unit = {
    inside(
      wsman.executeCommandOrError(WSManTarget(httpUrlFor(hostAndPort, !ssl), securityContext), Identify).futureValue
    ) {
      case -\/(e) => e shouldBe a[WSManIOError]
    }
  }

  @Test
  def enumerationRequest(): Unit = {
    val query = EnumerateBySelector(ResourceUri("http://sblim.sf.net/wbem/wscim/1/cim-schema/2/Linux_ComputerSystem"))

    inside(wsman.executeCommandOrError(target, query).futureValue) {
      case \/-(result) =>
        result.instances.head.stringProperty("CreationClassName").get shouldBe "Linux_ComputerSystem"
    }
  }

  @Test
  def multipleConcurrentRequests(): Unit = {
    val query = EnumerateBySelector(ResourceUri("http://sblim.sf.net/wbem/wscim/1/cim-schema/2/Linux_ComputerSystem"))

    val results = for (_ <- 1 to 5) yield {
      wsman.executeCommandOrError(target, query)
    }

    for (result <- results) {
      inside(result.futureValue) {
        case \/-(resultItem) =>
          resultItem.instances.head.stringProperty("CreationClassName").get shouldBe "Linux_ComputerSystem"
      }
    }

  }

  @Test
  def basicAuth_authExceptionThrownWhenBadcredentials(): Unit = {
    val query = EnumerateBySelector.fromClassName("ANY")

    inside(
      wsman
        .executeCommandOrError(
          target.copy(
            securityContext = PasswordSecurityContext(
              PasswordCredentials.fromStrings("someUser", "somePassword"),
              AuthenticationMethod.Basic
            )
          ),
          query
        )
        .futureValue
    ) {
      case -\/(e) => e shouldBe a[WSManAuthenticationError]
    }
  }
}
