package com.cyclone.ipmi.command.ipmiMessagingSupport

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.SessionId.ManagedSystemSessionId
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}
import com.cyclone.ipmi.protocol.security.AuthenticationType
import com.cyclone.ipmi.{StatusCodeError, UsernameV15}

object GetSessionChallenge {

  case object InvalidUserName extends StatusCodeError {
    val code = StatusCode(0x81.toByte)
    val message = "Invalid user name"
  }

  case object NullUserNameDisabled extends StatusCodeError {
    val code = StatusCode(0x82.toByte)
    val message = "Null user name (User 1) not enabled"
  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val sessionId = is.read(4).as[ManagedSystemSessionId]
        val challenge = is.read(16)

        CommandResult(sessionId, challenge)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult] {
      case InvalidUserName.code      => InvalidUserName
      case NullUserNameDisabled.code => NullUserNameDisabled
    }
  }

  /**
    * Response for [[GetSessionChallenge.Command]].
    */
  case class CommandResult(
    managedSystemSessionId: ManagedSystemSessionId,
    challengeData: ByteString
  ) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= authType.toBin
        b ++= username.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  case class Command(
    authType: AuthenticationType,
    username: UsernameV15
  )
    extends IpmiStandardCommand {
    val networkFunction: NetworkFunction = NetworkFunction.ApplicationRequest
    val commandCode = CommandCode(0x39)
  }

}
