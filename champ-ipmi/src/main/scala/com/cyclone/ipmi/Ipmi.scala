package com.cyclone.ipmi

import akka.actor.ActorSystem
import com.cyclone.akka.ActorSystemComponent
import com.cyclone.command.{OperationDeadline, TimeoutContext}
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.api.{ActorIpmiClientComponent, IpmiClientComponent}
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetChannelAuthenticationCapabilities
import com.cyclone.ipmi.protocol.ExtensionIpmiManagerComponent
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import com.cyclone.ipmi.tool.command.IpmiCommands.CommandExecutor
import com.cyclone.ipmi.tool.command.{IpmiCommands, IpmiToolCommand, IpmiToolCommandResult}
import com.cyclone.util.concurrent.Futures
import scalaz.EitherT._
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.control.NonFatal

/**
  * IPMI API.
  *
  * Allows executing [[IpmiStandardCommand]]s (defined in the IPMI Specification) along with higher level
  * [[IpmiToolCommand]]s (inspired by the popular ipmitool command line interface).
  *
  * @author Jeremy.Stone
  */
trait Ipmi {

  /**
    * Executes an [[IpmiStandardCommand]].
    *
    * Errors are converted to failed futures.
    */
  def executeCommand[Command <: IpmiStandardCommand, Result <: IpmiCommandResult](
    target: IpmiTarget,
    command: Command
  )(
    implicit timeoutContext: TimeoutContext,
    codec: CommandResultCodec[Command, Result]
  ): Future[Result] = {
    val raw = executeCommandOrError(target, command)

    Futures.disjunctionToFailedFuture(raw)(IpmiError.toThrowable)
  }

  /**
    * Executes an [[IpmiStandardCommand]] for a connection the result as a disjunction
    */
  def executeCommand[Command <: IpmiStandardCommand, Result <: IpmiCommandResult](command: Command)(
    implicit codec: CommandResultCodec[Command, Result],
    context: IpmiOperationContext
  ): Future[Result] = {
    val raw = executeCommandOrError(command)

    Futures.disjunctionToFailedFuture(raw)(IpmiError.toThrowable)
  }

  /**
    * Executes an [[IpmiStandardCommand]] returning the result as a disjunction
    */
  def executeCommandOrError[Command <: IpmiStandardCommand, Result <: IpmiCommandResult](
    target: IpmiTarget,
    command: Command
  )(
    implicit timeoutContext: TimeoutContext,
    codec: CommandResultCodec[Command, Result]
  ): Future[IpmiErrorOr[Result]]

  /**
    * Executes an [[IpmiStandardCommand]]
    *
    * Errors are converted to failed futures.
    */
  def executeCommandOrError[Command <: IpmiStandardCommand, Result <: IpmiCommandResult](
    command: Command
  )(
    implicit codec: CommandResultCodec[Command, Result],
    context: IpmiOperationContext
  ): Future[IpmiErrorOr[Result]]

  /**
    * Executes an [[IpmiToolCommand]].
    *
    * Errors are converted to failed futures.
    */
  def executeToolCommand[Command <: IpmiToolCommand, Result <: IpmiToolCommandResult](
    target: IpmiTarget,
    command: Command
  )(
    implicit timeoutContext: TimeoutContext,
    executor: CommandExecutor[Command, Result]
  ): Future[Result] = {
    val raw = executeToolCommandOrError(target, command)

    Futures.disjunctionToFailedFuture(raw)(IpmiError.toThrowable)
  }

  /**
    * Executes an [[IpmiToolCommand]] for a connection the result as a disjunction
    */
  def executeToolCommand[Command <: IpmiToolCommand, Result <: IpmiToolCommandResult](command: Command)(
    implicit executor: CommandExecutor[Command, Result],
    context: IpmiOperationContext
  ): Future[Result] = {
    val raw = executeToolCommandOrError(command)

    Futures.disjunctionToFailedFuture(raw)(IpmiError.toThrowable)
  }

  /**
    * Executes an [[IpmiToolCommand]] returning the result as a disjunction
    */
  def executeToolCommandOrError[Command <: IpmiToolCommand, Result <: IpmiToolCommandResult](
    target: IpmiTarget,
    command: Command
  )(
    implicit timeoutContext: TimeoutContext,
    executor: CommandExecutor[Command, Result]
  ): Future[IpmiErrorOr[Result]]

  /**
    * Executes an [[IpmiToolCommand]]
    *
    * Errors are converted to failed futures.
    */
  def executeToolCommandOrError[Command <: IpmiToolCommand, Result <: IpmiToolCommandResult](
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
    * Tests IPMI support for an address (ignores credentials).
    */
  def testSupport(target: IpmiTarget, timeout: FiniteDuration): Future[Boolean]

  /**
    * Tests whether an authenticated session can be negotiated for an address
    * based on the credentials in the target.
    */
  def testNegotiateSession(target: IpmiTarget, timeout: FiniteDuration): Future[IpmiErrorOr[Unit]]
}

object Ipmi {

  /**
    * Creates an [[Ipmi]].
    *
    * The resulting object can be used for multiple command executions etc.
    *
    * @param system an actor system
    */
  def create(implicit system: ActorSystem): Ipmi = {
    val component = new DefaultIpmiComponent with ActorIpmiClientComponent with ExtensionIpmiManagerComponent
    with ActorSystemComponent {
      implicit def actorSystem: ActorSystem = system
    }

    component.ipmi
  }
}

trait IpmiComponent {
  def ipmi: Ipmi
}

trait DefaultIpmiComponent extends IpmiComponent {
  self: IpmiClientComponent =>

  lazy val ipmi: Ipmi = new Ipmi {

    def executeCommandOrError[Command <: IpmiStandardCommand, Result <: IpmiCommandResult](
      target: IpmiTarget,
      command: Command
    )(
      implicit timeoutContext: TimeoutContext,
      codec: CommandResultCodec[Command, Result]
    ): Future[IpmiError.IpmiErrorOr[Result]] =
      withContextOrError(target)(implicit context => context.connection.executeCommandOrError(command))

    def executeCommandOrError[Command <: IpmiStandardCommand, Result <: IpmiCommandResult](command: Command)(
      implicit codec: CommandResultCodec[Command, Result],
      context: IpmiOperationContext
    ): Future[IpmiErrorOr[Result]] = {
      implicit val to: TimeoutContext = context.timeoutContext
      context.connection.executeCommandOrError(command)
    }

    def executeToolCommandOrError[Command <: IpmiToolCommand, Res <: IpmiToolCommandResult](
      target: IpmiTarget,
      command: Command
    )(
      implicit timeoutContext: TimeoutContext,
      executor: CommandExecutor[Command, Res]
    ): Future[IpmiErrorOr[Res]] = withContextOrError(target)(implicit context => executor.execute(command))

    def executeToolCommandOrError[Command <: IpmiToolCommand, Result <: IpmiToolCommandResult](
      command: Command
    )(
      implicit executor: IpmiCommands.CommandExecutor[Command, Result],
      context: IpmiOperationContext
    ): Future[IpmiErrorOr[Result]] = executor.execute(command)

    def withContextOrError[T](target: IpmiTarget)(
      operation: IpmiOperationContext => Future[IpmiErrorOr[T]]
    )(implicit timeoutContext: TimeoutContext): Future[IpmiErrorOr[T]] = {
      target match {
        case lan: IpmiTarget.LAN =>
          import lan._
          val futureConnection = ipmiClient.connectionFor(inetAddress, port)

          val result = for {
            connection <- rightT(futureConnection)
            _          <- eitherT(connection.negotiateSession(credentials, versionRequirement, privilegeLevel))
            result     <- eitherT(operation(IpmiOperationContext(connection, timeoutContext)))
          } yield result

          result.run.andThen {
            case _ => futureConnection.foreach(_.closedown())
          }
      }
    }

    def testSupport(target: IpmiTarget, timeout: FiniteDuration): Future[Boolean] = {
      implicit val timeoutContext: TimeoutContext = TimeoutContext(OperationDeadline.fromNow(timeout))

      target match {
        case lan: IpmiTarget.LAN =>
          import lan._

          ipmiClient.withConnection(inetAddress, port) { connection =>
            // Spec says can use GetChannelAuthenticationCapabilities to discover support...
            val result =
              connection.executeCommandOrError(GetChannelAuthenticationCapabilities.Command(PrivilegeLevel.User))

            // Don't care what the result is just whether we get a good one...
            result.map(_.isRight).recover { case NonFatal(_) => false }
          }
      }
    }

    def testNegotiateSession(
      target: IpmiTarget,
      timeout: FiniteDuration
    ): Future[IpmiErrorOr[Unit]] = {
      implicit val timeoutContext: TimeoutContext = TimeoutContext(OperationDeadline.fromNow(timeout))

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
