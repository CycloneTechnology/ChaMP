package com.cyclone.ipmi.command.oem.dell

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * DCMIGetDCMICapability
  */
object DcmiGetDcmiCapability {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val identification = is.readByte
        val majorVersion = is.readByte
        val minorVersion = is.readByte
        val parameterRevision = is.readByte
        val parameterData = is.read(12)

        CommandResult(
          identification,
          majorVersion,
          minorVersion,
          parameterRevision,
          parameterData)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    identification: Byte,
    majorVersion: Byte,
    minorVersion: Byte,
    parameterRevision: Byte,
    parameterData: ByteString) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += identification
        b += parameterSelect

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  case class Command(identification: Byte, parameterSelect: Byte) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.MediaSpecificRequest
    val commandCode = CommandCode(0x01)
  }

}
