package com.cyclone.ipmi.tool.command

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError
import com.cyclone.ipmi.command.global.DeviceGuid
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetDeviceGuid
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the [[GetDeviceGuid]] low-level command.
  */
object BmcGetGuidTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] = new CommandExecutor[Command.type, Result] {
      def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
        import ctx._

        val result = for {
          cmdResult <- eitherT(connection.executeCommandOrError(GetDeviceGuid.Command))
        } yield Result(cmdResult.guid)

        result.run
      }
    }

    def description() = "bmc guid"
  }

  case class Result(guid: DeviceGuid) extends IpmiToolCommandResult

}
