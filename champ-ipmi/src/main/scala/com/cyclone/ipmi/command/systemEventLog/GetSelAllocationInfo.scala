package com.cyclone.ipmi.command.systemEventLog

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Get SEL Allocation Info command and response
  */
object GetSelAllocationInfo {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val numberPossibleAllocationBytes = is.read(2).as[Short]

        val allocationUnitSizeInBytes = is.read(2).as[Short]

        val numberOfFreeAllocationUnits = is.read(2).as[Short]

        val largestFreeBlockInAllocationUnits = is.read(2).as[Short]

        val maximumRecordSizeInAllocationUnits = is.readByte.toUnsignedInt

        CommandResult(
          numberPossibleAllocationBytes,
          allocationUnitSizeInBytes,
          numberOfFreeAllocationUnits,
          largestFreeBlockInAllocationUnits,
          maximumRecordSizeInAllocationUnits
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    numberPossibleAllocationBytes: Short,
    allocationUnitSizeInBytes: Short,
    numberOfFreeAllocationUnits: Short,
    largestFreeBlockInAllocationUnits: Short,
    maximumRecordSizeInAllocationUnits: Int
  ) extends IpmiCommandResult

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {

      def encode(request: Command.type): ByteString =
        ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.StorageRequest
    val commandCode = CommandCode(0x41)
  }

}
