package com.cyclone.ipmi.command.plaftformEventFilteringAndAlerting

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.StatusCodeError
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Set Last Processed Event ID command and response
  */
object SetLastProcessedEventId {

  object CommandResult extends IpmiCommandResult {
    implicit val decoder: Decoder[CommandResult.type] = new Decoder[CommandResult.type] {
      def decode(data: ByteString): CommandResult.type = CommandResult
    }

    case object CannotExecuteCommand extends StatusCodeError {
      val code = StatusCode(0x81.toByte)
      val message = "Cannot execute command, SEL erase in progress"
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult.type] = StatusCodeTranslator[CommandResult.type] {
      case CannotExecuteCommand.code => CannotExecuteCommand
    }
  }

  sealed trait ProcessdBy

  object ProcessdBy {

    case object Software extends ProcessdBy

    case object BMC extends ProcessdBy

    implicit val codec: Coder[ProcessdBy] = new Coder[ProcessdBy] {
      def encode(s: ProcessdBy): ByteString = {
        s match {
          case Software => ByteString(0)
          case BMC      => ByteString(1)
        }

      }
    }
  }

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        // Assuming only 0 or 1 in first request byte as spec says bits 7:1 in this byte are reserved, so we assume they are all 0
        b ++= processedBy.toBin
        b ++= recordId.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult.type] = CommandResultCodec.commandResultCodecFor[Command, CommandResult.type]
  }

  case class Command(processedBy: ProcessdBy, recordId: Short) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.SensorRequest
    val commandCode = CommandCode(0x14)
  }

}
