package com.cyclone.ipmi

import com.cyclone.command.{OperationDeadline, TimeoutContext}
import com.cyclone.ipmi.api.MockIpmiClientComponent
import com.cyclone.ipmi.command.GenericStatusCodeErrors.InsufficientPrivilege
import com.cyclone.ipmi.command.global.{ColdReset, WarmReset}
import com.cyclone.ipmi.tool.command.ResetTool
import com.cyclone.ipmi.tool.command.ResetType.{cold, warm}
import com.cyclone.util.SynchronizedMockeryComponent
import com.google.common.net.InetAddresses
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Inside, Matchers, WordSpec}
import scalaz.Scalaz._
import scalaz._

import scala.concurrent.duration._

/**
  * Test for [[Ipmi]]
  */
class IpmiTest extends WordSpec with Matchers with Inside with ScalaFutures with IntegrationPatience {

  class Fixture(deadlineTimeout: FiniteDuration = 10.seconds)
      extends DefaultIpmiComponent
      with MockIpmiClientComponent
      with SynchronizedMockeryComponent {

    // WLOG
    val address = InetAddresses.forString("1.2.3.4")
    val port = 623
    val credentials = IpmiCredentials("user", "password")
    val vReq = IpmiVersionRequirement.V15Only
    val priv = PrivilegeLevel.User
    val target = IpmiTarget.LAN(address, port, credentials, priv, vReq)

    implicit val timeoutContext: TimeoutContext = TimeoutContext(OperationDeadline.fromNow(deadlineTimeout))
  }

  "an Ipmi API" when {
    "a command is executed" must {
      "execute and return the result" in new Fixture {
        val command = ColdReset.Command

        val connection = willGetConnectionFor(address, port).futureValue
        willNegotiateSession(connection, credentials, vReq, priv, ().right)
        willExecuteCommand(connection, ColdReset.Command, ColdReset.CommandResult.right)
        willClosedown(connection)

        inside(ipmi.executeCommandOrError(target, command).futureValue) {
          case \/-(result) => ResetTool.Reset.right
        }

        mockery.assertIsSatisfied()
      }

      "return an error when an error" in new Fixture {
        val command = ColdReset.Command

        val connection = willGetConnectionFor(address, port).futureValue
        willNegotiateSession(connection, credentials, vReq, priv, ().right)
        willExecuteCommand(connection, ColdReset.Command, InsufficientPrivilege.left)
        willClosedown(connection)

        inside(ipmi.executeCommandOrError(target, command).futureValue) {
          case -\/(e) => e shouldBe InsufficientPrivilege
        }

        mockery.assertIsSatisfied()
      }

      "return error when fail to negotiate session" in new Fixture {
        val command = ColdReset.Command

        val connection = willGetConnectionFor(address, port).futureValue

        willNegotiateSession(connection, credentials, vReq, priv, InsufficientPrivilege.left)
        willClosedown(connection)

        inside(ipmi.executeCommandOrError(target, command).futureValue) {
          case -\/(e) => e shouldBe InsufficientPrivilege
        }

        mockery.assertIsSatisfied()
      }
    }

    "a tool command is executed" must {
      // ResetTool.Command(cold) just wraps ColdReset.Command
      "execute and return result" in new Fixture {
        val command = ResetTool.Command(cold)

        val connection = willGetConnectionFor(address, port).futureValue
        willNegotiateSession(connection, credentials, vReq, priv, ().right)
        willExecuteCommand(connection, ColdReset.Command, ColdReset.CommandResult.right)
        willClosedown(connection)

        inside(ipmi.executeToolCommandOrError(target, command).futureValue) {
          case \/-(result) => ResetTool.Reset.right
        }

        mockery.assertIsSatisfied()
      }

      "return error when error" in new Fixture {
        val command = ResetTool.Command(warm)

        val connection = willGetConnectionFor(address, port).futureValue
        willNegotiateSession(connection, credentials, vReq, priv, ().right)
        willExecuteCommand(connection, WarmReset.Command, InsufficientPrivilege.left)
        willClosedown(connection)

        inside(ipmi.executeToolCommandOrError(target, command).futureValue) {
          case -\/(e) => e shouldBe InsufficientPrivilege
        }

        mockery.assertIsSatisfied()
      }
    }
  }

}
