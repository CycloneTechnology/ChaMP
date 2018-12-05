package com.cyclone.ipmi.command.fruInventory

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.StatusCodeError
import com.cyclone.ipmi.codec.{Coder, _}
import com.cyclone.ipmi.command.global.DeviceId
import com.cyclone.ipmi.command.{CommandCode, StatusCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

import scala.concurrent.duration._

/**
  * Command to read FRU data
  */
object ReadFruData {

  case object FRUDeviceBusy extends StatusCodeError {
    val code = StatusCode(0x81.toByte)

    override val retryAfter = Some(30.millis)

    val message: String =
      """FRU device busy. The requested cannot be completed
        |because the implementation of the logical FRU device is in a
        |state where the FRU information is temporarily unavailable.
        |This could be due to a condition such as a loss of arbitration
        |if the FRU is implemented as a device on a shared bus.
        | """.stripMargin
  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val countReturned = is.readByte
        val requestedData = is.read(countReturned.toInt)

        CommandResult(requestedData)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult] {
        case FRUDeviceBusy.code => FRUDeviceBusy
      }
  }

  case class CommandResult(requestedData: ByteString) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= deviceId.toBin
        b ++= fruInventoryOffset.toBin
        b += countToRead.toByte

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command(deviceId: DeviceId, fruInventoryOffset: Short, countToRead: Int) extends IpmiStandardCommand {
    val networkFunction: NetworkFunction = NetworkFunction.StorageRequest
    val commandCode = CommandCode(0x11)
  }

}
