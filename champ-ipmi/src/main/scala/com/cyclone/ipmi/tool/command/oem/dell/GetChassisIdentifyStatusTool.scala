package com.cyclone.ipmi.tool.command.oem.dell

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.command.oem.dell.GetChassisIdentifyStatus
import com.cyclone.ipmi.command.oem.dell.GetChassisIdentifyStatus.IdentifyStatus
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.tool.command.{IpmiToolCommand, IpmiToolCommandResult}
import com.cyclone.ipmi.IpmiError
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the Dell [[GetChassisIdentifyStatus]] low-level command.
  */
object GetChassisIdentifyStatusTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] = new CommandExecutor[Command.type, Result] {
      def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
        import ctx._

        val result = for {
          cmdResult <- eitherT(connection.executeCommandOrError(GetChassisIdentifyStatus.Command()))
        } yield Result(cmdResult.identifyStatus)

        result.run
      }
    }

    def description() = "dell get chassis-identify-status"
  }

  case class Result(identifyStatus: IdentifyStatus) extends IpmiToolCommandResult

}
