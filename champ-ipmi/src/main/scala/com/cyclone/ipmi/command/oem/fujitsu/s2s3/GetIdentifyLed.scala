package com.cyclone.ipmi.command.oem.fujitsu.s2s3

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * GetIdentifyLed command and response
  */
object GetIdentifyLed {

  sealed trait OnOff

  object OnOff {
    implicit val decoder: Decoder[OnOff] = new Decoder[OnOff] {
      def decode(data: ByteString): OnOff = data(0).toUnsignedInt match {
        case 0x00 => Off
        case 0x01 => On
      }
    }

    implicit val encoder: Coder[OnOff] = new Coder[OnOff] {
      def encode(a: OnOff): ByteString = {
        a match {
          case Off => ByteString(0x00)
          case On  => ByteString(0x01)
        }
      }
    }

    case object Off extends OnOff

    case object On extends OnOff

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        /*val requestType = */is.read(3).as[IanaEnterpriseNumber] // Should match the IANA value present in the request (0x80 0x28 0x00 in this case)

        val ledState = is.readByte.as[OnOff]

        CommandResult(ledState)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(ledState: OnOff) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {

        val b = new ByteStringBuilder

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin

        b += 0xb1.toByte // Command Specifier

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command() extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.OemRequest
    val commandCode = CommandCode(0xf5.toByte)
  }

}
