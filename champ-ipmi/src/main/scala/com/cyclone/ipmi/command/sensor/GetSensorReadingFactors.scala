package com.cyclone.ipmi.command.sensor

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}
import com.cyclone.ipmi.sdr.{RawSensorValue, ReadingFactors, SensorNumber}

/**
  * Get Sensor Reading Factors command and response
  */
object GetSensorReadingFactors {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val nextReading = is.readByte.as[RawSensorValue]

        val readingFactor = is.read(6).as[ReadingFactors]

        CommandResult(nextReading, readingFactor)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(nextReading: RawSensorValue, readingFactors: ReadingFactors) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= sensorNumber.toBin
        b ++= rawValue.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command(sensorNumber: SensorNumber, rawValue: RawSensorValue) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.SensorRequest
    val commandCode = CommandCode(0x23)
  }

}
