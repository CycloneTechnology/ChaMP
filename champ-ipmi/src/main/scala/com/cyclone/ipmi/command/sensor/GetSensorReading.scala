package com.cyclone.ipmi.command.sensor

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}
import com.cyclone.ipmi.sdr.{EventBits, RawSensorValue, SensorNumber}

/**
  * Get Sensor Reading command
  */
object GetSensorReading {

  sealed trait EventState

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val reading = is.readByte.as[RawSensorValue]

        val availabilityByte = is.readByte
        val eventMessagesEnabled = availabilityByte.bit7
        val sensorScanningEnabled = availabilityByte.bit6
        val readingUnavailable = availabilityByte.bit5

        val eventStateBytesRaw = iterator.toByteString

        val eventStateBytesAugmented = eventStateBytesRaw.length match {
          case 0 => ByteString(0, 0)
          case 1 => ByteString(0, data(0))
          case 2 => ByteString(data(1), data(0))
        }

        val eventBits = eventStateBytesAugmented.as[EventBits]

        CommandResult(
          rawValue = reading,
          eventMessagesEnabled = eventMessagesEnabled,
          sensorScanningEnabled = sensorScanningEnabled,
          readingUnavailable = readingUnavailable,
          eventStateBits = eventBits
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }


  /**
    * @param rawValue reading (undefined unless an analog reading is provided by the sensor)
    */
  case class CommandResult(
    rawValue: RawSensorValue,
    eventMessagesEnabled: Boolean,
    sensorScanningEnabled: Boolean,
    readingUnavailable: Boolean,
    eventStateBits: EventBits
  ) extends IpmiCommandResult

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
    val commandCode = CommandCode(0x2d)
  }

}
