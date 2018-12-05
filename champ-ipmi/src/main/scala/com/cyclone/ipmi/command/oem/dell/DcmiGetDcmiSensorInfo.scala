package com.cyclone.ipmi.command.oem.dell

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command._
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * DCMIGetDcmiSensorInfo
  */
object DcmiGetDcmiSensorInfo {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val identification = is.readByte
        val totalNum = is.readByte
        val recordIdNum = is.readByte
        val sdrRecId = is.read(recordIdNum)

        CommandResult(
          identification,
          totalNum,
          sdrRecId)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    identification: Byte,
    totalNum: Byte,
    sdrRecId: ByteString) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += identification
        b += sensorType
        b += entityId
        b += entityInstance
        b += start

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  case class Command(identification: Byte, sensorType: Byte, entityId: Byte, entityInstance: Byte, start: Byte) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.MediaSpecificRequest
    val commandCode = CommandCode(0x07)
  }

}
