package com.cyclone.ipmi.tool.command

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.protocol.sdr._
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.IpmiError
import com.typesafe.scalalogging.LazyLogging
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that prints SDR records
  */
object SdrTool {
  val sdrReader: SdrReader = SdrReaderComponent.sdrReader

  object Command extends LazyLogging {
    implicit val executor: CommandExecutor[Command, Result] = new CommandExecutor[Command, Result] {

      def execute(command: Command)(implicit ctx: Ctx): Future[IpmiError \/ SdrTool.Result] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext

        val result = for {
          sdrs     <- eitherT(sdrReader.readAllSdrs)
          filtered <- eitherT(sdrs.filter(command.sdrFilter.predicate).right.point[Future])
        } yield Result(filtered)

        result.run
      }
    }
  }

  case class Command(sdrFilter: SdrFilter) extends IpmiToolCommand {

    def description(): String = {
      import SdrFilter._
      import SensorDataRecordType._

      sdrFilter match {
        case ByRecordTypes(Full)                 => s"sdr list full"
        case ByRecordTypes(Compact)              => s"sdr list compact"
        case ByRecordTypes(EventOnly)            => s"sdr list event"
        case ByRecordTypes(McDeviceLocator)      => s"sdr list mcloc"
        case ByRecordTypes(FruDeviceLocator)     => s"sdr list fru"
        case ByRecordTypes(GenericDeviceLocator) => s"sdr list generic"
        case ByRecordTypes(Full, Compact)        => s"sdr list"
        case All                                 => s"sdr list all"
        case BySensorType(tpe)                   => s"sdr type $tpe"
        case BySensorIds(sensorIds @ _*) =>
          s"sdr get ${sensorIds.map(sid => s""""${sid.id}"""").mkString(" ")}"

        // There is no way to parse other filter types currently
        case _ => s"sdr list all"
      }
    }
  }

  case class Result(records: Seq[SdrKeyAndBody]) extends IpmiToolCommandResult {
    override def tabulationSource: Seq[SdrKeyAndBody] = records
  }

}
