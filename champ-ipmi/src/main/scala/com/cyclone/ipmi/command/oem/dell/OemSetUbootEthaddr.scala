package com.cyclone.ipmi.command.oem.dell

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * OemSetUbootEthaddr command and response
  *
  * This command is for Dedicated-NIC. After issuing the OEM
  * command, user must reset BMC manually.
  */
object OemSetUbootEthaddr {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {

        CommandResult()
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  // FIXME use case object CommandResult when no params (and elsewhere...)
  case class CommandResult() extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= macAddress // (Should be byte 1-17 in packet)
        b += 0x00.toByte // Byte 18: End Data - Must be 0x00

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  // FIXME change macAddress into ByteString that is 16 bytes long!
  case class Command(macAddress: ByteString) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.OemRequest
    val commandCode = CommandCode(0x21)
  }

}


