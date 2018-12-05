package com.cyclone.ipmi.command.plaftformEventFilteringAndAlerting

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Get PEF (Platform Event Filtering) Capabilities command and response
  */
object GetPEFCapabilities {

  object PEFCapabilities {
    implicit val decoder: Decoder[PEFCapabilities] = new Decoder[PEFCapabilities] {

      def decode(data: ByteString): PEFCapabilities = {
        val byte = data(0)

        PEFCapabilities(
          OEMEventRecordFilteringSupported = byte.bit7,
          reserved = byte.bit6,
          diagnosticInterrupt = byte.bit5,
          OEMAction = byte.bit4,
          Powercycle = byte.bit3,
          Reset = byte.bit2,
          Powerdown = byte.bit1,
          Alert = byte.bit0
        )
      }
    }
  }

  case class PEFCapabilities(
    OEMEventRecordFilteringSupported: Boolean,
    reserved: Boolean,
    diagnosticInterrupt: Boolean,
    OEMAction: Boolean,
    Powercycle: Boolean,
    Reset: Boolean,
    Powerdown: Boolean,
    Alert: Boolean
  )

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val pefVersion = is.readByte.as[PefVersion]

        val capabilities = is.readByteOptional.map(_.as[PEFCapabilities])

        val numberOfEventFilterTableEntries = is.readByte.toUnsignedInt

        CommandResult(pefVersion, capabilities, numberOfEventFilterTableEntries)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    pefVersion: PefVersion,
    capabilities: Option[PEFCapabilities],
    numberOfEventFilterTableEntries: Int
  ) extends IpmiCommandResult

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {
      def encode(request: Command.type): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.SensorRequest
    val commandCode = CommandCode(0x10)
  }

}
