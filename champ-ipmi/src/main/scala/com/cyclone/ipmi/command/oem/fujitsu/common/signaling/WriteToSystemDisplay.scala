package com.cyclone.ipmi.command.oem.fujitsu.common.signaling

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import com.google.common.base.Charsets

/**
  * WriteToSystemDisplay command and response
  */
object WriteToSystemDisplay {

  sealed trait Alignment

  object Alignment {
    implicit val decoder: Decoder[Alignment] = new Decoder[Alignment] {

      def decode(data: ByteString): Alignment = data(0).toUnsignedInt match {
        case 0x00 => Left
        case 0x01 => Centered
      }
    }

    implicit val encoder: Coder[Alignment] = new Coder[Alignment] {

      def encode(a: Alignment): ByteString = {
        a match {
          case Left     => ByteString(0x00)
          case Centered => ByteString(0x01)
        }
      }
    }

    case object Left extends Alignment

    case object Centered extends Alignment

  }

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

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin

        b += 0x02.toByte

        b += lineIndex.toByte // Line on display to write on
        b += 0
        b += 0 // ValueId
        b += message.length.toByte
        b ++= alignment.toBin
        b ++= message.getBytes(Charsets.US_ASCII)

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult.type] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult.type]

  }

  case class Command(message: String, lineIndex: Int, alignment: Alignment) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuGroupRequest
    val commandCode = CommandCode(0x10)
  }

}
