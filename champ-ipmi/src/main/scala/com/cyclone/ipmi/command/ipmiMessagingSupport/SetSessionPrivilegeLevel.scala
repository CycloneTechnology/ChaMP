package com.cyclone.ipmi.command.ipmiMessagingSupport

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import com.cyclone.ipmi.{PrivilegeLevel, StatusCodeError}

object SetSessionPrivilegeLevel {

  case object LevelNotAvailableToUser extends StatusCodeError {
    val code = StatusCode(0x80.toByte)
    val message = "Requested privilege level not available for this user"
  }

  case object LevelExceedsLimit extends StatusCodeError {
    val code = StatusCode(0x81.toByte)
    val message = "Requested privilege level exceeds Channel and/or User Privilege Limit"
  }

  case object CannotDisableAuthentication extends StatusCodeError {
    val code = StatusCode(0x82.toByte)
    val message = "Cannot disable User Level authentication"
  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val privilegeLevel = is.readByte.as[PrivilegeLevel]

        CommandResult(privilegeLevel)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult] {
        case LevelNotAvailableToUser.code     => LevelNotAvailableToUser
        case LevelExceedsLimit.code           => LevelExceedsLimit
        case CannotDisableAuthentication.code => CannotDisableAuthentication
      }
  }

  /**
    * Response for [[SetSessionPrivilegeLevel.Command]].
    */
  case class CommandResult(
    newPrivilegeLevel: PrivilegeLevel
  ) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= privilegeLevel.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  case class Command(privilegeLevel: PrivilegeLevel) extends IpmiStandardCommand {
    val networkFunction: NetworkFunction = NetworkFunction.ApplicationRequest
    val commandCode = CommandCode(0x3b)
  }

}
