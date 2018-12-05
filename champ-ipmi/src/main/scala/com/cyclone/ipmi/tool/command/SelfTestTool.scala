package com.cyclone.ipmi.tool.command

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.command.global.GetSelfTestResults
import com.cyclone.ipmi.command.global.GetSelfTestResults.SelfTestStatus
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.IpmiError
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the [[GetSelfTestResults]] low-level command.
  */
object SelfTestTool {

  case object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] = new CommandExecutor[Command.type, Result] {
      def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
        import ctx._

        val result = for {
          cmdResult <- eitherT(connection.executeCommandOrError(GetSelfTestResults.Command))
        } yield Result(cmdResult.selfTestStatus)

        result.run
      }
    }

    def description() = "chassis selftest"
  }

  case class Result(selfTestStatus: SelfTestStatus) extends IpmiToolCommandResult

}
