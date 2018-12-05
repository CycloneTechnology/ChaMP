package com.cyclone.ipmi.command.global

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Cold Reset command and response
  */
/**
  * FIXME The spec says the following, we need to cater for not receiving a response...
  *
  * It is recognized that there are conditions where a given controller may not be able to return a
  * response to a Cold Reset Request message. Therefore, though recommended, the implementation is
  * not required to return a response to the Cold Reset command.
  *
  * Applications should not rely on receiving a response as verification of the completion of a
  * Cold Reset command.
  */

object ColdReset {

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
    val commandCode = CommandCode(0x02)
  }

}
