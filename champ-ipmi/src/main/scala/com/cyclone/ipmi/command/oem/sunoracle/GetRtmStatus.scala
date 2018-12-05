package com.cyclone.ipmi.command.oem.sunoracle

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}
import com.cyclone.ipmi.StatusCodeError
import com.cyclone.ipmi.command.StatusCode
import akka.util.ByteStringBuilder

/**
  * Get RTM Status command
  */
object GetRtmStatus {

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

        /*val cpldVersion = */is.readByte

        val rtmPresenceDetected = is.readByte.bit0

        CommandResult(rtmPresenceDetected)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult] {
      case CommandNotSupported.code  => CommandNotSupported
      case InvalidDataInRequest.code => InvalidDataInRequest
    }
  }

  case class CommandResult(rtmPresenceDetected: Boolean) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += 0x00.toByte
        b += 0x00.toByte
        b += 0x2a.toByte

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  case class Command() extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.OemRequest
    val commandCode = CommandCode(0x88.toByte)
  }

}
