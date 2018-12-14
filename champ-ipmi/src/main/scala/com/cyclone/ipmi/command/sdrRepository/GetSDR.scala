package com.cyclone.ipmi.command.sdrRepository

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import com.cyclone.ipmi.protocol.sdr.{SdrReservationId, SensorDataRecordId}

/**
  * Get SDR command and response
  */
object GetSDR {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val nextRecordId = is.read(2).as[SensorDataRecordId]

        val recordData = iterator.toByteString

        CommandResult(nextRecordId, recordData)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(nextRecordId: SensorDataRecordId, recordData: ByteString) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= reservationId.toBin
        b ++= recordId.toBin
        b += offset.toByte
        b += bytesToRead.toByte

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  case class Command(
    reservationId: SdrReservationId,
    recordId: SensorDataRecordId,
    offset: Int,
    bytesToRead: Int
  ) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.StorageRequest
    val commandCode = CommandCode(0x23)
  }

}
