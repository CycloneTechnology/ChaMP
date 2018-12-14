package com.cyclone.ipmi.command.sensor

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import com.cyclone.ipmi.protocol.sdr.{SensorNumber, SensorType}

/**
  * Get Sensor Type command and response
  */
object GetSensorType {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val sensorType = is.readByte.as[SensorType]

        val eventReadingsTypeCode = is.readByte.bits0To6

        CommandResult(sensorType, eventReadingsTypeCode)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(sensorType: SensorType, eventReadingsTypeCode: Byte) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= sensorNumber.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command(sensorNumber: SensorNumber) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.SensorRequest
    val commandCode = CommandCode(0x27)
  }

}
