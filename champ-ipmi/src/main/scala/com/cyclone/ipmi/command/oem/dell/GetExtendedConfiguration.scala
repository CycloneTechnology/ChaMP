package com.cyclone.ipmi.command.oem.dell

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * GetExtendedConfiguration
  */
object GetExtendedConfiguration {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val configurationId = is.readByte
        val attributeId = is.readByte
        val index = is.readByte // valid for table object only
        val bytesReturned = is.readByte // (1 based)
        val parameterData = is.read(bytesReturned)

        CommandResult(configurationId, attributeId, index, parameterData)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    configurationId: Byte,
    attributeId: Byte,
    index: Byte,
    parameterData: ByteString
  ) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += reservationId
        b += configurationId
        b += attributeId
        b += index
        b += dataOffsetLSB
        b += dataOffsetMSB
        b += bytesToRead

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  // Note attributeId = 0x00 means read entire configuration data and bytestoRead = 0xFF means read entire configuration or attribute.
  case class Command(
    reservationId: Byte,
    configurationId: Byte,
    attributeId: Byte = 0x00.toByte,
    index: Byte,
    dataOffsetLSB: Byte,
    dataOffsetMSB: Byte,
    bytesToRead: Byte = 0xFF.toByte
  ) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.OemFree30hRequest
    val commandCode = CommandCode(0x02)
  }

}
