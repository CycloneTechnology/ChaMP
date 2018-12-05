package com.cyclone.ipmi.tool.command.oem.fujitsu

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError
import com.cyclone.ipmi.api.IpmiConnection
import com.cyclone.ipmi.command.oem.fujitsu.s2s3.GetIdentifyLed
import com.cyclone.ipmi.command.oem.fujitsu.s2s3.GetIdentifyLed.OnOff
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.tool.command.{IpmiCommands, IpmiToolCommand, IpmiToolCommandResult}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the Fujitsu [[GetIdentifyLed]] low-level command.
  */
object GetIdentifyLedTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] = new CommandExecutor[Command.type, Result] {
      def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
        import ctx._

        val result = for {
          cmdResult <- eitherT(connection.executeCommandOrError(GetIdentifyLed.Command()))
        } yield Result(cmdResult.ledState)

        result.run
      }
    }

    def description() = "fujitsu get identify-led"
  }

  case class Result(ledState: OnOff) extends IpmiToolCommandResult

}
