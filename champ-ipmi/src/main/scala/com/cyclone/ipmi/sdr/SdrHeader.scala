package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.sdrRepository.GetSDR

/**
  * Convenient holder for an SDR 'header'.
  *
  * Essentially a [[GetSDR.CommandResult]]
  * with the (partial) record data decoded as far as possible (typically only
  * enough to determine the body length).
  *
  * @param nextRecordId     the id of the next record
  * @param sensorDataRecord the record (may have a partial or incomplete body)
  */
case class SdrHeader private(
  nextRecordId: SensorDataRecordId,
  headerData: ByteString,
  sensorDataRecord: SensorDataRecord) {
  require(headerData.length == SensorDataRecord.headerLength)

  def recordId: SensorDataRecordId = sensorDataRecord.recordId

  def recordType: SensorDataRecordType = sensorDataRecord.recordType

  def bodyLength: Int = sensorDataRecord.bodyLength

  def totalLength: Int = SensorDataRecord.headerLength + bodyLength
}


object SdrHeader {
  def fromCommandResult(commandResult: GetSDR.CommandResult): IpmiErrorOr[SdrHeader] =
    SensorDataRecord.decoder.handleExceptions.decode(commandResult.recordData)
      .map { sdr =>
        SdrHeader(
          commandResult.nextRecordId,
          commandResult.recordData.take(SensorDataRecord.headerLength),
          sdr)
      }
}

/**
  * Represents an SDR. The type of data depends on the record type.
  */
case class SensorDataRecord(
  recordId: SensorDataRecordId,
  sdrVersion: SdrVersion,
  recordType: SensorDataRecordType,
  bodyLength: Int,
  bodyData: ByteString)

object SensorDataRecord {
  // When we do a Get SDR the length specified is the length of this entire record (header + body)
  // but the bodyLength we get is only the length of the body. So need to add the
  // header length to the body length (once we know it from a partial Get SDR) in order
  // to know how much data we need to build up to get a full SDR.
  val headerLength = 5

  implicit val decoder: Decoder[SensorDataRecord] = new Decoder[SensorDataRecord] {
    def decode(data: ByteString): SensorDataRecord = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val recordId = is.read(2).as[SensorDataRecordId]
      val version = is.read(1).as[SdrVersion]
      val recordType = is.read(1).as[SensorDataRecordType]

      val len = is.readByte.toUnsignedInt

      val body = iterator.toByteString

      SensorDataRecord(
        recordId = recordId,
        sdrVersion = version,
        recordType = recordType,
        bodyLength = len,
        bodyData = body
      )
    }
  }
}
