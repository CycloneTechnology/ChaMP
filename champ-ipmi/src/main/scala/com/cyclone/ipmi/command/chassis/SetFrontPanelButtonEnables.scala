package com.cyclone.ipmi.command.chassis

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Set Front Panel Button Enables command and response
  */
object SetFrontPanelButtonEnables {

  object FrontPanelEnables {
    implicit val decoder: Decoder[FrontPanelEnables] = new Decoder[FrontPanelEnables] {
      def decode(data: ByteString): FrontPanelEnables = {
        val byte = data(0)

        FrontPanelEnables(
          reserved7 = byte.bit7,
          reserved6 = byte.bit6,
          reserved5 = byte.bit5,
          reserved4 = byte.bit4,
          disableStandbyButton = byte.bit3,
          disableDiagnosticInterruptButton = byte.bit2,
          disableResetButton = byte.bit1,
          disablePowerOffButton = byte.bit0)
      }
    }

    implicit val encoder: Coder[FrontPanelEnables] = new Coder[FrontPanelEnables] {
      def encode(a: FrontPanelEnables): ByteString = {
        ByteString((
          a.reserved7.toBit7.toUnsignedInt &
            a.reserved6.toBit6.toUnsignedInt &
            a.reserved5.toBit5.toUnsignedInt &
            a.reserved4.toBit4.toUnsignedInt &
            a.disableStandbyButton.toBit3.toUnsignedInt &
            a.disableDiagnosticInterruptButton.toBit2.toUnsignedInt &
            a.disableResetButton.toBit1.toUnsignedInt &
            a.disablePowerOffButton.toBit0.toUnsignedInt).toByte)
      }
    }
  }

  case class FrontPanelEnables(
    reserved7: Boolean,
    reserved6: Boolean,
    reserved5: Boolean,
    reserved4: Boolean,
    disableStandbyButton: Boolean,
    disableDiagnosticInterruptButton: Boolean,
    disableResetButton: Boolean,
    disablePowerOffButton: Boolean)

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {

        CommandResult()
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult() extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= frontPanelEnables.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command(frontPanelEnables: FrontPanelEnables) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.ChassisRequest
    val commandCode = CommandCode(0x0A)
  }

}


