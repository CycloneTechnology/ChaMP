package com.cyclone.ipmi.command.oem.inventec

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.LanSource
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * GetLanSource
  */
object GetLanSource {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val lanSourceSetting = is.read(1).as[LanSource]

        CommandResult(lanSourceSetting)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(lanSourceSetting: LanSource) extends IpmiCommandResult

  // Note assetTag max size is 10 (0x0a) bytes
  case object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {
      def encode(request: Command.type): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.OemFree34hRequest
    val commandCode = CommandCode(0x14)
  }

}
