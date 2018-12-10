package com.cyclone.ipmi.command.global

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * Warm Reset command and response
  */
object WarmReset {

  object CommandResult extends IpmiCommandResult {
    implicit val decoder: Decoder[CommandResult.type] = new Decoder[CommandResult.type] {
      def decode(data: ByteString): CommandResult.type = CommandResult
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult.type] =
      StatusCodeTranslator[CommandResult.type]()
  }

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {
      def encode(request: Command.type): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult.type] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult.type]

    val networkFunction: NetworkFunction = NetworkFunction.ApplicationRequest
    val commandCode = CommandCode(0x03)
  }

}
