package com.cyclone.ipmi.protocol

import akka.testkit.{ImplicitSender, TestProbe}
import akka.util.ByteString
import com.cyclone.akka.{ActorSystemShutdown, TestKitSupport}
import com.cyclone.command.{OperationDeadline, RequestTimeouts, TimeoutContext}
import com.cyclone.ipmi._
import com.cyclone.ipmi.command._
import com.cyclone.ipmi.command.fruInventory.ReadFruData
import com.cyclone.ipmi.command.global.{DeviceAddress, DeviceId}
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetChannelCipherSuites
import com.cyclone.ipmi.protocol.RequestHandler.{RequestResult, SendRequest}
import com.cyclone.ipmi.protocol.packet._
import org.scalatest.{Inside, Matchers, WordSpecLike}
import scalaz.-\/
import scalaz.Scalaz._

import scala.concurrent.duration._

/**
  * Tests for [[RequestHandler]]
  */
class RequestHandlerTest
    extends TestKitSupport
    with WordSpecLike
    with Matchers
    with Inside
    with ImplicitSender
    with ActorSystemShutdown {

  // Create one of these for each command type in the test so that can
  // keep the command's decoder happy with something
  trait DummyResponseData[C <: IpmiStandardCommand] {
    def responseData: ByteString
  }

  implicit object GetChannelCipherSuitesDummyResponseData extends DummyResponseData[GetChannelCipherSuites.Command] {
    def responseData = ByteString(1, 2, 3)
  }

  implicit object ReadFRUDataDummyResponseData extends DummyResponseData[ReadFruData.Command] {
    def responseData = ByteString(1, 2)
  }

  class Fixture(attemptTimeout: FiniteDuration = 700.millis, timeout: FiniteDuration = 1500.millis) {
    // WLOG
    val sessionContext = SessionContext.NoSession

    val maxAttempts = 2
    val version = IpmiVersion.V20
    val hub = TestProbe()

    val timeoutContext =
      TimeoutContext(OperationDeadline.fromNow(timeout), RequestTimeouts.simple(attemptTimeout, maxAttempts))

    val requester = system.actorOf(RequestHandler.props(hub.ref, version, sessionContext, timeoutContext))

    val seqNo = SeqNo(12.toByte)

    def willRegisterWithHub(): Unit = {
      hub.expectMsg(SessionHub.RegisterRequestHandler(requester, seqNo))
      requester ! SessionHub.RequestHandlerRegistered
    }

    def response[C <: IpmiStandardCommand](command: C, statusCode: StatusCode)(implicit dummy: DummyResponseData[C]) =
      StandardCommandWrapper.ResponsePayload(
        resultData = dummy.responseData,
        statusCode = statusCode,
        networkFunction = command.networkFunction,
        seqNo = seqNo,
        commandCode = command.commandCode,
        requesterAddress = DeviceAddress.RemoteConsoleAddress,
        responderAddress = DeviceAddress.BmcAddress
      )
  }

  "a request handler" when {
    "a request succeeds" must {
      "acquire a sequence number, register with a hub, send request, return result to the client and unregister" in new Fixture {
        val command = GetChannelCipherSuites.Command(0)
        requester ! SendRequest(seqNo, command)

        willRegisterWithHub()

        val sendPayload = StandardCommandWrapper.RequestPayload.fromCommand(command, seqNo)
        hub.expectMsg(SessionHub.SendIpmi(sendPayload, version, sessionContext))

        requester ! SessionHub.ReceivedIpmi(response(command, StatusCode.NoErrors).right)

        val result = GetChannelCipherSuites.CommandResult(1, ByteString(2, 3))
        expectMsg(RequestResult(result.right))

        hub.expectMsg(SessionHub.UnregisterRequestHandler(requester))
      }
    }

    "a request fails" must {
      "convert failure completion codes to errors" in new Fixture {
        val command = GetChannelCipherSuites.Command(0)
        requester ! SendRequest(seqNo, command)

        willRegisterWithHub()

        val sendPayload = StandardCommandWrapper.RequestPayload.fromCommand(command, seqNo)
        hub.expectMsg(SessionHub.SendIpmi(sendPayload, version, sessionContext))

        requester ! SessionHub.ReceivedIpmi(response(command, StatusCode(0xC0)).right)

        inside(expectMsgType[RequestResult]) {
          case RequestResult(-\/(e)) =>
            e.message shouldBe GenericStatusCodeErrors.lookup(StatusCode(0xC0.toByte)).message
        }

        hub.expectMsg(SessionHub.UnregisterRequestHandler(requester))
      }

      "convert failure decodes to errors" in new Fixture {
        val command = GetChannelCipherSuites.Command(0)
        requester ! SendRequest(seqNo, command)

        willRegisterWithHub()

        val sendPayload = StandardCommandWrapper.RequestPayload.fromCommand(command, seqNo)
        hub.expectMsg(SessionHub.SendIpmi(sendPayload, version, sessionContext))

        requester ! SessionHub.ReceivedIpmi(GenericStatusCodeErrors.InvalidDataField.left)

        inside(expectMsgType[RequestResult]) {
          case RequestResult(-\/(e)) => e shouldBe GenericStatusCodeErrors.InvalidDataField
        }

        hub.expectMsg(SessionHub.UnregisterRequestHandler(requester))
      }
    }

    "deadline reached" when {
      "a request is made" must {
        "respond with a timeout error" in new Fixture(attemptTimeout = 95.millis, timeout = 200.millis) {
          val command = GetChannelCipherSuites.Command(0)
          requester ! SendRequest(seqNo, command)

          willRegisterWithHub()

          val sendPayload = StandardCommandWrapper.RequestPayload.fromCommand(command, seqNo)
          hub.expectMsg(SessionHub.SendIpmi(sendPayload, version, sessionContext))
          hub.expectMsg(SessionHub.SendIpmi(sendPayload, version, sessionContext))

          expectMsg(RequestResult(DeadlineReached.left))

          hub.expectMsg(SessionHub.UnregisterRequestHandler(requester))
        }
      }
    }

    "individual attempts time out" must {
      "retry requests" in new Fixture {
        val command = GetChannelCipherSuites.Command(0)
        requester ! SendRequest(seqNo, command)

        willRegisterWithHub()

        val sendPayload = StandardCommandWrapper.RequestPayload.fromCommand(command, seqNo)
        hub.expectMsg(SessionHub.SendIpmi(sendPayload, version, sessionContext))
        hub.expectMsg(SessionHub.SendIpmi(sendPayload, version, sessionContext))

        requester ! SessionHub.ReceivedIpmi(response(command, StatusCode.NoErrors).right)

        val result = GetChannelCipherSuites.CommandResult(1, ByteString(2, 3))
        expectMsg(RequestResult(result.right))

        hub.expectMsg(SessionHub.UnregisterRequestHandler(requester))
      }

      "fail after all attempts are used up" in new Fixture {
        val command = GetChannelCipherSuites.Command(0)
        requester ! SendRequest(seqNo, command)

        willRegisterWithHub()

        val sendPayload = StandardCommandWrapper.RequestPayload.fromCommand(command, seqNo)

        for (_ <- 1 to maxAttempts)
          hub.expectMsg(SessionHub.SendIpmi(sendPayload, version, sessionContext))

        expectMsg(RequestResult(TimeoutTooManyAttempts.left))

        hub.expectMsg(SessionHub.UnregisterRequestHandler(requester))
      }
    }

    "a request fails with an error indicating a retry is permitted" must {
      "retry requests " in new Fixture {
        val command = ReadFruData.Command(DeviceId(0), 0, 0)
        requester ! SendRequest(seqNo, command)

        willRegisterWithHub()

        val sendPayload = StandardCommandWrapper.RequestPayload.fromCommand(command, seqNo)

        hub.expectMsg(SessionHub.SendIpmi(sendPayload, version, sessionContext))
        requester ! SessionHub.ReceivedIpmi(response(command, ReadFruData.FRUDeviceBusy.code).right)

        hub.expectMsg(SessionHub.SendIpmi(sendPayload, version, sessionContext))
        requester ! SessionHub.ReceivedIpmi(response(command, StatusCode.NoErrors).right)

        val result = ReadFruData.CommandResult(ByteString(2))
        expectMsg(RequestResult(result.right))

        hub.expectMsg(SessionHub.UnregisterRequestHandler(requester))
      }

      "fail after all attempts are used up" in new Fixture {
        val command = ReadFruData.Command(DeviceId(0), 0, 0)
        requester ! SendRequest(seqNo, command)

        willRegisterWithHub()

        val sendPayload = StandardCommandWrapper.RequestPayload.fromCommand(command, seqNo)

        for (_ <- 1 to maxAttempts) {
          hub.expectMsg(SessionHub.SendIpmi(sendPayload, version, sessionContext))
          requester ! SessionHub.ReceivedIpmi(response(command, ReadFruData.FRUDeviceBusy.code).right)
        }

        expectMsg(RequestResult(ReadFruData.FRUDeviceBusy.left))

        hub.expectMsg(SessionHub.UnregisterRequestHandler(requester))
      }
    }

    "unregistration is acknowledged" must {
      "stop self" in new Fixture {
        val command = GetChannelCipherSuites.Command(0)
        requester ! SendRequest(seqNo, command)

        willRegisterWithHub()

        val sendPayload = StandardCommandWrapper.RequestPayload.fromCommand(command, seqNo)
        hub.expectMsg(SessionHub.SendIpmi(sendPayload, version, sessionContext))

        requester ! SessionHub.ReceivedIpmi(response(command, StatusCode.NoErrors).right)

        val result = GetChannelCipherSuites.CommandResult(1, ByteString(2, 3))
        expectMsg(RequestResult(result.right))

        hub.expectMsg(SessionHub.UnregisterRequestHandler(requester))

        watch(requester)
        requester ! SessionHub.RequestHandlerUnregistered
        expectTerminated(requester)
      }
    }
  }
}
