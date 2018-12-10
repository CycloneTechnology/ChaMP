package com.cyclone.ipmi

import com.cyclone.akka.ActorSystemShutdown
import com.cyclone.command.{OperationDeadline, TimeoutContext}
import com.cyclone.ipmi.api.ActorIpmiClientComponent
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetSessionChallenge.InvalidUserName
import com.cyclone.ipmi.protocol.TestIpmiManagerComponent
import com.cyclone.ipmi.tool.command.ChassisStatusTool
import com.google.common.net.InetAddresses
import org.scalatest.{Inside, Matchers, WordSpecLike}
import scalaz.Scalaz._
import scalaz.{-\/, \/-}

import scala.concurrent.duration._

/**
  * Integration tests for [[Ipmi]]
  */
class IpmiIntegrationTest
  extends BaseIntegrationTest
    with WordSpecLike
    with Matchers
    with Inside
    with ActorSystemShutdown {
  self =>

  class Fixture extends DefaultIpmiComponent
    with ActorIpmiClientComponent
    with TestIpmiManagerComponent
    with TestActorSystemComponent {

    val vReq = IpmiVersionRequirement.V15Only
    val priv = PrivilegeLevel.User

    val testSupportTimeout = 1.second

    implicit val timeoutContext: TimeoutContext = TimeoutContext(OperationDeadline.fromNow(2.seconds))
  }

  "an Ipmi high level API" when {
    "a command is executed" must {
      "execute and return result" in new Fixture {
        inside(ipmi.executeToolCommandOrError(target, ChassisStatusTool.Command).futureValue) {
          case \/-(cmdResult) => cmdResult shouldBe a[ChassisStatusTool.Result]
          case -\/(e)         => fail(s"expected success was $e")
        }
      }
    }

    "support is tested" must {
      "return true for a system that supports IPMI" in new Fixture {
        ipmi.testSupport(target, testSupportTimeout).futureValue shouldBe true
      }

      "return false for a system that does not support IPMI" in new Fixture {
        ipmi.testSupport(IpmiTarget.LAN.forHost("10.0.0.1", credentials = credentials), testSupportTimeout)
          .futureValue shouldBe false
      }

      "return false for a system that does not exist" in new Fixture {
        ipmi.testSupport(IpmiTarget.LAN.forHost("10.0.0.254", credentials = credentials), testSupportTimeout)
          .futureValue shouldBe false
      }
    }

    "session negotiation is tested" must {
      "return true for a system that supports IPMI when correct credentials are specified" in new Fixture {
        ipmi.testNegotiateSession(target, testSupportTimeout)
          .futureValue shouldBe ().right
      }

      "return false for a system that supports IPMI when incorrect credentials are specified" in new Fixture {
        ipmi.testNegotiateSession(target.copy(credentials = IpmiCredentials("noSuchUser", "password")), testSupportTimeout)
          .futureValue shouldBe InvalidUserName.left
      }

      "return false for a system that does not support IPMI" in new Fixture {
        ipmi.testNegotiateSession(target.copy(inetAddress = InetAddresses.forString("10.0.0.1")), testSupportTimeout)
          .futureValue shouldBe DeadlineReached.left
      }

      "return false for a system that does not exist" in new Fixture {
        ipmi.testNegotiateSession(target.copy(inetAddress = InetAddresses.forString("10.0.0.254")), testSupportTimeout)
          .futureValue shouldBe DeadlineReached.left
      }
    }
  }
}
