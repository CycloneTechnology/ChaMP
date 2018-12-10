package com.cyclone.ipmi.command.oem.dell

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * SetAssetTag
  */
object SetAssetTag {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val countWritten = is.readByte

        CommandResult(countWritten)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(countWritten: Byte) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += assetTag.length.toByte
        b ++= assetTag

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  // Note assetTag max size is 10 (0x0a) bytes
  case class Command(assetTag: ByteString) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.OemFree34hRequest
    val commandCode = CommandCode(0x12)
  }

}
