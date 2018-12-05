package com.cyclone.ipmi.command.ipmiMessagingSupport

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.StatusCodeError
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.SessionId.ManagedSystemSessionId
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Close Session command and response
  */
object CloseSession {

  object CommandResult extends IpmiCommandResult {
    implicit val decoder: Decoder[CommandResult.type] = new Decoder[CommandResult.type] {
      def decode(data: ByteString): CommandResult.type = CommandResult
    }

    case object InvalidSessionId extends StatusCodeError {
      val code = StatusCode(0x87.toByte)
      val message = "Invalid Session ID in request"
    }

    case object InvalidSessionHandleId extends StatusCodeError {
      val code = StatusCode(0x88.toByte)
      val message = "Invalid Session Handle in request"
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult.type] = StatusCodeTranslator[CommandResult.type] {
      case InvalidSessionId.code       => InvalidSessionId
      case InvalidSessionHandleId.code => InvalidSessionHandleId
    }
  }

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._
        val b = new ByteStringBuilder

        b ++= managedSystemSessionId.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult.type] = CommandResultCodec.commandResultCodecFor[Command, CommandResult.type]
  }

  case class Command(
    managedSystemSessionId: ManagedSystemSessionId)
    extends IpmiStandardCommand {
    val networkFunction: NetworkFunction = NetworkFunction.ApplicationRequest
    val commandCode = CommandCode(0x3c)
  }

}


