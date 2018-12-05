package com.cyclone.ipmi.tool.command

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError
import com.cyclone.ipmi.command.chassis.SetPowerRestorePolicy
import com.cyclone.ipmi.command.chassis.SetPowerRestorePolicy._
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.Future

sealed trait ChassisPolicy

object ChassisPolicy {

  case object list extends ChassisPolicy

  case object `always-on` extends ChassisPolicy

  case object previous extends ChassisPolicy

  case object `always-off` extends ChassisPolicy

}

/**
  * [[IpmiToolCommand]] that wraps the [[SetPowerRestorePolicy]] low-level command.
  */
object ChassisPowerPolicyTool {

  object Command {
    implicit val executor: CommandExecutor[Command, Result] = new CommandExecutor[Command, Result] {

      def execute(Command: Command)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
        import ctx._

        def exec = Command.chassisPolicy match {
          case ChassisPolicy.`always-off` =>
            connection.executeCommandOrError(
              SetPowerRestorePolicy.Command(PowerRestorePolicy.ChassisAlwaysOffAfterAcApplied)
            )
          case ChassisPolicy.previous =>
            connection.executeCommandOrError(
              SetPowerRestorePolicy.Command(PowerRestorePolicy.ChassisPowerRestoredToPreviousState)
            )
          case ChassisPolicy.`always-on` =>
            connection.executeCommandOrError(
              SetPowerRestorePolicy.Command(PowerRestorePolicy.ChassisAlwaysPowersOnAfterAcApplid)
            )
          case ChassisPolicy.list =>
            connection.executeCommandOrError(
              SetPowerRestorePolicy.Command(PowerRestorePolicy.NoChangeJustGetPresentPolicy)
            )
        }

        val result = for {
          cmdResult <- eitherT(exec)
        } yield Result(cmdResult.powerRestorePolicySupport)

        result.run
      }
    }
  }

  case class Command(chassisPolicy: ChassisPolicy) extends IpmiToolCommand {

    def description(): String = {
      s"chassis policy $chassisPolicy"
    }
  }

  object Result

  case class Result(powerRestorePolicySupport: Option[PowerRestorePolicySupport]) extends IpmiToolCommandResult

}
