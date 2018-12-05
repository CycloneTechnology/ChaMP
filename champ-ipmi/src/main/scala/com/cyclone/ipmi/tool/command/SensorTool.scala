package com.cyclone.ipmi.tool.command

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError._
import com.cyclone.ipmi.api.IpmiConnection
import com.cyclone.ipmi.command.GenericStatusCodeErrors
import com.cyclone.ipmi.command.sensor.{GetSensorReading, GetSensorReadingFactors, GetSensorThresholds}
import com.cyclone.ipmi.sdr.Linearization.Linearizable
import com.cyclone.ipmi.sdr.{RawValueConverter, _}
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.{IpmiError, IpmiOperationContext}
import com.cyclone.util.concurrent.Futures
import com.typesafe.scalalogging.LazyLogging
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that prints sensor readings
  */
object SensorTool {
  val sdrReader: SdrReader = SdrReaderComponent.sdrReader

  object Command extends LazyLogging {
    implicit val executor: CommandExecutor[Command, Result] = new CommandExecutor[Command, Result] {

      def execute(command: Command)(implicit ctx: Ctx): Future[IpmiError \/ SensorTool.Result] = {
        val result = for {
          sdrs     <- eitherT(sdrReader.readAllSdrs)
          filtered <- eitherT(sdrs.filter(command.sdrFilter.predicate).right.point[Future])
          sensorNumbersIdsAndSdrs <- eitherT(
            filtered
              .flatMap { sdr =>
                sdr.sensorNumbersAndIds
                  .map { case (num, sid) => (num, sid, sdr) }
              }
              .right
              .point[Future]
          )
          sensorReadings <- eitherT(sensorReadingsFor(sensorNumbersIdsAndSdrs))
        } yield Result(sensorReadings)

        result.run
      }

      private def sensorReadingsFor(
        sensorNumbersIdsAndSdrs: Seq[(SensorNumber, SensorId, SdrKeyAndBody)]
      )(implicit ctx: IpmiOperationContext): Future[IpmiErrorOr[Seq[SensorReading]]] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
        import ctx._

        def toSensorRecord(sensorNumber: SensorNumber, sensorId: SensorId, sdr: SdrKeyAndBody) = {
          val result: FutureIpmiErrorOr[SensorReading] = for {
            sensorReadingResult <- eitherT(
              connection.executeCommandOrError(GetSensorReading.Command(sensorNumber))
            )
            rawValueConverter <- rawValueConverter(sensorNumber, sensorReadingResult, sdr)

            sensorThresholdsResult <- eitherT(
              connection.executeCommandOrError(GetSensorThresholds.Command(sensorNumber))
            )
            analogReadings <- eitherT(
              evaluateAnalogReadings(
                sensorId,
                sensorReadingResult,
                sensorThresholdsResult,
                rawValueConverter,
                sdr
              ).right.point[Future]
            )

            discreteReadings <- eitherT(
              evaluateDiscreteReadings(sensorId, sensorReadingResult, sdr).right.point[Future]
            )
          } yield SensorReading(sensorId, analogReadings, discreteReadings)

          result.map(Some(_)).run.map { errorOrSensorReading =>
            errorOrSensorReading.recover {
              case GenericStatusCodeErrors.SensorNotPresent =>
                logger.debug(s"Ignoring missing sensor $sensorId")
                None
              case GenericStatusCodeErrors.IllegalCommand =>
                logger.debug(s"Ignoring non-sensor sdr $sensorId")
                None
            }
          }
        }

        Futures
          .traverseSerially(sensorNumbersIdsAndSdrs) {
            case (sensorNumber, sensorId, sdr) => toSensorRecord(sensorNumber, sensorId, sdr)
          }
          .map(_.map(_.flatten))
      }

      private def rawValueConverter(
        sensorNumber: SensorNumber,
        readingResult: GetSensorReading.CommandResult,
        sdr: SdrKeyAndBody
      )(implicit ctx: IpmiOperationContext): FutureIpmiErrorOr[RawValueConverter] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
        import ctx._

        sdr match {
          case analogSdr: AnalogSdrKeyAndBody =>
            def converter(linearizable: Linearizable, readingFactors: ReadingFactors) =
              RawValueConverter.fromReadingFactors(
                readingFactors,
                analogSdr.analogDataFormat,
                linearizable,
                analogSdr.sensorUnits
              )

            analogSdr.linearization match {
              case linearizable: Linearizable =>
                eitherT(
                  converter(linearizable, analogSdr.readingFactors).right[IpmiError].point[Future]
                )

              case Linearization.NonLinearizable =>
                for {
                  sensorReadingFactorsResult <- eitherT(
                    connection.executeCommandOrError(
                      GetSensorReadingFactors.Command(sensorNumber, readingResult.rawValue)
                    )
                  )
                } yield converter(Linearization.Linear, sensorReadingFactorsResult.readingFactors)
            }

          case _ => eitherT(RawValueConverter.NoConversion.right[IpmiError].point[Future])
        }
      }

      private def evaluateAnalogReadings(
        sensorId: SensorId,
        readingResult: GetSensorReading.CommandResult,
        thresholdsResult: GetSensorThresholds.CommandResult,
        rawValueConverter: RawValueConverter,
        sdr: SdrKeyAndBody
      ): Option[AnalogReadings] = sdr match {

        case _: AnalogSdrKeyAndBody if !readingResult.readingUnavailable =>
          val reading =
            AnalogReadings(
              rawValueConverter.converter(readingResult.rawValue),
              thresholdsResult.sensorThresholds.mapValues(rawValueConverter.converter).view.force
            )

          Some(reading)

        case _: SdrKeyAndBody =>
          logger.debug(s"Ignoring non-analog or unavailable reading for $sensorId: $readingResult")
          None
      }

      private def evaluateDiscreteReadings(
        sensorId: SensorId,
        readingResult: GetSensorReading.CommandResult,
        sdr: SdrKeyAndBody
      ): Option[DiscreteReadings] = sdr match {
        case sdr: EventSdrKeyAndBody =>
          Some(
            DiscreteReadings(
              sdr.sensorMasks.readingMask.evaluateOffsets(readingResult.eventStateBits)
            )
          )

        case _: SdrKeyAndBody =>
          logger.debug(s"Ignoring non-discrete reading for $sensorId")
          None
      }

    }
  }

  case class Command(sdrFilter: SdrFilter) extends IpmiToolCommand {

    def description(): String = {
      import SdrFilter._

      sdrFilter match {
        case All => s"sensor list"
        case BySensorIds(sensorIds @ _*) =>
          s"sensor get ${sensorIds.map(sid => s""""${sid.id}"""").mkString(" ")}"

        // There is no way to parse other filter types currently
        case _ => s"sensor list"
      }
    }
  }

  case class AnalogReadings(
    sensorValue: SensorValue,
    thresholdComparisons: Map[ThresholdComparison, SensorValue]
  )

  case class DiscreteReadings(assertedOffsets: Set[EventReadingOffset])

  case class SensorReading(
    sensorId: SensorId,
    analogReadings: Option[AnalogReadings],
    discreteReadings: Option[DiscreteReadings]
  )

  case class Result(records: Seq[SensorReading]) extends IpmiToolCommandResult {
    override def tabulationSource: Seq[SensorReading] = records
  }

}
