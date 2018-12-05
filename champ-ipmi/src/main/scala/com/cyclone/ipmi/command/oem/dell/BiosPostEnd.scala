package com.cyclone.ipmi.command.oem.dell

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * BiosPostEnd
  */
object BiosPostEnd {

  object CommandResult extends IpmiCommandResult {
    implicit val decoder: Decoder[CommandResult.type] = new Decoder[CommandResult.type] {
      def decode(data: ByteString): CommandResult.type = CommandResult
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult.type] =
      StatusCodeTranslator[CommandResult.type]()
  }

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command, CommandResult.type] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult.type]
  }

  // Note attributeId = 0x00 means read entire configuration data and bytestoRead = 0xFF means read entire configuration or attribute.
  // Note index used by table object only
  case class Command() extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.OemFree30hRequest
    val commandCode = CommandCode(0x80.toByte)
  }

}
