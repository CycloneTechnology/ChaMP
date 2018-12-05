package com.cyclone.ipmi.tool.command

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.command.chassis.GetSystemRestartCause
import com.cyclone.ipmi.command.chassis.GetSystemRestartCause.RestartCause
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.IpmiError
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the [[GetSystemRestartCause]] low-level command.
  */
object ChassisRestartCauseTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] =
      new CommandExecutor[Command.type, Result] {

        def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
          import ctx._

          val result = for {
            cmdResult <- eitherT(connection.executeCommandOrError(GetSystemRestartCause.Command))
          } yield Result(cmdResult.restartCause, cmdResult.channelNumber)

          result.run
        }
      }

    def description() = "chassis restart_cause"
  }

  case class Result(restartCause: RestartCause, channelNumber: Byte) extends IpmiToolCommandResult

}
