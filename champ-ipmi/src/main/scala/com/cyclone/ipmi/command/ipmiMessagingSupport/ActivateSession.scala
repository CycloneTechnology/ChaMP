package com.cyclone.ipmi.command.ipmiMessagingSupport

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.SessionId.ManagedSystemSessionId
import com.cyclone.ipmi.protocol.packet._
import com.cyclone.ipmi.protocol.security.AuthenticationType
import com.cyclone.ipmi.{PrivilegeLevel, StatusCodeError}

object ActivateSession {

  case object NoSessionSlotAvailable extends StatusCodeError {
    val code = StatusCode(0x81.toByte)
    val message = "No session slot available (BMC cannot accept any more sessions)"
  }

  case object NoSlotForUser extends StatusCodeError {
    val code = StatusCode(0x82.toByte)

    val message =
      "No slot available for given user. (Limit of user sessions allowed under that name has been reached)"
  }

  case object NoSlotForUserPrivilege extends StatusCodeError {
    val code = StatusCode(0x83.toByte)

    val message: String =
      """No slot available to support user due to maximum privilege capability.
        |(An implementation may only be able to support a certain number of
        |sessions based on what authentication resources are required. For
        |example, if User Level Authentication is disabled, an implementation
        |may be able to allow a larger number of users that are limited to User
        |Level privilege, than users that require higher privilege.""".stripMargin
  }

  case object SessionSequenceNumberOutOfRange extends StatusCodeError {
    val code = StatusCode(0x84.toByte)
    val message = "Session sequence number out of range"
  }

  case object InvalidSessionId extends StatusCodeError {
    val code = StatusCode(0x85.toByte)
    val message = "Invalid Session ID in request"
  }

  case object PrivilegeLevelTooHigh extends StatusCodeError {
    val code = StatusCode(0x86.toByte)
    val message = "Requested maximum privilege level exceeds user and/or channel privilege limit"
  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val authType = is.readByte.as[AuthenticationType]
        val sessionId = is.read(4).as[ManagedSystemSessionId]
        val seqNo = is.read(4).as[SessionSequenceNumber]
        val privilegeLevel = is.readByte.as[PrivilegeLevel]

        CommandResult(authType, sessionId, seqNo, privilegeLevel)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult] {
        case NoSessionSlotAvailable.code          => NoSessionSlotAvailable
        case NoSlotForUser.code                   => NoSlotForUser
        case NoSlotForUserPrivilege.code          => NoSlotForUserPrivilege
        case SessionSequenceNumberOutOfRange.code => SessionSequenceNumberOutOfRange
        case InvalidSessionId.code                => InvalidSessionId
        case PrivilegeLevelTooHigh.code           => PrivilegeLevelTooHigh
      }
  }

  /**
    * Response for [[ActivateSession.Command]].
    */
  case class CommandResult(
    authenticationType: AuthenticationType,
    managedSystemSessionId: ManagedSystemSessionId,
    initialSendSequenceNumber: SessionSequenceNumber,
    maximumPrivilegeLevel: PrivilegeLevel
  ) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= authType.toBin
        b ++= maximumPrivilegeLevel.toBin
        b ++= challengeString
        b ++= initialReceiveSequenceNumber.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  case class Command(
    authType: AuthenticationType,
    maximumPrivilegeLevel: PrivilegeLevel,
    challengeString: ByteString,
    initialReceiveSequenceNumber: SessionSequenceNumber = SessionSequenceNumber(1)
  ) extends IpmiStandardCommand {
    val networkFunction: NetworkFunction = NetworkFunction.ApplicationRequest
    val commandCode = CommandCode(0x3a)
  }

}
