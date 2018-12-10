package com.cyclone.ipmi.command.oem.fujitsu.common.power

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * GetPowerOffInhibit command and response
  */
object GetPowerOffInhibit {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        /* val ianaNumber = */
        is.read(3)
          .as[IanaEnterpriseNumber] // IANA number LSB First (Should always be 80 28 00 for Fujitsu
        /* val length = */
        is.readByte // Should always be 1

        val inhibitFlag = is.readByte.as[InhibitFlag]

        CommandResult(inhibitFlag)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(inhibitFlag: InhibitFlag) extends IpmiCommandResult

  case object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {

      def encode(request: Command.type): ByteString = {

        val b = new ByteStringBuilder

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin

        b += 0x1d.toByte // Command

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuGroupRequest
    val commandCode = CommandCode(0x01)
  }

}
