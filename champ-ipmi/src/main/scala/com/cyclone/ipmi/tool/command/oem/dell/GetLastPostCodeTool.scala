package com.cyclone.ipmi.tool.command.oem.dell

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError
import com.cyclone.ipmi.command.oem.dell.GetLastPostCode
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.tool.command.{IpmiCommands, IpmiToolCommand, IpmiToolCommandResult}
import scalaz.EitherT._

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the Dell [[GetLastPostCode]] low-level command.
  */
object GetLastPostCodeTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] =
      new CommandExecutor[Command.type, Result] {

        def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
          import ctx._

          val result = for {
            cmdResult <- eitherT(connection.executeCommandOrError(GetLastPostCode.Command))
          } yield Result(cmdResult.postCode, cmdResult.postCodeString)

          result.run
        }
      }

    def description() = "dell get last-post-code"
  }

  case class Result(postCode: Byte, postCodeString: String) extends IpmiToolCommandResult

}
