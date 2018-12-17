package com.cyclone.ipmi.protocol

import akka.actor.{ActorContext, ActorRef, Status}
import akka.testkit.{ImplicitSender, TestProbe}
import com.cyclone.akka.{ActorSystemShutdown, TestKitSupport}
import com.cyclone.command.{OperationDeadline, TimeoutContext}
import com.cyclone.ipmi._
import com.cyclone.ipmi.command.global.WarmReset
import com.cyclone.ipmi.command.ipmiMessagingSupport.CloseSession
import com.cyclone.ipmi.protocol.SessionHub.SessionHubFactory
import com.cyclone.ipmi.protocol.Transport.Factory
import com.cyclone.ipmi.protocol.packet.SessionId.ManagedSystemSessionId
import com.cyclone.ipmi.protocol.packet.{IpmiVersion, SessionSequenceNumber}
import com.cyclone.ipmi.protocol.rakp.RmcpPlusAndRakpStatusCodeErrors
import com.cyclone.util.SynchronizedMockeryComponent
import com.google.common.net.InetAddresses
import org.scalatest.{Matchers, WordSpecLike}
import scalaz.Scalaz._

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Tests for [[SessionManager]]
  */
class SessionManagerTest
    extends TestKitSupport
    with WordSpecLike
    with Matchers
    with ImplicitSender
    with ActorSystemShutdown {

  class Fixture(inactivityTimeout: FiniteDuration = 1.minute)
      extends MockSessionNegotiatorComponent
      with MockRequesterFactoryComponent
      with SynchronizedMockeryComponent {

    val hub = TestProbe()

    val hubFactory = new SessionHubFactory {
      def createHub(ctx: ActorContext, sm: ActorRef, tf: Factory) = hub.ref
    }

    val versionReq = IpmiVersionRequirement.V15Only
    val version = IpmiVersion.V15

    val address = InetAddresses.forString("10.0.0.123")
    val port = 623
    val creds = IpmiCredentials("someUser", "somePassword")
    implicit val reqContext: TimeoutContext = TimeoutContext(OperationDeadline.fromNow(10.seconds))
    val priv = PrivilegeLevel.Operator

    val seqNoManagerFactory = TestProbe()
    val seqNoManager = TestProbe()

    val sessionManager = system.actorOf(
      SessionManager.props(
        seqNoManagerFactory.ref,
        address,
        port,
        inactivityTimeout,
        hubFactory,
        sessionNegotiator = sessionNegotiator,
        requesterFactory = requesterFactory
      )
    )

    val command = WarmReset.Command

    // WLOG
    val commandResult = WarmReset.CommandResult

    val sessionContext =
      V15SessionContext(ManagedSystemSessionId(123), initialSendSequenceNumber = SessionSequenceNumber(321))

    def willGetSeqNoManager(): Unit = {
      seqNoManagerFactory.expectMsg(SeqNoManagerFactory.GetSeqNoManagerFor((address, port), sessionManager))
      sessionManager ! SeqNoManagerFactory.SeqNoManager(seqNoManager.ref)
    }
  }

  "a session manager" when {
    "no session" must {
      "allow session negotiation" in new Fixture {
        willGetSeqNoManager()
        willNegotiateSession(Future.successful((sessionContext, version).right))
        sessionManager ! SessionManager.NegotiateSession(creds, versionReq, priv)
        expectMsg(SessionManager.SessionNegotiationSuccess)
      }

      "allow command execution without a session" in new Fixture {
        willGetSeqNoManager()
        willMakeRequest(command, version, SessionContext.NoSession, Future.successful(commandResult.right))
        sessionManager ! SessionManager.ExecuteCommand(command)
        expectMsg(SessionManager.CommandExecutionSuccess(commandResult))
      }

      "stop on closedown" in new Fixture {
        willGetSeqNoManager()
        watch(sessionManager)
        sessionManager ! SessionManager.Closedown
        expectMsg(SessionManager.ClosedDown)
        expectTerminated(sessionManager)
      }

      "automatically stop after a period of inactivity" in new Fixture(inactivityTimeout = 500.millis) {
        willGetSeqNoManager()
        watch(sessionManager)
        expectTerminated(sessionManager)
      }
    }

    "session negotiation fails with exception" must {
      "revert to no session state" in new Fixture {
        willGetSeqNoManager()
        val exception = new Exception("failed")
        willNegotiateSession(Future.failed(exception))
        sessionManager ! SessionManager.NegotiateSession(creds, versionReq, priv)
        expectMsg(SessionManager.SessionNegotiationFailure(exception))

        willNegotiateSession(Future.successful((sessionContext, version).right))
        sessionManager ! SessionManager.NegotiateSession(creds, versionReq, priv)
        hub.expectMsgType[Transport.SetSessionContext]
        sessionManager ! Transport.SetSessionContextAck
        expectMsg(SessionManager.SessionNegotiationSuccess)
      }
    }

    "session negotiation fails with error" must {
      "revert to no session state" in new Fixture {
        willGetSeqNoManager()
        willNegotiateSession(Future.successful(RmcpPlusAndRakpStatusCodeErrors.InvalidRole.left))
        sessionManager ! SessionManager.NegotiateSession(creds, versionReq, priv)
        expectMsg(SessionManager.SessionNegotiationError(RmcpPlusAndRakpStatusCodeErrors.InvalidRole))

        willNegotiateSession(Future.successful((sessionContext, version).right))
        sessionManager ! SessionManager.NegotiateSession(creds, versionReq, priv)
        hub.expectMsgType[Transport.SetSessionContext]
        sessionManager ! Transport.SetSessionContextAck
        expectMsg(SessionManager.SessionNegotiationSuccess)
      }
    }

    "a session is established" must {
      "immediately reject further session negotiation requests" in new Fixture {
        willGetSeqNoManager()
        willNegotiateSession(Future.successful((sessionContext, version).right))
        sessionManager ! SessionManager.NegotiateSession(creds, versionReq, priv)
        hub.expectMsgType[Transport.SetSessionContext]
        sessionManager ! Transport.SetSessionContextAck
        expectMsg(SessionManager.SessionNegotiationSuccess)

        sessionManager ! SessionManager.NegotiateSession(creds, versionReq, priv)
        expectMsgType[Status.Failure]
      }

      "allow command execution using the session" in new Fixture {
        willGetSeqNoManager()
        willNegotiateSession(Future.successful((sessionContext, version).right))
        sessionManager ! SessionManager.NegotiateSession(creds, versionReq, priv)
        hub.expectMsgType[Transport.SetSessionContext]
        sessionManager ! Transport.SetSessionContextAck
        expectMsg(SessionManager.SessionNegotiationSuccess)

        willMakeRequest(command, version, sessionContext, Future.successful(commandResult.right))
        sessionManager ! SessionManager.ExecuteCommand(command)
        expectMsg(SessionManager.CommandExecutionSuccess(commandResult))
      }

      "execute CloseSession and stop on closedown" in new Fixture {
        willGetSeqNoManager()
        watch(sessionManager)

        willNegotiateSession(Future.successful((sessionContext, version).right))
        sessionManager ! SessionManager.NegotiateSession(creds, versionReq, priv)
        hub.expectMsgType[Transport.SetSessionContext]
        sessionManager ! Transport.SetSessionContextAck
        expectMsg(SessionManager.SessionNegotiationSuccess)

        willMakeRequestWithContext(
          CloseSession.Command(sessionContext.managedSystemSessionId),
          SessionManager.ClosedowntimeoutContext,
          version,
          sessionContext,
          Future.successful(CloseSession.CommandResult.right)
        )

        sessionManager ! SessionManager.Closedown
        expectMsg(SessionManager.ClosedDown)
        expectTerminated(sessionManager)
      }

      "automatically close down and stop after a period of inactivity" in new Fixture(inactivityTimeout = 500.millis) {
        willGetSeqNoManager()
        watch(sessionManager)

        willNegotiateSession(Future.successful((sessionContext, version).right))
        sessionManager ! SessionManager.NegotiateSession(creds, versionReq, priv)
        hub.expectMsgType[Transport.SetSessionContext]
        sessionManager ! Transport.SetSessionContextAck
        expectMsg(SessionManager.SessionNegotiationSuccess)

        willMakeRequestWithContext(
          CloseSession.Command(sessionContext.managedSystemSessionId),
          SessionManager.ClosedowntimeoutContext,
          version,
          sessionContext,
          Future.successful(CloseSession.CommandResult.right)
        )

        expectTerminated(sessionManager)
      }
    }
  }
}
