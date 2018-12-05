package com.cyclone.ipmi.tool.command.oem.supermicro

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError
import com.cyclone.ipmi.command.oem.supermicro.GetExtraFirmwareInfo
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.tool.command.{IpmiCommands, IpmiToolCommand, IpmiToolCommandResult}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the Dell [[GetExtraFirmwareInfo]] low-level command.
  */
object GetExtraFirmwareInfoTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] =
      new CommandExecutor[Command.type, Result] {

        def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
          import ctx._

          val result = for {
            cmdResult <- eitherT(connection.executeCommandOrError(GetExtraFirmwareInfo.Command()))
          } yield
            Result(
              cmdResult.firmwareMajorVersion,
              cmdResult.firmwareMinorVersion,
              cmdResult.firmwareSubVersion,
              cmdResult.firmwareBuildNumber,
              cmdResult.hardwareId,
              cmdResult.firmwareTag
            )

          result.run
        }
      }

    def description() = "supermicro extra-firmware-info"
  }

  case class Result(
    firmwareMajorVersion: Int,
    firmwareMinorVersion: Int,
    firmwareSubVersion: Int,
    firmwareBuildNumber: Int,
    hardwareId: Byte,
    firmwareTag: String
  ) extends IpmiToolCommandResult

}
