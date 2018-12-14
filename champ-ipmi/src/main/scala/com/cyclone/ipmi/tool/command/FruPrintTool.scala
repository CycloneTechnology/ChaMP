package com.cyclone.ipmi.tool.command

import akka.util.ByteString
import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.client.IpmiConnection
import com.cyclone.ipmi.codec.RichByteString
import com.cyclone.ipmi.command.GenericStatusCodeErrors
import com.cyclone.ipmi.command.fruInventory.{GetFruInventoryAreaInfo, ReadFruData}
import com.cyclone.ipmi.command.global.{DeviceId, GetDeviceId}
import com.cyclone.ipmi.protocol.fru.{Fru, FruDescriptor}
import com.cyclone.ipmi.protocol.sdr._
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.{DeadlineReached, IpmiError, IpmiOperationContext}
import com.cyclone.util.concurrent.Futures
import com.typesafe.scalalogging.LazyLogging
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that prints FRU (field-replaceable unit) information.
  */
object FruPrintTool {
  val sdrReader: SdrReader = SdrReaderComponent.sdrReader

  object Command extends IpmiToolCommand with LazyLogging {
    val rawDataReadChunkSize = 16

    private[command] def chunkSizesAndOffsetsFor(size: Int): Iterator[(Int, Short)] = {
      logger.debug(s"Total size = $size")

      def chunkSizes =
        (0 until size / rawDataReadChunkSize).toIterator.map(_ => rawDataReadChunkSize) ++
        (if (size % rawDataReadChunkSize == 0) Iterator.empty
         else Iterator(size % rawDataReadChunkSize))

      val offsets = chunkSizes.scanLeft(0)(_ + _).map(_.toShort)

      chunkSizes zip offsets
    }

    implicit val executor: CommandExecutor[Command.type, Result] =
      new CommandExecutor[Command.type, Result] {

        def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext

          val result = for {
            frus <- eitherT(readFrus)
          } yield Result(frus)

          result.run
        }

        private def readFruFor(fruDescriptor: FruDescriptor)(implicit ctx: IpmiOperationContext) = {
          logger.debug(s"Reading FRU for $fruDescriptor")

          val result = for {
            rawData <- rawFruData(fruDescriptor.deviceId)
            fru <- eitherT(
              fruDescriptor
                .decode(rawData)
                .map(fru => FruInfo.present(fruDescriptor, fru))
                .point[Future]
            )
          } yield fru

          result.run.map { fruInfoOrError =>
            fruInfoOrError.recover(Function.unlift {
              case GenericStatusCodeErrors.SensorNotPresent => Some(FruInfo.absent(fruDescriptor))
              case DeadlineReached                          => None // <- Don't recover from this
              case e: IpmiError                             => Some(FruInfo.error(fruDescriptor, e.message))
            })
          }
        }

        private def readFrus(implicit ctx: IpmiOperationContext) = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
          import ctx._

          val result = for {
            deviceId       <- eitherT(connection.executeCommandOrError(GetDeviceId.Command))
            fruDescriptors <- getFruDescriptors
            frus           <- eitherT(Futures.traverseSerially(fruDescriptors)(readFruFor))
          } yield frus

          result.run
        }

        private def getFruDescriptors(implicit ctx: IpmiOperationContext) = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
          import ctx._

          def fruDescriptorsFrom(sdrHeader: SdrHeader) =
            if (sdrHeader.recordType.canProvideFruDescriptor) {
              val result = for {
                sdr <- eitherT(sdrReader.readSdr(sdrHeader.recordId))
                optDescriptor = sdr match {
                  case fruSdr: FruSdrKeyAndBody => fruSdr.optFruDescriptor
                  case _                        => None
                }
              } yield optDescriptor

              result.run
            } else None.right[IpmiError].point[Future]

          for {
            headers <- eitherT(sdrReader.readAllSdrHeaders())
            descriptors <- eitherT(
              Futures
                .traverseSerially(headers)(fruDescriptorsFrom)
                .map(_.map(_.flatten))
            )
          } yield descriptors
        }

        private def rawFruData(deviceId: DeviceId)(implicit ctx: IpmiOperationContext) = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
          import ctx._

          for {
            areaInfo <- eitherT(
              connection.executeCommandOrError(GetFruInventoryAreaInfo.Command(deviceId))
            )

            chunksAndOffsets = chunkSizesAndOffsetsFor(areaInfo.fruInventoryAreaSize)
            bytes <- eitherT(
              chunksAndOffsets.foldLeft(Future.successful(ByteString.empty.right[IpmiError])) {
                case (facc, (chunkSize, offset)) =>
                  val result = for {
                    acc <- eitherT(facc)

                    _ = logger.debug(s"Reading offset $offset for $chunkSize")
                    commandResult <- eitherT(
                      connection
                        .executeCommandOrError(ReadFruData.Command(deviceId, offset, chunkSize))
                    )
                  } yield {
                    val ReadFruData.CommandResult(data) = commandResult
                    logger.debug(s"Read chunk ${data.toHexString()}")
                    acc ++ data
                  }

                  result.run
              }
            )

          } yield bytes
        }
      }

    def description() = "fru print"
  }

  /**
    * FRU information for a sensor/device
    */
  case class FruInfo(
    fruDescriptor: FruDescriptor,
    present: Boolean,
    fru: Option[Fru],
    error: Option[String]
  )

  object FruInfo {

    def present(fruDescriptor: FruDescriptor, fru: Fru): FruInfo =
      FruInfo(fruDescriptor, present = true, Some(fru), None)

    def absent(fruDescriptor: FruDescriptor): FruInfo =
      FruInfo(fruDescriptor, present = false, None, None)

    def error(fruDescriptor: FruDescriptor, message: String): FruInfo =
      FruInfo(fruDescriptor, present = false, None, Some(message))
  }

  case class Result(fruInfos: Seq[FruInfo]) extends IpmiToolCommandResult {
    override def tabulationSource: Seq[FruInfo] = fruInfos
  }

}
