package com.cyclone.ipmi.command.plaftformEventFilteringAndAlerting

import akka.util.ByteString
import com.cyclone.ipmi.StatusCodeError
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}
import org.joda.time.Instant

/**
  * Get Last Processed Event ID command and response
  */
object GetLastProcessedEventId {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {

        val iterator = data.iterator
        val is = iterator.asInputStream

        val mostRecentAdditionTimestamp = is.read(4).as[Instant]

        // NOTE: Returns FFFFh if SEL is empty.
        val recordIdForLastRecordInSEL = is.read(2).as[Short]

        val lastSWProcessedEventRecordId = is.read(2).as[Short]

        // NOTE: Returns 0000h when event has been processed but could not be logged because the SEL is full or logging has been disabled.
        val lastBMCProcessedEventRecordId = is.read(2).as[Short]

        CommandResult(
          mostRecentAdditionTimestamp,
          recordIdForLastRecordInSEL,
          lastSWProcessedEventRecordId,
          lastBMCProcessedEventRecordId
        )
      }

    }

    case object CannotExecuteCommand extends StatusCodeError {
      val code = StatusCode(0x81.toByte)
      val message = "Cannot execute command, SEL erase in progress"
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult] {
        case CannotExecuteCommand.code => CannotExecuteCommand
      }
  }

  case class CommandResult(
    mostRecentAdditionTimestamp: Instant,
    recordIdForLastRecordInSEL: Short,
    lastSWProcessedEventRecordId: Short,
    lastBMCProcessedEventRecordId: Short
  ) extends IpmiCommandResult

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {

      def encode(request: Command.type): ByteString =
        ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.SensorRequest
    val commandCode = CommandCode(0x10)
  }

}
