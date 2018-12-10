package com.cyclone.ipmi.command.systemEventLog

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import org.joda.time.Instant

/**
  * Get SEL Info command and response
  */
object GetSelInfo {

  object OperationSupport {
    implicit val decoder: Decoder[OperationSupport] = new Decoder[OperationSupport] {

      def decode(data: ByteString): OperationSupport = {
        val byte = data(0)

        OperationSupport(
          standbyAllowed = byte.bit7,
          overflow = byte.bit7,
          reserved6 = byte.bit6,
          reserved5 = byte.bit5,
          reserved4 = byte.bit4,
          deleteSELSupported = byte.bit3,
          partialAddSELSupported = byte.bit2,
          reserveSELSupported = byte.bit1,
          getSELAllocationCommandSupported = byte.bit0
        )
      }
    }
  }

  case class OperationSupport(
    standbyAllowed: Boolean,
    overflow: Boolean,
    reserved6: Boolean,
    reserved5: Boolean,
    reserved4: Boolean,
    deleteSELSupported: Boolean,
    partialAddSELSupported: Boolean,
    reserveSELSupported: Boolean,
    getSELAllocationCommandSupported: Boolean
  )

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val selVersion = is.readByte.as[SelVersion]

        val entries = is.read(2).as[Short]

        val freeSpace = is.read(2).as[Short]

        // Most recent addition timestamp. LS byte first.
        // Returns FFFF_FFFFh if no SEL entries have ever been made or if a component update or error caused the retained value to be lost.
        val mostRecentAdditionTimestamp = is.read(4).as[Instant]

        // Most recent erase timestamp. Last time that one or more entries were deleted from the log. LS byte first.
        val mostRecentEraseTimestamp = is.read(4).as[Instant]

        val operationSupport = is.readByteOptional.map(_.as[OperationSupport])

        CommandResult(
          selVersion,
          entries,
          freeSpace,
          mostRecentAdditionTimestamp,
          mostRecentEraseTimestamp,
          operationSupport
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    selVersion: SelVersion,
    entries: Short,
    freeSpace: Short,
    mostRecentAdditionTimestamp: Instant,
    mostRecentEraseTimestamp: Instant,
    operationSupport: Option[OperationSupport]
  ) extends IpmiCommandResult

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {

      def encode(request: Command.type): ByteString =
        ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.StorageRequest
    val commandCode = CommandCode(0x40)
  }

}
