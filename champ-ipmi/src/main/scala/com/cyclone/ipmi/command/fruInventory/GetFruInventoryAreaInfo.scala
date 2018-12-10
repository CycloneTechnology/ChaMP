package com.cyclone.ipmi.command.fruInventory

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.global.DeviceId
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * Get FRU Inventory Area Info command and response
  */
object GetFruInventoryAreaInfo {

  sealed trait DataAccessType

  object DataAccessType {
    implicit val decoder: Decoder[DataAccessType] = new Decoder[DataAccessType] {

      def decode(data: ByteString): DataAccessType =
        if (data(0).bit0) ByWords else ByBytes
    }

    case object ByBytes extends DataAccessType

    case object ByWords extends DataAccessType

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val fruInventoryAreaSize = is.read(2).as[Short]

        val deviceAccessType = is.readByte.as[DataAccessType]

        CommandResult(fruInventoryAreaSize, deviceAccessType)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(fruInventoryAreaSize: Int, dataAccessType: DataAccessType) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= deviceId.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command(deviceId: DeviceId) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.StorageRequest
    val commandCode = CommandCode(0x10)
  }

}
