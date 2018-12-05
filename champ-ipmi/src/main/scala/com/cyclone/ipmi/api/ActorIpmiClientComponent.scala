package com.cyclone.ipmi.api

import java.net.InetAddress

import akka.actor.ActorRef
import akka.pattern.{ask, AskTimeoutException}
import akka.util.Timeout
import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi._
import com.cyclone.ipmi.command.global.DeviceAddress
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import com.cyclone.ipmi.protocol.{IpmiManager, IpmiManagerComponent, SessionManager}
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * IpmiComponent that uses a global [[com.cyclone.ipmi.protocol.IpmiManager]] actor
  * and a [[com.cyclone.ipmi.protocol.SessionManager]] actor for each [[IpmiConnection]].
  */
trait ActorIpmiClientComponent extends IpmiClientComponent {
  self: IpmiManagerComponent =>

  lazy val ipmiClient: IpmiClient = new IpmiClient {

    def connectionFor(inetAddress: InetAddress, port: Int): Future[IpmiConnectionImpl] = {
      implicit val timeout: Timeout = Timeout(1.second)

      (ipmiManager ? IpmiManager.CreateSessionManagerFor(inetAddress, port))
        .mapTo[IpmiManager.SessionManagerCreated]
        .map(x => new IpmiConnectionImpl(x.actorRef))
    }
  }

  class IpmiConnectionImpl(sessionManager: ActorRef) extends IpmiConnection {

    def negotiateSession(
      ipmiCredentials: IpmiCredentials,
      versionRequirement: IpmiVersionRequirement,
      privilegeLevel: PrivilegeLevel
    )(implicit timeoutContext: TimeoutContext): Future[IpmiError \/ Unit] = {

      implicit val timeout: Timeout = timeoutContext.deadline.largerTimeout()

      (sessionManager ? SessionManager.NegotiateSession(
        ipmiCredentials,
        versionRequirement,
        privilegeLevel
      )).mapTo[SessionManager.SessionNegotiationResult]
        .flatMap {
          case SessionManager.SessionNegotiationSuccess    => Future.successful(().right)
          case SessionManager.SessionNegotiationError(e)   => Future.successful(e.left)
          case SessionManager.SessionNegotiationFailure(e) => Future.failed(e)
        }
        .recover {
          case _: AskTimeoutException => DeadlineReached.left
        }
    }

    def closedown(): Future[Unit] = {
      implicit val timeout: Timeout = Timeout(1.second)

      (sessionManager ? SessionManager.Closedown)
        .mapTo[SessionManager.ClosedDown.type]
        .map(_ => ())
    }

    def executeCommandOrError[Cmd <: IpmiStandardCommand, Res <: IpmiCommandResult](
      command: Cmd,
      targetAddress: DeviceAddress
    )(
      implicit timeoutContext: TimeoutContext,
      codec: CommandResultCodec[Cmd, Res]
    ): Future[IpmiError \/ Res] = {

      implicit val timeout: Timeout = timeoutContext.deadline.largerTimeout()

      (sessionManager ? SessionManager.ExecuteCommand(command, targetAddress))
        .mapTo[SessionManager.CommandExecutionResult]
        .flatMap {
          case SessionManager.CommandExecutionSuccess(r) => Future.successful(r.asInstanceOf[Res].right)
          case SessionManager.CommandExecutionError(e)   => Future.successful(e.left)
          case SessionManager.CommandExecutionFailure(e) => Future.failed(e)
        }
        .recover {
          case _: AskTimeoutException => DeadlineReached.left
        }
    }
  }

}
