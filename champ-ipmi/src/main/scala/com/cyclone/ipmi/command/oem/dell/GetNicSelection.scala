package com.cyclone.ipmi.command.oem.dell

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * GetNicSelection
  */
object GetNicSelection {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val nicSelection = is.read(1).as[NicSelection]

        CommandResult(nicSelection)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(nicSelection: NicSelection) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  // FIXME use case object Command when no params (and elsewhere...)
  case class Command() extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.OemFree30hRequest
    val commandCode = CommandCode(0x25)
  }

}
