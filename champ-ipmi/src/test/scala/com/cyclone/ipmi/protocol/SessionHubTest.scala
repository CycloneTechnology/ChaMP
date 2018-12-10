package com.cyclone.ipmi.protocol

import akka.actor.ActorRef
import akka.testkit.{ImplicitSender, TestProbe}
import akka.util.ByteString
import com.cyclone.akka.{ActorSystemShutdown, TestKitSupport}
import com.cyclone.ipmi.IpmiDecodeError
import com.cyclone.ipmi.codec.Coder
import com.cyclone.ipmi.command.global.DeviceAddress
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCode}
import com.cyclone.ipmi.protocol.Transport.Factory
import com.cyclone.ipmi.protocol.packet._
import org.scalatest.{Matchers, WordSpecLike}
import scalaz.Scalaz._

import scala.concurrent.duration._

/**
  * Tests for [[SessionHub]]
  */
class SessionHubTest
    extends TestKitSupport
    with WordSpecLike
    with Matchers
    with ImplicitSender
    with ActorSystemShutdown {

  class Fixture {
    val version = IpmiVersion.V20
    val transport = TestProbe()
    val sessionManager = TestProbe()
    // WLOG
    val sessionContext = SessionContext.NoSession

    val hub = system.actorOf(
      SessionHub.props(
        sessionManager.ref,
        new Factory {
          def createTransport(hub: ActorRef) = transport.ref
        }
      )
    )

    val cmdCode = CommandCode(123)

    def responsePayload(seqNo: SeqNo): StandardCommandWrapper.ResponsePayload =
      StandardCommandWrapper.ResponsePayload(
        seqNo = seqNo,
        resultData = ByteString.empty,
        statusCode = StatusCode(0),
        networkFunction = NetworkFunction.ApplicationResponse,
        commandCode = cmdCode,
        requesterAddress = DeviceAddress.RemoteConsoleAddress,
        responderAddress = DeviceAddress.BmcAddress
      )

    def requestPayload: StandardCommandWrapper.RequestPayload =
      StandardCommandWrapper.RequestPayload(
        networkFunction = NetworkFunction.ApplicationResponse,
        commandCode = cmdCode,
        seqNo = SeqNo(1),
        targetAddress = DeviceAddress.BmcAddress,
        commandData = ByteString.empty
      )
  }

  "a SessionHub" when {
    "it receives incoming message with a sequence number" must {
      "tell the registered requester" in new Fixture {
        val requester = TestProbe()

        hub ! SessionHub.RegisterRequestHandler(requester.ref, SeqNo(1))
        expectMsg(SessionHub.RequestHandlerRegistered)

        val payload = responsePayload(SeqNo(1))
        hub ! Transport.ReceivedIpmi(payload.right, inSession = true)

        requester.expectMsg(SessionHub.ReceivedIpmi(payload.right))
      }
    }

    "it receives a decode error outside a session" must {
      "notifies all requesters" in new Fixture {
        val requester1 = TestProbe()
        val requester2 = TestProbe()

        hub ! SessionHub.RegisterRequestHandler(requester1.ref, SeqNo(1))
        expectMsg(SessionHub.RequestHandlerRegistered)
        hub ! SessionHub.RegisterRequestHandler(requester2.ref, SeqNo(2))
        expectMsg(SessionHub.RequestHandlerRegistered)

        val error = IpmiDecodeError("problem").left
        hub ! Transport.ReceivedIpmi(error, inSession = false)

        requester1.expectMsg(SessionHub.ReceivedIpmi(error))
        requester2.expectMsg(SessionHub.ReceivedIpmi(error))
      }
    }

    "it receives a decode error inside a session" must {
      "ignore it" in new Fixture {
        val requester = TestProbe()

        hub ! SessionHub.RegisterRequestHandler(requester.ref, SeqNo(1))
        expectMsg(SessionHub.RequestHandlerRegistered)

        val error = IpmiDecodeError("problem").left
        hub ! Transport.ReceivedIpmi(error, inSession = true)

        requester.expectNoMsg(300.millis)
      }
    }

    "it receives an outgoing Send message" must {
      "tell the transport" in new Fixture {
        implicit val coder = implicitly[Coder[StandardCommandWrapper.RequestPayload]]
        val payload = requestPayload

        hub ! SessionHub.SendIpmi(payload, version, sessionContext)

        transport.expectMsg(Transport.SendIpmi(payload, version, sessionContext))
      }
    }

    "a requester unregisters" must {
      "remove the requester from the internal map and acknowledge" in new Fixture {
        val requester = TestProbe()

        hub ! SessionHub.RegisterRequestHandler(requester.ref, SeqNo(1))
        expectMsg(SessionHub.RequestHandlerRegistered)

        val payload = responsePayload(SeqNo(1))
        hub ! Transport.ReceivedIpmi(payload.right, inSession = true)

        requester.expectMsg(SessionHub.ReceivedIpmi(payload.right))

        hub ! SessionHub.GetRequestHandlerCount
        expectMsg(SessionHub.RequestHandlerCount(1))

        hub ! SessionHub.UnregisterRequestHandler(requester.ref)
        expectMsg(SessionHub.RequestHandlerUnregistered)

        hub ! SessionHub.GetRequestHandlerCount
        expectMsg(SessionHub.RequestHandlerCount(0))
      }
    }
  }

  "a session hub" must {
    "forward SetSessionContext messages to the transport" in new Fixture {
      val msg = Transport.SetSessionContext(sessionContext)

      hub ! msg
      transport.expectMsg(msg)
      transport.lastSender shouldBe self
    }
  }

}
