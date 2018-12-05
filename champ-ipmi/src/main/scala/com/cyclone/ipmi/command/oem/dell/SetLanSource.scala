package com.cyclone.ipmi.command.oem.dell

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.LanSource
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * SetLanSource
  */
object SetLanSource {

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

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= lanSource.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  // Note assetTag max size is 10 (0x0a) bytes
  case class Command(lanSource: LanSource) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.OemFree34hRequest
    val commandCode = CommandCode(0x13)
  }

}
