package com.cyclone.ipmi.command.oem.fujitsu.common.communication

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * AgentConnectStatus command and response
  */
object AgentConnectStatus {

  sealed trait ConnectStatus

  object ConnectStatus {
    implicit val decoder: Decoder[ConnectStatus] = new Decoder[ConnectStatus] {

      def decode(data: ByteString): ConnectStatus = data(0).toUnsignedInt match {
        case 0x00 => ConnectionLostAgentNotConnected
        case 0x01 => ConnectionReEstablishedAgentConnected
      }
    }

    implicit val encoder: Coder[ConnectStatus] = new Coder[ConnectStatus] {

      def encode(a: ConnectStatus): ByteString = {
        a match {
          case ConnectionLostAgentNotConnected       => ByteString(0x00)
          case ConnectionReEstablishedAgentConnected => ByteString(0x01)
        }
      }
    }

    case object ConnectionLostAgentNotConnected extends ConnectStatus

    case object ConnectionReEstablishedAgentConnected extends ConnectStatus

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        /*val ianaNumber = */
        is.read(3)
          .as[IanaEnterpriseNumber] // FIXME - IANA number LSB First (Should always be 80 28 00 for Fujitsu)
        /*val length = */
        is.readByte // Should always be 1

        val connectStatus = is.readByte.as[ConnectStatus]

        CommandResult(connectStatus)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(connectStatus: ConnectStatus) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {

        val b = new ByteStringBuilder

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin

        b += 0x08.toByte // Command

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command() extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuGroupRequest
    val commandCode = CommandCode(0x02)
  }

}
