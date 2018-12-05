package com.cyclone.ipmi.command.chassis

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Set Power Restore Policy command and response
  */
object SetPowerRestorePolicy {

  sealed trait PowerRestorePolicy

  object PowerRestorePolicy {
    implicit val encoder: Coder[PowerRestorePolicy] = new Coder[PowerRestorePolicy] {
      def encode(a: PowerRestorePolicy): ByteString = {
        a match {
          case ChassisAlwaysOffAfterAcApplied      => ByteString(0x00)
          case ChassisPowerRestoredToPreviousState => ByteString(0x01)
          case ChassisAlwaysPowersOnAfterAcApplid  => ByteString(0x02)
          case NoChangeJustGetPresentPolicy        => ByteString(0x03)
        }
      }
    }

    case object ChassisAlwaysOffAfterAcApplied extends PowerRestorePolicy

    case object ChassisPowerRestoredToPreviousState extends PowerRestorePolicy

    case object ChassisAlwaysPowersOnAfterAcApplid extends PowerRestorePolicy

    case object NoChangeJustGetPresentPolicy extends PowerRestorePolicy

  }

  object PowerRestorePolicySupport {
    implicit val decoder: Decoder[PowerRestorePolicySupport] = new Decoder[PowerRestorePolicySupport] {
      def decode(data: ByteString): PowerRestorePolicySupport = {
        val byte = data(0)

        PowerRestorePolicySupport(
          chassisSupportsAlwaysPoweringUpAfterAcReturns = byte.bit2,
          chassisSupportsAlwaysRestoringPowerToStateBeforeAcLost = byte.bit1,
          chassisSupportsPowerRemainingOffAfterAcReturns = byte.bit0)
      }
    }
  }

  case class PowerRestorePolicySupport(
    chassisSupportsAlwaysPoweringUpAfterAcReturns: Boolean,
    chassisSupportsAlwaysRestoringPowerToStateBeforeAcLost: Boolean,
    chassisSupportsPowerRemainingOffAfterAcReturns: Boolean)

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val powerRestorePolicySupport = is.readByteOptional.map(_.as[PowerRestorePolicySupport])

        CommandResult(powerRestorePolicySupport)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(powerRestorePolicySupport: Option[PowerRestorePolicySupport]) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= restorePolicy.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command(restorePolicy: PowerRestorePolicy) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.ChassisRequest
    val commandCode = CommandCode(0x06)
  }

}


