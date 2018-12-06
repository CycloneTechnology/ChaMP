package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.command.{RequestTimeouts, TimeoutContext}
import com.cyclone.ipmi.IpmiError._
import com.cyclone.ipmi.api.IpmiConnection
import com.cyclone.ipmi.command.GenericStatusCodeErrors
import com.cyclone.ipmi.command.sdrRepository.{GetSDR, ReserveSDRRepository}
import com.cyclone.ipmi.{IpmiError, IpmiOperationContext}
import com.typesafe.scalalogging.LazyLogging
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Utility for reading SDRs
  */
trait SdrReader {

  /**
    * Reads an SDR header with a specific record id
    */
  def readSdrHeader(
    recordId: SensorDataRecordId,
    reservationId: SdrReservationId = SdrReservationId.noReservation
  )(implicit ctx: IpmiOperationContext): Future[IpmiErrorOr[SdrHeader]]

  /**
    * Reads all SDR headers
    */
  def readAllSdrHeaders(reservationId: SdrReservationId = SdrReservationId.noReservation)(
    implicit ctx: IpmiOperationContext
  ): Future[IpmiErrorOr[Seq[SdrHeader]]]

  /**
    * Reads an SDR with a specific record id
    */
  def readSdr(recordId: SensorDataRecordId)(
    implicit ctx: IpmiOperationContext
  ): Future[IpmiErrorOr[SdrKeyAndBody]]

  /**
    * Reads all SDRs for a connection
    *
    * @return the SDRs
    */
  def readAllSdrs(implicit ctx: IpmiOperationContext): Future[IpmiErrorOr[Seq[SdrKeyAndBody]]]

}

trait SdrReaderComponent {
  def sdrReader: SdrReader
}

object SdrReaderComponent extends SdrReaderComponent with LazyLogging {

  private val chunkSize = 16

  lazy val sdrReader: SdrReader = new SdrReader {

    def readAllSdrs(implicit ctx: IpmiOperationContext): Future[IpmiError \/ Seq[SdrKeyAndBody]] = {
      val result = for {
        reservation <- eitherT(getReservation)
        records     <- eitherT(readAllSdrBodies(reservation.reservationId))
      } yield records

      result.run
    }

    def readSdrHeader(recordId: SensorDataRecordId, reservationId: SdrReservationId)(
      implicit ctx: IpmiOperationContext
    ): Future[IpmiError \/ SdrHeader] = {

      logger.debug(s"Reading SDR header for $recordId, $reservationId")

      val result = for {
        commandResult <- runGetSdr(
          recordId,
          reservationId,
          bytesToRead = SensorDataRecord.headerLength
        )
        sensorDataRecordHeader <- eitherT(SdrHeader.fromCommandResult(commandResult).point[Future])
      } yield sensorDataRecordHeader

      result.run
    }

    def readAllSdrHeaders(
      reservationId: SdrReservationId
    )(implicit ctx: IpmiOperationContext): Future[IpmiError \/ Seq[SdrHeader]] = {
      def loop(
        acc: Vector[SdrHeader],
        recordId: SensorDataRecordId
      ): FutureIpmiErrorOr[Seq[SdrHeader]] =
        if (recordId == SensorDataRecordId.last)
          rightT(acc.point[Future])
        else
          for {
            header <- eitherT(readSdrHeader(recordId, reservationId))

            _ = logger.debug(s"Read $header; next is ${header.nextRecordId}")
            headers <- loop(acc :+ header, header.nextRecordId)
          } yield headers

      loop(Vector.empty, SensorDataRecordId.first).run
    }

    def readSdr(
      recordId: SensorDataRecordId
    )(implicit ctx: IpmiOperationContext): Future[IpmiError \/ SdrKeyAndBody] = {
      val result = for {
        reservation         <- eitherT(getReservation)
        bodyAndNextRecordId <- eitherT(readSdrBody(recordId, reservation.reservationId))
      } yield bodyAndNextRecordId._1

      result.run
    }

    private def getReservation(implicit ctx: IpmiOperationContext) = {
      implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
      import ctx._

      // Repeated reservation attempts can cause reservation to be cancelled
      // - so just do once with a long timeout...
      connection.executeCommandOrError(ReserveSDRRepository.Command)(
        timeoutContext = timeoutContext.withTimeouts(RequestTimeouts.simple(30.seconds, maxAttempts = 1)),
        codec = ReserveSDRRepository.Command.codec
      )
    }

    private def readAllSdrBodies(
      reservationId: SdrReservationId
    )(implicit ctx: IpmiOperationContext): Future[IpmiErrorOr[Seq[SdrKeyAndBody]]] = {

      def loop(
        acc: Vector[SdrKeyAndBody],
        recordId: SensorDataRecordId
      ): FutureIpmiErrorOr[Seq[SdrKeyAndBody]] =
        if (recordId == SensorDataRecordId.last)
          rightT(acc.point[Future])
        else
          for {
            bodyAndNextRecordId <- eitherT(readSdrBody(recordId, reservationId))
            (body, nextRecordId) = bodyAndNextRecordId

            _ = logger.debug(s"Read $body; next is $nextRecordId")
            bodies <- loop(acc :+ body, nextRecordId)
          } yield bodies

      loop(Vector.empty, SensorDataRecordId.first).run
    }

    private def readSdrBody(recordId: SensorDataRecordId, reservationId: SdrReservationId)(
      implicit ctx: IpmiOperationContext
    ): Future[IpmiErrorOr[(SdrKeyAndBody, SensorDataRecordId)]] = {
      import GenericStatusCodeErrors._

      def doReadSdrBody(
        remainingToChunkSize: Int => Int
      ): Future[IpmiErrorOr[(SdrKeyAndBody, SensorDataRecordId)]] = {

        def loop(
          acc: ByteString,
          totalLength: Int,
          offset: Int,
          bytesToRead: Int
        ): FutureIpmiErrorOr[ByteString] = {
          logger.debug(
            s"Getting SDR chunk from $offset to ${offset + bytesToRead} (of $totalLength)."
          )
          if (bytesToRead <= 0) rightT(acc.point[Future])
          else
            for {
              commandResult <- runGetSdr(recordId, reservationId, offset, bytesToRead)
              data = commandResult.recordData
              newAcc = acc ++ data
              remaining = totalLength - newAcc.length
              result <- loop(
                newAcc,
                totalLength,
                offset = offset + data.length,
                bytesToRead = remaining min chunkSize
              )
            } yield result
        }

        val result = for {
          header <- eitherT(readSdrHeader(recordId, reservationId))

          remaining = header.bodyLength

          _ = logger.debug(s"Got partial SDR. Reading remaining $remaining...")
          sensorDataRecordBytes <- loop(
            header.headerData,
            header.totalLength,
            SensorDataRecord.headerLength,
            remainingToChunkSize(remaining)
          )

          recordBody <- eitherT(decodeBody(sensorDataRecordBytes).point[Future])
        } yield (recordBody, header.nextRecordId)

        result.run
      }

      logger.debug(s"Reading SDR for $recordId, $reservationId")

      val inChunks = { remaining: Int =>
        remaining min chunkSize
      }
      val inOneGo = { remaining: Int =>
        remaining
      }

      // See sec 33.12 - use small chunks if this fails...
      doReadSdrBody(inOneGo).flatMap {
        case \/-(r) => r.right.point[Future]
        case -\/(e)
            if e == TooManyBytesRequested |
            e == UnspecifiedError =>
          doReadSdrBody(inChunks)
        case -\/(e) => e.left.point[Future]
      }
    }

    private def runGetSdr(
      recordId: SensorDataRecordId,
      reservationId: SdrReservationId,
      offset: Int = 0,
      bytesToRead: Int = 0xff
    )(implicit ctx: IpmiOperationContext) = {
      implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
      import ctx._

      for {
        commandResult <- eitherT(
          connection
            .executeCommandOrError(GetSDR.Command(reservationId, recordId, offset, bytesToRead))
        )
        _ = logger.debug(
          s"Read SDR chunk from $offset for $bytesToRead bytes for $recordId: $commandResult"
        )
      } yield commandResult
    }

    private def decodeBody(sensorDataRecordBytes: ByteString): IpmiErrorOr[SdrKeyAndBody] =
      for {
        sensorDataRecord <- SensorDataRecord.decoder.handleExceptions.decode(sensorDataRecordBytes)
        recordBody       <- SdrKeyAndBody.decodeBody(sensorDataRecord)
      } yield recordBody

  }

}
