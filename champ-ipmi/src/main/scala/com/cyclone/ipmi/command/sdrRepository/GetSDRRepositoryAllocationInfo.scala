package com.cyclone.ipmi.command.sdrRepository

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Get SDR Repository Allocation Info command and response
  */
object GetSDRRepositoryAllocationInfo {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val numberPossibleAllocationUnits = is.read(2).as[Short].toInt
        val allocationUnitSize = is.read(2).as[Short].toInt
        val numberOfFreeAllocationUnits = is.read(2).as[Short].toInt
        val largestFreeBlockInAllocationUnits = is.read(2).as[Short].toInt

        val maximumRecordSizeInAllocationUnits = is.readByte

        CommandResult(
          numberPossibleAllocationUnits,
          allocationUnitSize,
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
    numberPossibleAllocationUnits: Int,
    allocationUnitSize: Int,
    numberOfFreeAllocationUnits: Int,
    largestFreeBlockInAllocationUnits: Int,
    maximumRecordSizeInAllocationUnits: Byte
  ) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString =
        ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command() extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.StorageRequest
    val commandCode = CommandCode(0x21)
  }

}
