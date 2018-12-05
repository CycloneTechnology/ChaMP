package com.cyclone.ipmi.tool.command

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.command.chassis.GetChassisStatus
import com.cyclone.ipmi.command.chassis.GetChassisStatus.{CurrentPowerState, FrontPanelButtonCapabilities, LastPowerEvent, MiscChassisState}
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.IpmiError
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the [[GetChassisStatus]] low-level command.
  */
object PowerStatusTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] = new CommandExecutor[Command.type, Result] {
      def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
        import ctx._

        val result = for {
          cmdResult <- eitherT(connection.executeCommandOrError(GetChassisStatus.Command))
        } yield Result(
          cmdResult.currentPowerState, cmdResult.lastPowerEvent, cmdResult.miscChassisState, cmdResult.frontPanelButtonCapabilities)

        result.run
      }
    }

    def description() = "power status"
  }

  case class Result(
    currentPowerState: CurrentPowerState,
    lastEvent: LastPowerEvent,
    miscState: MiscChassisState,
    frontPanelButtonCapabilities: Option[FrontPanelButtonCapabilities]) extends IpmiToolCommandResult

}
