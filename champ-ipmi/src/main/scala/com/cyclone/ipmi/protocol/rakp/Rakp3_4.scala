package com.cyclone.ipmi.protocol.rakp

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{StatusCode, _}
import com.cyclone.ipmi.protocol.packet.SessionId.{ManagedSystemSessionId, RemoteConsoleSessionId}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiSessionActivationCommand, PayloadType}

/**
  * RAKP 3 and 4 request and response messages.
  *
  * See sec 13 (from 13.19)
  */
object Rakp3_4 {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        is.skip(2)

        val remoteConsoleSessionId = is.read(4).as[RemoteConsoleSessionId]
        val key = iterator.toByteString

        CommandResult(
          remoteConsoleSessionId = remoteConsoleSessionId,
          integrityCode = key
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult](RmcpPlusAndRakpStatusCodeErrors.lookup)
  }

  /**
    * RAKP response message 4
    *
    * @param remoteConsoleSessionId out session id
    * @param integrityCode          integrity code validated using SIK for key.
    */
  case class CommandResult(
    remoteConsoleSessionId: RemoteConsoleSessionId,
    integrityCode: ByteString
  ) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= statusCode.toBin
        b += 0
        b += 0

        b ++= managedSystemSessionId.toBin

        if (!statusCode.isError) b ++= keyExchangeAuthenticationCode

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, Rakp3_4.CommandResult]
  }

  /**
    * RAKP request message 3
    *
    * @param statusCode                    status code in response to received [[Rakp1_2.CommandResult]]
    * @param managedSystemSessionId        device's session id
    * @param keyExchangeAuthenticationCode HMAC hash of shared random numbers etc
    */
  case class Command(
    statusCode: StatusCode,
    managedSystemSessionId: ManagedSystemSessionId,
    keyExchangeAuthenticationCode: ByteString
  ) extends IpmiSessionActivationCommand {
    val payloadType: PayloadType = PayloadType.Rakp3
  }

}
