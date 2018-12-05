package com.cyclone.ipmi.tool.command

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.command.chassis.GetPohCounter
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.IpmiError
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the [[GetPohCounter]] low-level command.
  */
object PohCounterTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] = new CommandExecutor[Command.type, Result] {
      def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
        import ctx._

        val result = for {
          cmdResult <- eitherT(connection.executeCommandOrError(GetPohCounter.Command))
        } yield Result(
          cmdResult.powerOnTime.toHours)

        result.run
      }
    }

    def description() = "chassis poh"
  }

  case class Result(
    powerOnTime: Long) extends IpmiToolCommandResult

}
