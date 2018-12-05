package com.cyclone.wsman

import com.cyclone.akka.{ActorMaterializerComponent, ActorSystemShutdown, TestKitSupport}
import com.cyclone.command.TimeoutContext
import com.cyclone.util.kerberos.TestKerberosDeployment
import com.cyclone.util.net.{AuthenticationMethod, HostAndPort, JavaNamingDnsLookupComponent, PasswordSecurityContext}
import com.cyclone.util.{OperationDeadline, PasswordCredentials}
import com.cyclone.wsman.WSMan.httpUrlFor
import com.cyclone.wsman.command.{EnumerateBySelector, Identify}
import com.cyclone.wsman.impl.subscription.push.GuavaKerberosTokenCacheComponent
import org.junit.Test
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.junit.JUnitSuiteLike
import org.scalatest.{Inside, Matchers}
import scalaz.{-\/, \/-}

import scala.concurrent.duration._

class WSManTestBasicAuthTest
  extends TestKitSupport
    with JUnitSuiteLike
    with Matchers
    with ScalaFutures
    with Inside
    with IntegrationPatience
    with WSManTestProperties
    with TestKerberosDeployment
    with ApplicationWSManComponent
    with GuavaKerberosTokenCacheComponent
    with ActorSystemShutdown
    with ActorMaterializerComponent
    with WSManTestFileCreation
    with JavaNamingDnsLookupComponent {

  val ssl = false
  val securityContext = basicAuthSecurityContext
  val httpUrl = httpUrlFor(hostAndPort, ssl)

  implicit val timeoutContext: TimeoutContext = TimeoutContext(OperationDeadline.reusableTimeout(10.seconds))
  val target = WSManTarget(httpUrl, securityContext)


  @Test
  def basicAuth_authExceptionThrownWhenBadcredentials(): Unit = {
    val query = EnumerateBySelector.fromClassName("ANY")

    inside(wsman.executeCommandOrError(
      WSManTarget(httpUrl,
        PasswordSecurityContext(
          PasswordCredentials.fromStrings("someUser", "somePassword"),
          AuthenticationMethod.Basic)),
      query).futureValue) {
      case -\/(e) => e shouldBe a[WSManAuthenticationError]
    }
  }

  @Test
  def identify(): Unit = {
    inside(wsman.executeCommandOrError(target, Identify).futureValue) {
      case \/-(result) => result.productVendor shouldBe Some("Openwsman Project")
    }
  }

  @Test
  def authExceptionWhenIncorrectSecurityContextType(): Unit = {
    inside(wsman.executeCommandOrError(
      WSManTarget(httpUrl, kerberosSecurityContext), Identify)
      .futureValue) {
      case -\/(e) => e shouldBe a[WSManAuthenticationError]
    }
  }

  @Test
  def wrappedWSManExceptionThrownWhenNotConnectable(): Unit = {
    inside(wsman.executeCommandOrError(
      WSManTarget(httpUrlFor(HostAndPort.fromString("npbuild"), ssl), securityContext),
      Identify).futureValue) {
      case -\/(e) =>
        assert(e.message != null, "Null message")
        assert(e.message != "", "No message")
    }
  }
}