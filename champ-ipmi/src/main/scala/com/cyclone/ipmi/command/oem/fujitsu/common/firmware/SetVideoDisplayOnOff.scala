package com.cyclone.ipmi.command.oem.fujitsu.common.firmware

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * SetVideoDisplayOnOff command and response
  */
object SetVideoDisplayOnOff {

  sealed trait OnOff

  object OnOff {
    implicit val decoder: Decoder[OnOff] = new Decoder[OnOff] {

      def decode(data: ByteString): OnOff = data(0).toUnsignedInt match {
        case 0x00 => On
        case 0x01 => Off
      }
    }

    implicit val encoder: Coder[OnOff] = new Coder[OnOff] {

      def encode(a: OnOff): ByteString = {
        a match {
          case On  => ByteString(0x00)
          case Off => ByteString(0x01)
        }
      }
    }

    case object On extends OnOff

    case object Off extends OnOff

  }

  object CommandResult extends IpmiCommandResult{
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

        b ++= onOff.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult.type] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult.type]

  }

  case class Command(onOff: OnOff) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuOemRequest
    val commandCode = CommandCode(0x1a)
  }

}
