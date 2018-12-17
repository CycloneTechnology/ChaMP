package com.cyclone.wsman

import com.cyclone.akka.{ActorMaterializerComponent, ActorSystemShutdown, TestKitSupport}
import com.cyclone.command.{OperationDeadline, TimeoutContext}
import com.cyclone.util.PasswordCredentials
import com.cyclone.util.kerberos.TestKerberosDeployment
import com.cyclone.util.net.{AuthenticationMethod, HostAndPort, JavaNamingDnsLookupComponent, PasswordSecurityContext}
import com.cyclone.wsman.WSMan.httpUrlFor
import com.cyclone.wsman.command.{EnumerateBySelector, Identify}
import com.cyclone.wsman.impl.subscription.push.GuavaKerberosTokenCacheComponent
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Inside, Matchers, WordSpecLike}
import scalaz.{-\/, \/-}

import scala.concurrent.duration._

class WSManTestBasicAuthTest
    extends TestKitSupport
    with WordSpecLike
    with Matchers
    with ScalaFutures
    with Inside
    with IntegrationPatience
    with WSManTestProperties
    with TestKerberosDeployment
    with TestWSManComponent
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

  "wsman" when {
    "using basic auth" must {

      "indicate when bad credentials" taggedAs RequiresRealWsman in {
        val query = EnumerateBySelector.fromClassName("ANY")

        inside(
          wsman
            .executeCommandOrError(
              WSManTarget(
                httpUrl,
                PasswordSecurityContext(
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

      "allow identify" taggedAs RequiresRealWsman in {
        inside(wsman.executeCommandOrError(target, Identify).futureValue) {
          case \/-(result) => result.productVendor shouldBe Some("Openwsman Project")
        }
      }

      "indicate when invalid authentication type" taggedAs RequiresRealWsman in {
        inside(wsman.executeCommandOrError(WSManTarget(httpUrl, kerberosSecurityContext), Identify).futureValue) {
          case -\/(e) => e shouldBe a[WSManAuthenticationError]
        }
      }

      "indicate when not connectable" taggedAs RequiresRealWsman in {
        inside(
          wsman
            .executeCommandOrError(
              WSManTarget(httpUrlFor(HostAndPort.fromString("npbuild"), ssl), securityContext),
              Identify
            )
            .futureValue
        ) {
          case -\/(e) =>
            assert(e.message != null, "Null message")
            assert(e.message != "", "No message")
        }
      }
    }
  }
}
