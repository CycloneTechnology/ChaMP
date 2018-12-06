package com.cyclone.ipmi.command.oem.fujitsu.common.power

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * GetPowerOnSource command and response
  */
object GetPowerOnSource {

  sealed trait PowerOnSource

  object PowerOnSource {
    implicit val decoder: Decoder[PowerOnSource] = new Decoder[PowerOnSource] {

      def decode(data: ByteString): PowerOnSource = data(0).toUnsignedInt match {
        case 0x00 => SoftwareOrCommand
        case 0x01 => PowerSwitch
        case 0x02 => AutomaticRestartAfterPowerFailure
        case 0x03 => ClockOrTimer
        case 0x04 => AutomaticRestartAfterFanShutdown
        case 0x05 => AutomaticRestartAfterCriticalTemperatureShutdown
        case 0x08 => RebootAfterWatchdogTimeout
        case 0x09 => RemoteOn
        case 0x0c => RebootAfterCpuError
        case 0x15 => RebootByHardwareReset
        case 0x16 => RebootAfterWarmStart
        case 0x1a => PoweredOnByPciBusPowerManagementEvent
        case 0x1d => PoweredOnByRemoteControlViaRemoteManager
        case 0x1e => RebootResetByRemoteControlViaRemoteManager
      }
    }

    implicit val encoder: Coder[PowerOnSource] = new Coder[PowerOnSource] {

      def encode(a: PowerOnSource): ByteString = {
        a match {
          case SoftwareOrCommand                                => ByteString(0x00)
          case PowerSwitch                                      => ByteString(0x01)
          case AutomaticRestartAfterPowerFailure                => ByteString(0x02)
          case ClockOrTimer                                     => ByteString(0x03)
          case AutomaticRestartAfterFanShutdown                 => ByteString(0x04)
          case AutomaticRestartAfterCriticalTemperatureShutdown => ByteString(0x05)
          case RebootAfterWatchdogTimeout                       => ByteString(0x08)
          case RemoteOn                                         => ByteString(0x09)
          case RebootAfterCpuError                              => ByteString(0x0c)
          case RebootByHardwareReset                            => ByteString(0x15)
          case RebootAfterWarmStart                             => ByteString(0x16)
          case PoweredOnByPciBusPowerManagementEvent            => ByteString(0x1a)
          case PoweredOnByRemoteControlViaRemoteManager         => ByteString(0x1d)
          case RebootResetByRemoteControlViaRemoteManager       => ByteString(0x1e)
        }
      }
    }

    case object SoftwareOrCommand extends PowerOnSource

    case object PowerSwitch extends PowerOnSource

    case object AutomaticRestartAfterPowerFailure extends PowerOnSource

    case object ClockOrTimer extends PowerOnSource

    case object AutomaticRestartAfterFanShutdown extends PowerOnSource

    case object AutomaticRestartAfterCriticalTemperatureShutdown extends PowerOnSource

    case object RebootAfterWatchdogTimeout extends PowerOnSource

    case object RemoteOn extends PowerOnSource

    case object RebootAfterCpuError extends PowerOnSource

    case object RebootByHardwareReset extends PowerOnSource

    case object RebootAfterWarmStart extends PowerOnSource

    case object PoweredOnByPciBusPowerManagementEvent extends PowerOnSource

    case object PoweredOnByRemoteControlViaRemoteManager extends PowerOnSource

    case object RebootResetByRemoteControlViaRemoteManager extends PowerOnSource

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
        val powerOnSource = is.readByte.as[PowerOnSource]

        CommandResult(powerOnSource)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(powerOnSource: PowerOnSource) extends IpmiCommandResult

  case object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {

      def encode(request: Command.type): ByteString = {

        val b = new ByteStringBuilder

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin

        b += 0x15.toByte

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuGroupRequest
    val commandCode = CommandCode(0x01)
  }

}
