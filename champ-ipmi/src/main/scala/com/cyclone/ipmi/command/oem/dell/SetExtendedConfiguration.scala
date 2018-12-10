package com.cyclone.ipmi.command.oem.dell

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * SetExtendedConfiguration
  */
object SetExtendedConfiguration {

  sealed trait Progress

  object Progress {
    implicit val encoder: Coder[Progress] = new Coder[Progress] {

      def encode(a: Progress): ByteString = {
        a match {
          case InProgress                                         => ByteString(0x00)
          case LastConfigurationDataBeingTransferredInThisRequest => ByteString(0x01)
        }
      }
    }

    case object InProgress extends Progress

    case object LastConfigurationDataBeingTransferredInThisRequest extends Progress

  }

  // FIXME - Completion Code 01x == No More Data
  object CommandResult extends IpmiCommandResult {
    implicit val decoder: Decoder[CommandResult.type] = new Decoder[CommandResult.type] {

      def decode(data: ByteString): CommandResult.type = CommandResult
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult.type] =
      StatusCodeTranslator[CommandResult.type]()
  }

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += reservationId
        b += configurationId
        b += attributeId
        b += index
        b += dataOffsetLSB
        b += dataOffsetMSB
        b ++= inProgress.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult.type] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult.type]
  }

  // Note attributeId = 0x00 means read entire configuration data and bytestoRead = 0xFF means read entire configuration or attribute.
  // Note index used by table object only
  case class Command(
    reservationId: Byte,
    configurationId: Byte,
    attributeId: Byte = 0x00.toByte,
    index: Byte,
    dataOffsetLSB: Byte,
    dataOffsetMSB: Byte,
    inProgress: Progress
  ) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.OemFree30hRequest
    val commandCode = CommandCode(0x03)
  }

}
