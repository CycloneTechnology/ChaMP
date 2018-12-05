package com.cyclone.ipmi.command.chassis

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Get System Restart Cause command and response
  */
object GetSystemRestartCause {

  sealed trait RestartCause

  object RestartCause {
    implicit val decoder: Decoder[RestartCause] = new Decoder[RestartCause] {

      def decode(data: ByteString): RestartCause = data(0).bits0To3.toUnsignedInt match {
        case 0x00 => Unknown
        case 0x01 => ChassisControlCommand
        case 0x02 => ResetViaPushbutton
        case 0x03 => PowerUpViaPowerPushbutton
        case 0x04 => WatchdogExpiration
        case 0x05 => OEM
        case 0x06 => AutomaticPowerUpOnACBeingAppliedDueTo_AlwaysRestore_PowerRestorePolicy
        case 0x07 =>
          AutomaticPowerUpOnACBeingAppliedDueTo_RestorePreviousPowerState_PowerRestorePolicy
        case 0x08 => ResetViaPEF
        case 0x09 => PowerCycleViaPEF
        case 0x0A => SoftReset
        case 0x0B => PowerupViaRTC
      }

    }

    case object Unknown extends RestartCause

    case object ChassisControlCommand extends RestartCause

    case object ResetViaPushbutton extends RestartCause

    case object PowerUpViaPowerPushbutton extends RestartCause

    case object WatchdogExpiration extends RestartCause

    case object OEM extends RestartCause

    case object AutomaticPowerUpOnACBeingAppliedDueTo_AlwaysRestore_PowerRestorePolicy extends RestartCause

    case object AutomaticPowerUpOnACBeingAppliedDueTo_RestorePreviousPowerState_PowerRestorePolicy extends RestartCause

    case object ResetViaPEF extends RestartCause

    case object PowerCycleViaPEF extends RestartCause

    case object SoftReset extends RestartCause

    case object PowerupViaRTC extends RestartCause

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val restartCause = is.readByte.as[RestartCause]
        val channelNumber = is.readByte

        CommandResult(
          restartCause,
          channelNumber
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    restartCause: RestartCause,
    channelNumber: Byte
  ) extends IpmiCommandResult

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {
      def encode(request: Command.type): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.ChassisRequest
    val commandCode = CommandCode(0x07)
  }

}
