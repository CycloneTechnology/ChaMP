package com.cyclone.ipmi.tool.command.oem.sunoracle

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError
import com.cyclone.ipmi.api.IpmiConnection
import com.cyclone.ipmi.command.oem.sunoracle.GetVersion
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.tool.command.{IpmiCommands, IpmiToolCommand, IpmiToolCommandResult}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the Sun/Oracle [[GetVersion]] low-level command.
  */
object GetVersionTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] =
      new CommandExecutor[Command.type, Result] {

        def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
          import ctx._

          val result = for {
            cmdResult <- eitherT(connection.executeCommandOrError(GetVersion.Command))
          } yield Result(cmdResult.cpldVersion, cmdResult.ipmiFirmwareRev)

          result.run
        }
      }

    def description() = "sun get version"
  }

  case class Result(cpldVersion: Byte, ipmiFirmwareRev: String) extends IpmiToolCommandResult

}
