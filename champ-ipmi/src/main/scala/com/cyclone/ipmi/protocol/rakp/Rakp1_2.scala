package com.cyclone.ipmi.protocol.rakp

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command._
import com.cyclone.ipmi.protocol.packet.SessionId.{ManagedSystemSessionId, RemoteConsoleSessionId}
import com.cyclone.ipmi.protocol.packet.{
  CommandResultCodec,
  IpmiCommandResult,
  IpmiSessionActivationCommand,
  PayloadType
}
import com.cyclone.ipmi.{PrivilegeLevel, Username}

/**
  * RAKP 1 and 2 request and response messages
  *
  * See sec 13 (from 13.19)
  */
object Rakp1_2 {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        is.skip(2)
        val remoteConsoleSessionId = is.read(4).as[RemoteConsoleSessionId]
        val managedSystemRandomNumber = is.read(16)
        val managedSystemGuid = is.read(16)

        val key = iterator.toByteString

        CommandResult(
          remoteConsoleSessionId,
          managedSystemRandomNumber = managedSystemRandomNumber,
          managedSystemGuid = managedSystemGuid,
          keyExchangeAuthCode = key
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult](RmcpPlusAndRakpStatusCodeErrors.lookup)
  }

  case class CommandResult(
    remoteConsoleSessionId: RemoteConsoleSessionId,
    managedSystemRandomNumber: ByteString,
    managedSystemGuid: ByteString,
    keyExchangeAuthCode: ByteString
  ) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += 0
        b += 0
        b += 0

        b ++= managedSystemSessionId.toBin

        b ++= consoleRandomNumber

        b += requestedMaximumPrivilegeLevel.toByte.set4

        b += 0
        b += 0

        b ++= username.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, Rakp1_2.CommandResult]
  }

  case class Command(
    managedSystemSessionId: ManagedSystemSessionId,
    consoleRandomNumber: ByteString,
    requestedMaximumPrivilegeLevel: PrivilegeLevel,
    username: Username
  ) extends IpmiSessionActivationCommand {
    def payloadType: PayloadType = PayloadType.Rakp1
  }

}
