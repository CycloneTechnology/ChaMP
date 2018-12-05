package com.cyclone.ipmi.command.sensor

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}
import com.cyclone.ipmi.sdr.ThresholdComparison._
import com.cyclone.ipmi.sdr.{RawSensorValue, SensorNumber, ThresholdComparison}

/**
  * Get Sensor Thresholds command and response
  */
object GetSensorThresholds {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val readableThresholds = is.readByte.as[Set[ThresholdComparison]]

        val values = Map[ThresholdComparison, Byte](
          ThresholdComparison.LowerNonCritical -> is.readByte,
          ThresholdComparison.LowerCritical -> is.readByte,
          ThresholdComparison.LowerNonRecoverable -> is.readByte,
          ThresholdComparison.UpperNonCritical -> is.readByte,
          ThresholdComparison.UpperCritical -> is.readByte,
          ThresholdComparison.UpperNonRecoverable -> is.readByte
        )
          .collect {
            case (k, b) if readableThresholds.contains(k) => (k, RawSensorValue(b.toUnsignedInt))
          }

        CommandResult(values)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(sensorThresholds: Map[ThresholdComparison, RawSensorValue]) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= sensorNumber.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command(sensorNumber: SensorNumber) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.SensorRequest
    val commandCode = CommandCode(0x27)
  }

}


