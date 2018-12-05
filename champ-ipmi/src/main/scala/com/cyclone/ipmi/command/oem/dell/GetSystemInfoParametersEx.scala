package com.cyclone.ipmi.command.oem.dell

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetSystemInfoParameters.ParameterSelector
import com.cyclone.ipmi.command.ipmiMessagingSupport.ParameterRevision
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Extension to GetSystemInfoParameters command for getting 11G/12G Mac addresses
  * (selector param 0xda) (anything else?). Includes two extra bytes in the command:
  *
  * byte 5 : offset into data to read
  * byte 6 : length of data to read
  */
object GetSystemInfoParametersEx {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val parameterRevision = is.readByte.as[ParameterRevision]

        val responseData = iterator.toByteString

        CommandResult(parameterRevision, responseData)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    parameterRevision: ParameterRevision,
    responseData: ByteString)
    extends IpmiCommandResult


  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += 0x00.toByte // bit7 0b = Get Parameter, 1b = Get Parameter Revision Only, bits0to6 - reserved
        b ++= parameterSelector.toBin
        b += setSelector.toByte
        b += blockSelector.toByte
        b += readOffset.toByte
        b += readLength.toByte

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  // TODO if used other than for macs make generic as have in main GetSystemInfoParameters command?
  case class Command(
    parameterSelector: ParameterSelector,
    readOffset: Int = 0,
    readLength: Int = 0,
    setSelector: Int = 0,
    blockSelector: Int = 0) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.ApplicationRequest
    val commandCode = CommandCode(0x59)
  }

}
