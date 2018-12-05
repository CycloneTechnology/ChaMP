package com.cyclone.ipmi.command.oem.fujitsu.common.firmware

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * GetFirmwareSelector command and response
  */
object GetFirmwareSelector {

  sealed trait RunningSelector

  object RunningSelector {
    implicit val decoder: Decoder[RunningSelector] = new Decoder[RunningSelector] {

      def decode(data: ByteString): RunningSelector = data(0).toUnsignedInt match {
        case 0x01 => LowEeprom
        case 0x02 => HighEeprom
      }
    }

    implicit val encoder: Coder[RunningSelector] = new Coder[RunningSelector] {

      def encode(a: RunningSelector): ByteString = {
        a match {
          case LowEeprom  => ByteString(0x01)
          case HighEeprom => ByteString(0x02)
        }
      }
    }

    case object LowEeprom extends RunningSelector

    case object HighEeprom extends RunningSelector

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val nextBootSelector = is.readByte.as[FirmwareSelector]
        val runningSelector = is.readByte.as[RunningSelector]

        CommandResult(nextBootSelector, runningSelector)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(nextBootSelector: FirmwareSelector, runningSelector: RunningSelector)
      extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command(selector: FirmwareSelector) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuFirmwareRequest
    val commandCode = CommandCode(0x05)
  }

}
