package com.cyclone.ipmi.command.chassis

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * Set Power Cycle Interval command and response
  */
object SetPowerCycleInterval {

  object CommandResult extends IpmiCommandResult {
    implicit val decoder: Decoder[CommandResult.type] = new Decoder[CommandResult.type] {

      def decode(data: ByteString): CommandResult.type = CommandResult
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult.type] =
      StatusCodeTranslator[CommandResult.type]()
  }

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += powerCycleInterval

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult.type] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult.type]

  }

  case class Command(powerCycleInterval: Byte = 0x00) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.ChassisRequest
    val commandCode = CommandCode(0x0B)
  }

}
