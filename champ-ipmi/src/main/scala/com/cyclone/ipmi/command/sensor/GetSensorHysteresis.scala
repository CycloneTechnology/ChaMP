package com.cyclone.ipmi.command.sensor

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import com.cyclone.ipmi.protocol.sdr.SensorNumber

/**
  * Get Sensor Hysteresis command and response
  */
object GetSensorHysteresis {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val positiveGoingThreshold = is.readByte.toUnsignedInt

        val negativeGoingThreshold = is.readByte.toUnsignedInt

        CommandResult(positiveGoingThreshold, negativeGoingThreshold)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(positiveGoingThreshold: Int, negativeGoingThreshold: Int) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= sensorNumber.toBin
        b += 0xFF.toByte

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  case class Command(sensorNumber: SensorNumber) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.SensorRequest
    val commandCode = CommandCode(0x25)
  }

}
