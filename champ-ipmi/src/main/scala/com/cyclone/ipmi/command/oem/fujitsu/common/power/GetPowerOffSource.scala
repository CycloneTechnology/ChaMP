package com.cyclone.ipmi.command.oem.fujitsu.common.power

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * GetPowerOffSource command and response
  */
object GetPowerOffSource {

  sealed trait PowerOffSource

  object PowerOffSource {
    implicit val decoder: Decoder[PowerOffSource] = new Decoder[PowerOffSource] {

      def decode(data: ByteString): PowerOffSource = data(0).toUnsignedInt match {
        case 0x00 => Software
        case 0x01 => PowerSwitch
        case 0x02 => AcPowerFail
        case 0x03 => ClockOrTimer
        case 0x04 => FanFailure
        case 0x05 => CriticalTemperature
        case 0x08 => FinalPowerOffAfterRepeatedWatchdogTimeouts
        case 0x0c => FinalPowerOffAfterRepeatedCpuErrors
        case 0x1d => PoweredOffByRemoteControlViaRemoteManager
      }
    }

    implicit val encoder: Coder[PowerOffSource] = new Coder[PowerOffSource] {

      def encode(a: PowerOffSource): ByteString = {
        a match {
          case Software                                   => ByteString(0x00)
          case PowerSwitch                                => ByteString(0x01)
          case AcPowerFail                                => ByteString(0x02)
          case ClockOrTimer                               => ByteString(0x03)
          case FanFailure                                 => ByteString(0x04)
          case CriticalTemperature                        => ByteString(0x05)
          case FinalPowerOffAfterRepeatedWatchdogTimeouts => ByteString(0x08)
          case FinalPowerOffAfterRepeatedCpuErrors        => ByteString(0x0c)
          case PoweredOffByRemoteControlViaRemoteManager  => ByteString(0x1d)
        }
      }
    }

    case object Software extends PowerOffSource

    case object PowerSwitch extends PowerOffSource

    case object AcPowerFail extends PowerOffSource

    case object ClockOrTimer extends PowerOffSource

    case object FanFailure extends PowerOffSource

    case object CriticalTemperature extends PowerOffSource

    case object FinalPowerOffAfterRepeatedWatchdogTimeouts extends PowerOffSource

    case object FinalPowerOffAfterRepeatedCpuErrors extends PowerOffSource

    case object PoweredOffByRemoteControlViaRemoteManager extends PowerOffSource

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        /*val ianaNumber = */
        is.read(3)
          .as[IanaEnterpriseNumber] // IANA number LSB First (Should always be 80 28 00 for Fujitsu
        /*val length = */
        is.readByte // Should always be 1
        val powerOffSource = is.readByte.as[PowerOffSource]

        CommandResult(powerOffSource)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(powerOffSource: PowerOffSource) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {

        val b = new ByteStringBuilder

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin
        b += 0x16.toByte

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command() extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuGroupRequest
    val commandCode = CommandCode(0x01)
  }

}
