package com.cyclone.ipmi.tool.command

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError
import com.cyclone.ipmi.command.chassis.ChassisIdentify
import com.cyclone.ipmi.command.chassis.ChassisIdentify.IdentifyInterval
import com.cyclone.ipmi.command.chassis.ChassisIdentify.IdentifyInterval.{Off, On, OnFor}
import com.cyclone.ipmi.tool.command.IpmiCommands._

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the [[ChassisIdentify]] low-level command.
  */
object ChassisIdentifyTool {

  object Command {
    implicit val executor: CommandExecutor[Command, ChassisIdentified.type] = new CommandExecutor[Command, ChassisIdentified.type] {
      def execute(command: Command)(implicit ctx: Ctx): Future[IpmiError \/ ChassisIdentifyTool.ChassisIdentified.type] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
        import ctx._

        val result = for {
          cmdResult <- eitherT(connection.executeCommandOrError(ChassisIdentify.Command(command.interval)))
        } yield ChassisIdentified

        result.run
      }
    }
  }

  case class Command(interval: IdentifyInterval) extends IpmiToolCommand {
    def description(): String = {
      val intervalParam = interval match {
        case Off         => "0"
        case OnFor(time) => time.toSeconds.toString
        case On          => "force"
      }

      s"chassis identify $intervalParam"
    }
  }

  case object ChassisIdentified extends IpmiToolCommandResult

}
