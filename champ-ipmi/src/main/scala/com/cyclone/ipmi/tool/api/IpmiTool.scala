package com.cyclone.ipmi.tool.api

import akka.actor.ActorSystem
import com.cyclone.akka.ActorSystemComponent
import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi._
import com.cyclone.ipmi.api.{ActorIpmiClientComponent, IpmiClientComponent}
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetChannelAuthenticationCapabilities
import com.cyclone.ipmi.protocol.ExtensionIpmiManagerComponent
import com.cyclone.ipmi.tool.command.IpmiCommands._
import com.cyclone.ipmi.tool.command.{IpmiCommands, IpmiToolCommand, IpmiToolCommandResult}
import com.cyclone.util.OperationDeadline
import com.cyclone.util.concurrent.Futures
import scalaz.EitherT._
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.control.NonFatal

/**
  * High-level command API for IPMI.
  */
trait IpmiTool {

  /**
    * Executes [[IpmiToolCommand]].
    *
    * Errors are converted to failed futures.
    */
  def executeCommand[Command <: IpmiToolCommand, Result <: IpmiToolCommandResult](
    target: IpmiTarget,
    command: Command
  )(
    implicit timeoutContext: TimeoutContext,
    executor: CommandExecutor[Command, Result]
  ): Future[Result] = {
    val raw = executeCommandOrError(target, command)

    Futures.disjunctionToFailedFuture(raw)(IpmiError.toThrowable)
  }

  /**
    * Executes [[com.cyclone.ipmi.tool.command.IpmiToolCommand]] for a connection the result as a disjunction
    */
  def executeCommand[Command <: IpmiToolCommand, Result <: IpmiToolCommandResult](command: Command)(
    implicit executor: CommandExecutor[Command, Result],
    context: IpmiOperationContext
  ): Future[Result] = {
    val raw = executeCommandOrError(command)

    Futures.disjunctionToFailedFuture(raw)(IpmiError.toThrowable)
  }

  /**
    * Executes [[com.cyclone.ipmi.tool.command.IpmiToolCommand]] returning the result as a disjunction
    */
  def executeCommandOrError[Command <: IpmiToolCommand, Result <: IpmiToolCommandResult](
    target: IpmiTarget,
    command: Command
  )(
    implicit timeoutContext: TimeoutContext,
    executor: CommandExecutor[Command, Result]
  ): Future[IpmiErrorOr[Result]]

  /**
    * Executes [[com.cyclone.ipmi.tool.command.IpmiToolCommand]]
    *
    * Errors are converted to failed futures.
    */
  def executeCommandOrError[Command <: IpmiToolCommand, Result <: IpmiToolCommandResult](
    command: Command
  )(
    implicit executor: CommandExecutor[Command, Result],
    context: IpmiOperationContext
  ): Future[IpmiErrorOr[Result]]

  /**
    * Utility to perform some operation (e.q. sequence of commands) with the same context.
    */
  def withContextOrError[T](target: IpmiTarget)(
    operation: IpmiOperationContext => Future[IpmiErrorOr[T]]
  )(implicit timeoutContext: TimeoutContext): Future[IpmiErrorOr[T]]

  /**
    * Utility to perform some operation (e.q. sequence of commands) with the same context.
    */
  def withContext[T](target: IpmiTarget)(
    operation: IpmiOperationContext => Future[T]
  )(implicit timeoutContext: TimeoutContext): Future[T] = {
    val raw = withContextOrError(target)(ctx => rightT(operation(ctx)).run)

    Futures.disjunctionToFailedFuture(raw)(IpmiError.toThrowable)
  }

  /**
    * Tests IPMI support for an address.
    */
  def testSupport(target: IpmiTarget, timeout: FiniteDuration): Future[Boolean]

  /**
    * Tests whether an authenticated session can be negotiated for an address.
    */
  def testNegotiateSession(target: IpmiTarget, timeout: FiniteDuration): Future[IpmiErrorOr[Unit]]
}

object IpmiTool {

  def create(implicit system: ActorSystem): IpmiTool = {
    val component = new DefaultIpmiToolComponent with ActorIpmiClientComponent with ExtensionIpmiManagerComponent
    with ActorSystemComponent {
      implicit def actorSystem: ActorSystem = system
    }

    component.ipmiTool
  }
}

trait IpmiToolComponent {
  def ipmiTool: IpmiTool
}

trait DefaultIpmiToolComponent extends IpmiToolComponent {
  self: IpmiClientComponent =>

  lazy val ipmiTool: IpmiTool = new IpmiTool {

    def withContextOrError[T](target: IpmiTarget)(
      operation: IpmiOperationContext => Future[IpmiErrorOr[T]]
    )(implicit timeoutContext: TimeoutContext): Future[IpmiErrorOr[T]] = {
      target match {
        case lan: IpmiTarget.LAN =>
          import lan._
          val futureConnection = ipmiClient.connectionFor(inetAddress, port)

          val result = for {
            connection <- rightT(futureConnection)
            _ <- eitherT(
              connection.negotiateSession(credentials, versionRequirement, privilegeLevel)
            )
            result <- eitherT(operation(IpmiOperationContext(connection, timeoutContext)))
          } yield result

          result.run.andThen {
            case _ => futureConnection.foreach(_.closedown())
          }
      }
    }

    def executeCommandOrError[Command <: IpmiToolCommand, Res <: IpmiToolCommandResult](
      target: IpmiTarget,
      command: Command
    )(
      implicit timeoutContext: TimeoutContext,
      executor: CommandExecutor[Command, Res]
    ): Future[IpmiErrorOr[Res]] = {

      withContextOrError(target) { implicit context =>
        executor.execute(command)
      }
    }

    def executeCommandOrError[Command <: IpmiToolCommand, Result <: IpmiToolCommandResult](
      command: Command
    )(
      implicit executor: IpmiCommands.CommandExecutor[Command, Result],
      context: IpmiOperationContext
    ): Future[IpmiErrorOr[Result]] =
      executor.execute(command)

    def testSupport(target: IpmiTarget, timeout: FiniteDuration): Future[Boolean] = {
      implicit val timeoutContext: TimeoutContext = TimeoutContext(
        OperationDeadline.fromNow(timeout)
      )

      target match {
        case lan: IpmiTarget.LAN =>
          import lan._

          ipmiClient.withConnection(inetAddress, port) { connection =>
            // Spec says can use GetChannelAuthenticationCapabilities to discover support...
            val result = connection.executeCommandOrError(
              GetChannelAuthenticationCapabilities.Command(PrivilegeLevel.User)
            )

            // Don't care what the result is just whether we get a good one...
            result.map(_.isRight).recover { case NonFatal(_) => false }
          }
      }
    }

    def testNegotiateSession(
      target: IpmiTarget,
      timeout: FiniteDuration
    ): Future[IpmiErrorOr[Unit]] = {
      implicit val timeoutContext: TimeoutContext = TimeoutContext(
        OperationDeadline.fromNow(timeout)
      )

      target match {
        case lan: IpmiTarget.LAN =>
          import lan._

          ipmiClient.withConnection(inetAddress, port) { connection =>
            val result =
              connection.negotiateSession(credentials, versionRequirement, PrivilegeLevel.User)

            result.recover { case NonFatal(e) => IpmiExceptionError(e).left }
          }
      }
    }
  }
}
