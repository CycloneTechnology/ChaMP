package com.cyclone.ipmi.command.oem.dell

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * DCMIGetAssetTag
  */
object DcmiGetAssetTag {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val identification = is.readByte
        val length = is.readByte
        val assetTagData = is.read(length)

        CommandResult(identification, assetTagData)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(identification: Byte, assetTagData: ByteString) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += identification
        b += offset
        b += number

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  case class Command(identification: Byte, offset: Byte, number: Byte) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.MediaSpecificRequest
    val commandCode = CommandCode(0x06)
  }

}
