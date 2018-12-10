package com.cyclone.ipmi.command.oem.fujitsu.s2s3

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * GetErrorLed command and response
  */
object GetErrorLed {

  sealed trait ErrorLedState

  object ErrorLedState {
    implicit val decoder: Decoder[ErrorLedState] = new Decoder[ErrorLedState] {

      def decode(data: ByteString): ErrorLedState = data(0).toUnsignedInt match {
        case 0x00 => OffOff
        case 0x01 => OffOn
        case 0x02 => OffBlink
        case 0x03 => OnOff
        case 0x04 => OnOn
        case 0x05 => OnBlink
        case 0x06 => BlinkOff
        case 0x07 => BlinkOn
        case 0x08 => BlinkBlink
      }
    }

    implicit val encoder: Coder[ErrorLedState] = new Coder[ErrorLedState] {

      def encode(a: ErrorLedState): ByteString = {
        a match {
          case OffOff     => ByteString(0x00)
          case OffOn      => ByteString(0x01)
          case OffBlink   => ByteString(0x02)
          case OnOff      => ByteString(0x03)
          case OnOn       => ByteString(0x04)
          case OnBlink    => ByteString(0x05)
          case BlinkOff   => ByteString(0x06)
          case BlinkOn    => ByteString(0x07)
          case BlinkBlink => ByteString(0x08)
        }
      }
    }

    case object OffOff extends ErrorLedState {
      val message = "Customer Self Service LED: OFF, Global error LED: OFF"
    }

    case object OffOn extends ErrorLedState {
      val message = "Customer Self Service LED: OFF, Global error LED: ON"
    }

    case object OffBlink extends ErrorLedState {
      val message = "Customer Self Service LED: OFF, Global error LED: BLINK"
    }

    case object OnOff extends ErrorLedState {
      val message = "Customer Self Service LED: ON, Global error LED: OFF"
    }

    case object OnOn extends ErrorLedState {
      val message = "Customer Self Service LED: ON, Global error LED: ON"
    }

    case object OnBlink extends ErrorLedState {
      val message = "Customer Self Service LED: ON, Global error LED: BLINK"
    }

    case object BlinkOff extends ErrorLedState {
      val message = "Customer Self Service LED: BLINK, Global error LED: OFF"
    }

    case object BlinkOn extends ErrorLedState {
      val message = "Customer Self Service LED: BLINK, Global error LED: ON"
    }

    case object BlinkBlink extends ErrorLedState {
      val message = "Customer Self Service LED: BLINK, Global error LED: BLINK"
    }

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        /*val requestType = */
        is.read(3)
          .as[IanaEnterpriseNumber] // Should match the IANA value present in the request (0x80 0x28 0x00 in this case)

        val ledState = is.readByte.as[ErrorLedState]

        CommandResult(ledState)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(ledState: ErrorLedState) extends IpmiCommandResult

  case object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {

      def encode(request: Command.type): ByteString = {

        val b = new ByteStringBuilder

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin

        b += 0xb3.toByte // Command Specifier

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.OemRequest
    val commandCode = CommandCode(0xf5.toByte)
  }

}
