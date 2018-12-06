package com.cyclone.ipmi.command.oem.fujitsu.common.firmware

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * One format for the GetRemoteStorageConnectionOrStatus command and response
  */
object GetRemoteStorageStatus {

  sealed trait Connected

  object Connected {
    implicit val decoder: Decoder[Connected] = new Decoder[Connected] {

      def decode(data: ByteString): Connected = data(0).toUnsignedInt match {
        case 0x00 => No
        case 0x01 => Yes
      }
    }

    implicit val encoder: Coder[Connected] = new Coder[Connected] {

      def encode(a: Connected): ByteString = {
        a match {
          case No  => ByteString(0x00)
          case Yes => ByteString(0x01)
        }
      }
    }

    case object No extends Connected

    case object Yes extends Connected

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        /*val requestType = */
        is.readByte // Should match the value in the request (0x02 in this case)

        val connectedState = is.readByte.as[Connected]

        /*val unused = */
        is.read(2)

        CommandResult(connectedState)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(connectedState: Connected) extends IpmiCommandResult

  case object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {

      def encode(request: Command.type): ByteString = {

        val b = new ByteStringBuilder

        b += 0x01.toByte // requestType

        b += 0x00.toByte
        b += 0x00.toByte

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuOemRequest
    val commandCode = CommandCode(0x19)
  }

}
