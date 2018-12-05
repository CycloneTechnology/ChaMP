package com.cyclone.ipmi.tool.command.oem.dell

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.command.oem.dell.{GetNicSelection, NicSelection}
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.tool.command.{IpmiToolCommand, IpmiToolCommandResult}
import com.cyclone.ipmi.IpmiError
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the Dell [[GetNicSelection]] low-level command.
  */
object GetNicSelectionTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] =
      new CommandExecutor[Command.type, Result] {

        def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
          import ctx._

          val result = for {
            cmdResult <- eitherT(connection.executeCommandOrError(GetNicSelection.Command()))
          } yield Result(cmdResult.nicSelection)

          result.run
        }
      }

    def description() = "dell get nic-selection"
  }

  case class Result(nicSelection: NicSelection) extends IpmiToolCommandResult

}
