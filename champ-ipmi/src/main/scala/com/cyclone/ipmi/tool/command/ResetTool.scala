package com.cyclone.ipmi.tool.command

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.command.global.{ColdReset, WarmReset}
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.IpmiError
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait ResetType

object ResetType {

  case object warm extends ResetType

  case object cold extends ResetType

}

/**
  * [[IpmiToolCommand]] that wraps the [[WarmReset]] and  [[ColdReset]] low-level commands.
  */
object ResetTool {

  object Command {
    implicit val executor: CommandExecutor[Command, Reset.type] = new CommandExecutor[Command, Reset.type] {
      def execute(command: Command)(implicit ctx: Ctx): Future[IpmiError \/ Reset.type] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
        import ctx._

        def exec = command.resetType match {
          case ResetType.cold => connection.executeCommandOrError(ColdReset.Command)
          case ResetType.warm => connection.executeCommandOrError(WarmReset.Command)
        }

        val result = for {
          _ <- eitherT(exec)
        } yield Reset

        result.run
      }
    }
  }

  case class Command(resetType: ResetType) extends IpmiToolCommand {
    def description() = s"bmc reset $resetType"
  }

  case object Reset extends IpmiToolCommandResult

}
