package com.cyclone.ipmi.command.oem.fujitsu.common.communication

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * SystemOsShutdownRequestAndReset command and response
  */
object SystemOsShutdownRequestAndReset {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        //        val iterator = data.iterator
        //        val is = iterator.asInputStream
        //
        //        val ianaNumber = is.read(3).as[IanaEnterpriseNumber] // IANA number LSB First (Should always be 80 28 00 for Fujitsu

        CommandResult()
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult() extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {

        val b = new ByteStringBuilder

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin

        b += 0x06.toByte // Command

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command() extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuGroupRequest
    val commandCode = CommandCode(0x02)
  }

}
