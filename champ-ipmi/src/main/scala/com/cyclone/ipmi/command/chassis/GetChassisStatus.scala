package com.cyclone.ipmi.command.chassis

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Get Chassis Status command and response
  */
object GetChassisStatus {

  sealed trait PowerRestorePolicy

  object PowerRestorePolicy {
    implicit val decoder: Decoder[PowerRestorePolicy] = new Decoder[PowerRestorePolicy] {
      def decode(data: ByteString): PowerRestorePolicy = data(0).bits5To6.toUnsignedInt match {
        case 0 => StaysPoweredOff
        case 1 => RestoreState
        case 2 => PowerUp
        case 3 => Unknown
      }
    }

    case object StaysPoweredOff extends PowerRestorePolicy

    case object RestoreState extends PowerRestorePolicy

    case object PowerUp extends PowerRestorePolicy

    case object Unknown extends PowerRestorePolicy

  }

  object CurrentPowerState {
    implicit val decoder: Decoder[CurrentPowerState] = new Decoder[CurrentPowerState] {
      def decode(data: ByteString): CurrentPowerState = {
        val byte = data(0)

        CurrentPowerState(
          restorePolicy = byte.as[PowerRestorePolicy],
          controlFault = byte.bit4,
          fault = byte.bit3,
          interlock = byte.bit4,
          overload = byte.bit1,
          on = byte.bit0
        )
      }
    }
  }

  case class CurrentPowerState(
    restorePolicy: PowerRestorePolicy,
    controlFault: Boolean,
    fault: Boolean,
    interlock: Boolean,
    overload: Boolean,
    on: Boolean
  )

  object LastPowerEvent {
    implicit val decoder: Decoder[LastPowerEvent] = new Decoder[LastPowerEvent] {
      def decode(data: ByteString): LastPowerEvent = {
        val byte = data(0)

        LastPowerEvent(
          onByIpmi = byte.bit4,
          causedByFault = byte.bit3,
          causedByInterlock = byte.bit2,
          causedByOverload = byte.bit1,
          causedByAcFailed = byte.bit0
        )
      }
    }
  }

  case class LastPowerEvent(
    onByIpmi: Boolean,
    causedByFault: Boolean,
    causedByInterlock: Boolean,
    causedByOverload: Boolean,
    causedByAcFailed: Boolean
  )

  sealed trait ChassisIdentifyState

  object ChassisIdentifyState {
    implicit val decoder: Decoder[ChassisIdentifyState] = new Decoder[ChassisIdentifyState] {
      def decode(data: ByteString): ChassisIdentifyState =
        data(0).toUnsignedInt match {
          case 0 => Off
          case 1 => Temporary
          case 2 => Indefinite
          case 3 => Reserved
        }
    }

    case object Off extends ChassisIdentifyState

    case object Temporary extends ChassisIdentifyState

    case object Indefinite extends ChassisIdentifyState

    // For reserved value
    case object Reserved extends ChassisIdentifyState

  }

  object MiscChassisState {
    implicit val decoder: Decoder[MiscChassisState] = new Decoder[MiscChassisState] {
      def decode(data: ByteString): MiscChassisState = {
        val byte = data(0)

        MiscChassisState(
          identifyState = if (byte.bit6) None else Some(byte.bits4To5.as[ChassisIdentifyState]),
          coolingFault = byte.bit3,
          driveFault = byte.bit2,
          frontPanelLockout = byte.bit1,
          chassisIntrusion = byte.bit0
        )
      }
    }
  }

  case class MiscChassisState(
    identifyState: Option[ChassisIdentifyState],
    coolingFault: Boolean,
    driveFault: Boolean,
    frontPanelLockout: Boolean,
    chassisIntrusion: Boolean)

  object FrontPanelButtonCapabilities {
    implicit val decoder: Decoder[FrontPanelButtonCapabilities] = new Decoder[FrontPanelButtonCapabilities] {
      def decode(data: ByteString): FrontPanelButtonCapabilities = {
        val byte = data(0)

        FrontPanelButtonCapabilities(
          standbyAllowed = byte.bit7,
          diagnosticInterruptDisableAllowed = byte.bit6,
          resetDisableAllowed = byte.bit5,
          powerOffDisableAllowed = byte.bit4,
          standbyDisabled = byte.bit3,
          diagnosticInterruptDisabled = byte.bit2,
          resetDisabled = byte.bit1,
          powerOffDisabled = byte.bit0
        )
      }
    }
  }

  case class FrontPanelButtonCapabilities(
    standbyAllowed: Boolean,
    diagnosticInterruptDisableAllowed: Boolean,
    resetDisableAllowed: Boolean,
    powerOffDisableAllowed: Boolean,
    standbyDisabled: Boolean,
    diagnosticInterruptDisabled: Boolean,
    resetDisabled: Boolean,
    powerOffDisabled: Boolean)

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val currentPowerState = is.readByte.as[CurrentPowerState]
        val lastPowerEvent = is.readByte.as[LastPowerEvent]
        val miscChassisState = is.readByte.as[MiscChassisState]
        val frontPanelButtonCapabilities =
          is.readByteOptional.map(_.as[FrontPanelButtonCapabilities])

        CommandResult(
          currentPowerState,
          lastPowerEvent,
          miscChassisState,
          frontPanelButtonCapabilities
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    currentPowerState: CurrentPowerState,
    lastPowerEvent: LastPowerEvent,
    miscChassisState: MiscChassisState,
    frontPanelButtonCapabilities: Option[FrontPanelButtonCapabilities]
  ) extends IpmiCommandResult

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {
      def encode(request: Command.type): ByteString =
        ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] = CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.ChassisRequest
    val commandCode = CommandCode(0x01)
  }

}


