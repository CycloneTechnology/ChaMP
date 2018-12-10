package com.cyclone.ipmi.command.oem.fujitsu.common.firmware

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * SetFirmwareSelector command and response
  */
object SetFirmwareSelector {

  object CommandResult extends IpmiCommandResult {
    implicit val decoder: Decoder[CommandResult.type] = new Decoder[CommandResult.type] {

      def decode(data: ByteString): CommandResult.type = CommandResult
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult.type] =
      StatusCodeTranslator[CommandResult.type]()
  }

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= selector.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult.type] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult.type]

  }

  case class Command(selector: FirmwareSelector) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuFirmwareRequest
    val commandCode = CommandCode(0x04)
  }

}
