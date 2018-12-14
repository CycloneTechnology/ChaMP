package com.cyclone.ipmi.client

import java.net.InetAddress

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi._
import com.cyclone.ipmi.command.global.DeviceAddress
import com.cyclone.ipmi.command.ipmiMessagingSupport.SetSessionPrivilegeLevel
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import com.cyclone.util.concurrent.Futures
import scalaz.EitherT._
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Low-level IMPI client entry point api.
  */
trait IpmiClient {

  /**
    * Creates a logged out [[IpmiConnection]] for interacting with an IPMI BMC device
    * on the specified address and port.
    */
  def connectionFor(
    inetAddress: InetAddress,
    port: Int = IpmiTarget.defaultPort
  ): Future[IpmiConnection]

  /**
    * Utility to perform a task with a new connection, closing it afterwards
    */
  def withConnection[T](inetAddress: InetAddress, port: Int = IpmiTarget.defaultPort)(
    task: IpmiConnection => Future[T]
  ): Future[T] = {
    val futureConnection = connectionFor(inetAddress, port)

    val result = for {
      connection <- futureConnection
      result     <- task(connection)
    } yield result

    result.andThen {
      case _ => futureConnection.foreach(_.closedown())
    }
  }
}

trait IpmiClientComponent {
  def ipmiClient: IpmiClient
}

/**
  * Representation of an IPMI 'connection' to a single device.
  */
trait IpmiConnection {

  /**
    * Negotiates a session satisfying version and privilege level constraints.
    */
  def negotiateSession(
    ipmiCredentials: IpmiCredentials,
    versionRequirement: IpmiVersionRequirement,
    privilegeLevel: PrivilegeLevel = PrivilegeLevel.User
  )(implicit timeoutContext: TimeoutContext): Future[IpmiErrorOr[Unit]]

  /**
    * Elevates privileges to the required level
    *
    * @param privilegeLevel the new level
    * @return the new level if a level was set or an error
    */
  def elevatePrivilegeLevel(
    privilegeLevel: PrivilegeLevel
  )(implicit timeoutContext: TimeoutContext): Future[IpmiErrorOr[Option[PrivilegeLevel]]] =
    // user level anyway by default
    if (privilegeLevel != PrivilegeLevel.User) {
      val result = for {
        cmdRes <- eitherT(executeCommandOrError(SetSessionPrivilegeLevel.Command(privilegeLevel)))
      } yield Some(cmdRes.newPrivilegeLevel)

      result.run
    } else
      None.right.point[Future]

  /**
    * The connection should be closed down when it is finished with.
    */
  def closedown(): Future[Unit]

  /**
    * If a session has been negotiated, executes the command within that session using the negotiated IPMI version.
    *
    * If no session has been negotiated, attempts to execute the command outside of the session using
    * IMPI V1.5. Only certain commands can be run without the privileges provided with a session.
    *
    * The command will be executed using IPMI V1.5 protocol.
    *
    * Errors are converted to failed futures.
    */
  def executeCommand[Command <: IpmiStandardCommand, Result <: IpmiCommandResult](
    command: Command,
    targetAddress: DeviceAddress
  )(
    implicit timeoutContext: TimeoutContext,
    codec: CommandResultCodec[Command, Result]
  ): Future[Result] = {
    val raw = executeCommandOrError(command, targetAddress)

    Futures.disjunctionToFailedFuture(raw)(IpmiError.toThrowable)
  }

  def executeCommand[Command <: IpmiStandardCommand, Result <: IpmiCommandResult](
    command: Command
  )(implicit timeoutContext: TimeoutContext, codec: CommandResultCodec[Command, Result]): Future[Result] =
    executeCommand(command, DeviceAddress.BmcAddress)

  /**
    * If a session has been negotiated, executes the command within that session using the negotiated IPMI version.
    *
    * If no session has been negotiated, attempts to execute the command outside of the session using
    * IMPI V1.5. Only certain commands can be run without the privileges provided with a session.
    *
    * The command will be executed using IPMI V1.5 protocol.
    */
  def executeCommandOrError[Command <: IpmiStandardCommand, Result <: IpmiCommandResult](
    command: Command,
    targetAddress: DeviceAddress
  )(
    implicit timeoutContext: TimeoutContext,
    codec: CommandResultCodec[Command, Result]
  ): Future[IpmiErrorOr[Result]]

  def executeCommandOrError[Command <: IpmiStandardCommand, Result <: IpmiCommandResult](
    command: Command
  )(
    implicit timeoutContext: TimeoutContext,
    codec: CommandResultCodec[Command, Result]
  ): Future[IpmiErrorOr[Result]] =
    executeCommandOrError(command, DeviceAddress.BmcAddress)
}
