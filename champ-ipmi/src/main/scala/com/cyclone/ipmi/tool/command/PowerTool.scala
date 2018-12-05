package com.cyclone.ipmi.tool.command

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.command.chassis.ChassisControl
import com.cyclone.ipmi.command.chassis.ChassisControl.Control
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.IpmiError
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait Power

object Power {

  case object on extends Power

  case object off extends Power

  case object cycle extends Power

  case object reset extends Power

  case object diag extends Power

  case object soft extends Power

}

/**
  * [[IpmiToolCommand]] that wraps the [[ChassisControl]] low-level command.
  */
object PowerTool {

  object Command {
    implicit val executor: CommandExecutor[Command, PowerSet.type] =
      new CommandExecutor[Command, PowerSet.type] {

        def execute(
          command: Command
        )(implicit ctx: Ctx): Future[IpmiError \/ PowerTool.PowerSet.type] = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
          import ctx._

          def exec = command.power match {
            case Power.on =>
              connection.executeCommandOrError(ChassisControl.Command(Control.PowerUp))
            case Power.off =>
              connection.executeCommandOrError(ChassisControl.Command(Control.PowerDown))
            case Power.cycle =>
              connection.executeCommandOrError(ChassisControl.Command(Control.PowerCycle))
            case Power.reset =>
              connection.executeCommandOrError(ChassisControl.Command(Control.HardReset))
            case Power.diag =>
              connection.executeCommandOrError(
                ChassisControl.Command(Control.PulseDiagnosticInterrupt)
              )
            case Power.soft =>
              connection.executeCommandOrError(
                ChassisControl.Command(Control.EmulateFatalOverTemperature)
              )
          }

          val result = for {
            _ <- eitherT(exec)
          } yield PowerSet

          result.run
        }
      }
  }

  case class Command(power: Power) extends IpmiToolCommand {

    def description(): String = {
      s"power $power"
    }
  }

  case object PowerSet extends IpmiToolCommandResult

}
