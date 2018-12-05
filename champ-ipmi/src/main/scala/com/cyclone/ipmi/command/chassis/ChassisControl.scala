package com.cyclone.ipmi.command.chassis

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Chassis Control command and response
  */
object ChassisControl {

  object CommandResult extends IpmiCommandResult {
    implicit val decoder: Decoder[CommandResult.type] = new Decoder[CommandResult.type] {
      def decode(data: ByteString): CommandResult.type = CommandResult
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult.type] =
      StatusCodeTranslator[CommandResult.type]()
  }

  sealed trait Control

  object Control {

    implicit val coder: Coder[Control] = new Coder[Control] {

      def encode(a: Control): ByteString = {
        a match {
          case PowerDown                   => ByteString(0x00)
          case PowerUp                     => ByteString(0x01)
          case PowerCycle                  => ByteString(0x02)
          case HardReset                   => ByteString(0x03)
          case PulseDiagnosticInterrupt    => ByteString(0x04)
          case EmulateFatalOverTemperature => ByteString(0x05)
          case _                           => ByteString(0xFF)
        }
      }
    }

    case object PowerDown extends Control

    case object PowerUp extends Control

    case object PowerCycle extends Control

    case object HardReset extends Control

    case object PulseDiagnosticInterrupt extends Control

    case object EmulateFatalOverTemperature extends Control

    case object Reserved extends Control

  }

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= control.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult.type] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult.type]

  }

  case class Command(control: Control) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.ChassisRequest
    val commandCode = CommandCode(0x02)
  }

}
