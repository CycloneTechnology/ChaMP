package com.cyclone.ipmi.protocol

import java.net.InetAddress

import akka.actor.{Actor, ActorLogging, ActorRef, Props, ReceiveTimeout, Stash, Status}
import akka.event.LoggingReceive
import akka.pattern.pipe
import com.cyclone.command.{OperationDeadline, RequestTimeouts, TimeoutContext}
import com.cyclone.ipmi._
import com.cyclone.ipmi.command.global.DeviceAddress
import com.cyclone.ipmi.command.ipmiMessagingSupport.CloseSession
import com.cyclone.ipmi.protocol.SessionHub.{DefaultSessionHubFactory, SessionHubFactory}
import com.cyclone.ipmi.protocol.SessionManager._
import com.cyclone.ipmi.protocol.packet.SessionId.RemoteConsoleSessionId
import com.cyclone.ipmi.protocol.packet._
import scalaz.{-\/, \/-}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.control.NonFatal
import scala.util.{Failure, Random, Success}

object SessionManager {

  def props(
    seqNoManagerFactory: ActorRef,
    address: InetAddress,
    port: Int,
    inactivityTimeout: FiniteDuration = 1.minute,
    hubFactory: SessionHubFactory = DefaultSessionHubFactory,
    sessionNegotiator: SessionNegotiator = DefaultSessionNegotiator,
    requesterFactory: RequesterFactory = RequestHandlerRequesterFactory
  ) =
    Props(
      new SessionManager(
        seqNoManagerFactory,
        address,
        port,
        inactivityTimeout,
        hubFactory,
        sessionNegotiator,
        requesterFactory
      )
    )

  case class NegotiateSession(
    ipmiCredentials: IpmiCredentials,
    versionRequirement: IpmiVersionRequirement,
    privilegeLevel: PrivilegeLevel = PrivilegeLevel.User
  )(implicit val timeoutContext: TimeoutContext)

  sealed trait SessionNegotiationResult

  case object SessionNegotiationSuccess extends SessionNegotiationResult

  case class SessionNegotiationError(error: IpmiError) extends SessionNegotiationResult

  case class SessionNegotiationFailure(e: Throwable) extends SessionNegotiationResult

  case object Closedown

  case object ClosedDown

  case class ExecuteCommand[Cmd <: IpmiStandardCommand, Res <: IpmiCommandResult](
    command: Cmd,
    targetAddress: DeviceAddress = DeviceAddress.BmcAddress
  )(implicit val timeoutContext: TimeoutContext, val codec: CommandResultCodec[Cmd, Res])

  sealed trait CommandExecutionResult

  case class CommandExecutionSuccess(result: IpmiCommandResult) extends CommandExecutionResult

  case class CommandExecutionError(error: IpmiError) extends CommandExecutionResult

  case class CommandExecutionFailure(e: Throwable) extends CommandExecutionResult

  private case class SessionNegotiationComplete(
    sessionContext: SessionContext,
    version: IpmiVersion
  )

  private case object SessionNegotiationFailed

  private[protocol] val ClosedowntimeoutContext =
    TimeoutContext(OperationDeadline.fromNow(5.second), RequestTimeouts.simple())
}

/**
  * Represents and coordinates a user session.
  */
class SessionManager(
  seqNoManagerFactory: ActorRef,
  address: InetAddress,
  port: Int,
  inactivityTimeout: FiniteDuration,
  hubFactory: SessionHubFactory,
  sessionNegotiator: SessionNegotiator,
  requesterFactory: RequesterFactory
) extends Actor
    with Stash
    with ActorLogging {

  val remoteConsoleSessionId = RemoteConsoleSessionId(new Random().nextInt())

  val hub: ActorRef =
    hubFactory.createHub(context, self, UDPTransport.factory(context, address, port))

  context.setReceiveTimeout(inactivityTimeout)

  override def preStart(): Unit = {
    super.preStart()

    // Have a separate seqNoManager for each device but shared by
    // other session managers for the same device so that seqNos are not duplicated
    // for an in-progress request...
    seqNoManagerFactory ! SeqNoManagerFactory.GetSeqNoManagerFor((address, port), self)
  }

  def receive: Receive = awaitingSeqNoManager

  def awaitingSeqNoManager: Receive = LoggingReceive.withLabel("awaiting seq no manager") {
    case SeqNoManagerFactory.SeqNoManager(seqNoManager) =>
      val requester = requesterFactory.requester(
        context,
        hub = hub,
        seqNoManager = seqNoManager
      )

      context become noSession(requester)
      unstashAll()

    case _ => stash()
  }

  def noSession(requester: Requester): Receive =
    LoggingReceive.withLabel("No session") {
      case ns @ NegotiateSession(credentials, versionRequirement, privilegeLevel) =>
        implicit val rc: TimeoutContext = ns.timeoutContext
        val client = sender

        val futureNegotiation =
          sessionNegotiator.negotiateSession(
            versionRequirement,
            remoteConsoleSessionId,
            credentials,
            privilegeLevel,
            requester
          )

        futureNegotiation.onComplete {
          case Success(\/-((sessionContext, version))) =>
            client ! SessionNegotiationSuccess
            self ! SessionNegotiationComplete(sessionContext, version)

          case Success(-\/(e)) =>
            client ! SessionNegotiationError(e)
            self ! SessionNegotiationFailed

          case Failure(t) =>
            client ! SessionNegotiationFailure(t)
            self ! SessionNegotiationFailed
        }

        context become negotiatingSession(requester)

      case Closedown =>
        context become closingDown(sender)
        self ! ClosedDown

    } orElse handleExecutionRequests(requester, SessionContext.NoSession, IpmiVersion.V15) orElse closedownIfInactive

  def negotiatingSession(requester: Requester): Receive =
    LoggingReceive.withLabel("Negotiating session") {
      case SessionNegotiationComplete(sessionContext, version) =>
        hub ! Transport.SetSessionContext(sessionContext)
        context become awaitingSetSessionContextAck(requester, sessionContext, version)
        unstashAll()

      case SessionNegotiationFailed =>
        context become noSession(requester)
        unstashAll()

      case _ => stash()
    }

  def awaitingSetSessionContextAck(
    requester: Requester,
    sessionContext: SessionContext,
    version: IpmiVersion
  ): Receive =
    LoggingReceive.withLabel("Awaiting SetSessionContextAck") {
      case Transport.SetSessionContextAck =>
        context become sessionActive(requester, sessionContext, version)
        unstashAll()

      case _ => stash()
    }

  def sessionActive(
    requester: Requester,
    sessionContext: SessionContext,
    version: IpmiVersion
  ): Receive =
    LoggingReceive.withLabel("Session active") {
      case Closedown =>
        implicit val rc: TimeoutContext = ClosedowntimeoutContext

        val futClose = requester.makeRequest(
          CloseSession.Command(sessionContext.managedSystemSessionId),
          version,
          sessionContext,
          DeviceAddress.BmcAddress
        )

        context become closingDown(sender)

        futClose.onComplete { t =>
          log.debug("CloseSession result {}", t)
          self ! ClosedDown
        }

      case _: NegotiateSession =>
        sender() ! Status.Failure(new IllegalStateException("Session already active"))

    } orElse handleExecutionRequests(requester, sessionContext, version) orElse closedownIfInactive

  def closingDown(client: ActorRef): Receive = LoggingReceive.withLabel("Closing down") {
    case ClosedDown =>
      client ! ClosedDown
      context stop self
  }

  def handleExecutionRequests(
    requester: Requester,
    sessionContext: SessionContext,
    version: IpmiVersion
  ): Receive = LoggingReceive {
    case mr @ ExecuteCommand(request, targetAddress) =>
      implicit val rc: TimeoutContext = mr.timeoutContext
      implicit val codec: CommandResultCodec[IpmiStandardCommand, IpmiCommandResult] = mr.codec
      requester
        .makeRequest(request, version, sessionContext, targetAddress)
        .map {
          case \/-(result) => CommandExecutionSuccess(result)
          case -\/(e)      => CommandExecutionError(e)
        }
        .recover {
          case NonFatal(t) => CommandExecutionFailure(t)
        }
        .pipeTo(sender())
  }

  def closedownIfInactive: Receive = LoggingReceive {
    case ReceiveTimeout =>
      context.setReceiveTimeout(Duration.Undefined)
      self ! Closedown
  }
}
