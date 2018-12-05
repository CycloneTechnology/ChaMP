package com.cyclone.ipmi.command.ipmiMessagingSupport

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.PrivilegeLevel
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}
import com.cyclone.ipmi.protocol.security.AuthenticationTypes

object GetChannelAuthenticationCapabilities {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val chan = is.readByte

        val authTypeSupportByte = is.readByte
        val supportsV20 = authTypeSupportByte.bit7
        val authTypes = authTypeSupportByte.as[AuthenticationTypes]

        val byte4 = is.readByte
        val kgRequired = byte4.bit5

        val perMessageAuthEnabled = byte4.bit4
        val userLevelAuthEnabled = byte4.bit3

        val nonNullUsernamesEnabled = byte4.bit2
        val nullUsernamesEnabled = byte4.bit1
        val anonLoginEnabled = byte4.bit0

        // Ignore extended caps (byte5)
        // Ignore OEM ID (bytes 6:8) and data (byte 9)

        CommandResult(
          channelNumber = chan,
          supportsV20 = supportsV20,
          authenticationTypes = authTypes,
          kgRequired = kgRequired,
          perMessageAuthEnabled = perMessageAuthEnabled,
          userLevelAuthEnabled = userLevelAuthEnabled,
          nonNullUsernamesEnabled = nonNullUsernamesEnabled,
          nullUsernamesEnabled = nullUsernamesEnabled,
          anonLoginEnabled = anonLoginEnabled
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  /**
    * Response for [[GetChannelAuthenticationCapabilities.Command]].
    *
    * Note (spec sec 22.15) cipher suites are returned in data of multiple responses for
    * different listIndex values (0, 1, ...).
    *
    * The encoded records may be split between multiple records.
    */
  case class CommandResult(
    channelNumber: Byte,
    supportsV20: Boolean,
    authenticationTypes: AuthenticationTypes,
    kgRequired: Boolean,
    perMessageAuthEnabled: Boolean,
    userLevelAuthEnabled: Boolean,
    nonNullUsernamesEnabled: Boolean,
    nullUsernamesEnabled: Boolean,
    anonLoginEnabled: Boolean
  ) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += (channelNumber | getV20ExtendedData.toBit7).toByte
        b ++= requestedPrivilegeLevel.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  case class Command(
    requestedPrivilegeLevel: PrivilegeLevel,
    getV20ExtendedData: Boolean = true
  ) extends IpmiStandardCommand {
    val networkFunction: NetworkFunction = NetworkFunction.ApplicationRequest
    val commandCode = CommandCode(0x38.toByte)
  }

}
