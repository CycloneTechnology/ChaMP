package com.cyclone.ipmi.command.oem.sunoracle

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.StatusCodeError
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * GetVersion
  */
object GetVersion {

  case object CommandNotSupported extends StatusCodeError {
    val code = StatusCode(0xc1.toByte)
    val message = "Command not supported"
  }

  case object InvalidDataInRequest extends StatusCodeError {
    val code = StatusCode(0xcc.toByte)
    val message = "Invalid data in request"
  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        is.skip(3) // Skip 0x00 0x00 0x2a

        val cpldVersion = is.readByte

        val ipmiFirmwareRev1Byte = is.readByte
        val ipmiFirmwareRev1LowerNibble = ipmiFirmwareRev1Byte.bits0To3

        /*val ipmiFirmwareRev2Byte = */
        is.readByte
        val ipmiFirmwareRev2LowerNibble = ipmiFirmwareRev1Byte.bits0To3
        val ipmiFirmwareRev2HighNibble = ipmiFirmwareRev1Byte.bits4To7

        val ipmiFirmwareRev = "%d.%d.%d".format(
          ipmiFirmwareRev1LowerNibble,
          ipmiFirmwareRev2HighNibble,
          ipmiFirmwareRev2LowerNibble
        )

        is.skip(3) // Reserved for future use, ignore

        CommandResult(cpldVersion, ipmiFirmwareRev)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult] {
        case CommandNotSupported.code  => CommandNotSupported
        case InvalidDataInRequest.code => InvalidDataInRequest
      }
  }

  case class CommandResult(cpldVersion: Byte, ipmiFirmwareRev: String) extends IpmiCommandResult

  case object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {

      def encode(request: Command.type): ByteString = {

        val b = new ByteStringBuilder

        b += 0x00.toByte
        b += 0x00.toByte
        b += 0x2a.toByte

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.OemRequest
    val commandCode = CommandCode(0x80.toByte)
  }

}
