package com.cyclone.ipmi.command.oem.fujitsu.common.communication

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * SystemOsShutdownRequest command and response
  */
object SystemOsShutdownRequest {

  object CommandResult extends IpmiCommandResult {
    implicit val decoder: Decoder[CommandResult.type] = new Decoder[CommandResult.type] {

      def decode(data: ByteString): CommandResult.type = {
        //        val iterator = data.iterator
        //        val is = iterator.asInputStream
        //
        //        val ianaNumber = is.read(3).as[IanaEnterpriseNumber] // IANA number LSB First (Should always be 80 28 00 for Fujitsu

        CommandResult
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult.type] =
      StatusCodeTranslator[CommandResult.type]()
  }

  case object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {

      def encode(request: Command.type): ByteString = {

        val b = new ByteStringBuilder

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin

        b += 0x05.toByte // Command

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult.type] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult.type]

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuGroupRequest
    val commandCode = CommandCode(0x02)
  }

}
