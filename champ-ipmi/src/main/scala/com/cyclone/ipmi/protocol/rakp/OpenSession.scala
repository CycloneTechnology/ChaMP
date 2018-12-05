package com.cyclone.ipmi.protocol.rakp

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.PrivilegeLevel
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command._
import com.cyclone.ipmi.protocol.packet.SessionId.{ManagedSystemSessionId, RemoteConsoleSessionId}
import com.cyclone.ipmi.protocol.packet.{
  CommandResultCodec,
  IpmiCommandResult,
  IpmiSessionActivationCommand,
  PayloadType
}
import com.cyclone.ipmi.protocol.security._

import scalaz.Scalaz._

/**
  * Open session request and response messages
  */
object OpenSession {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val privilegeLevel = is.readByte.as[PrivilegeLevel]

        is.skip(1)

        val remoteConsoleSessionId = is.read(4).as[RemoteConsoleSessionId]
        val managedSystemSessionId = is.read(4).as[ManagedSystemSessionId]

        is.skip(4)
        val authAlg = is.readByte.as[AuthenticationAlgorithm]
        is.skip(3)

        is.skip(4)
        val integAlg = is.readByte.as[IntegrityAlgorithm]
        is.skip(3)

        is.skip(4)
        val confAlg = is.readByte.as[ConfidentialityAlgorithm]
        is.skip(3)

        CommandResult(
          privilegeLevel,
          remoteConsoleSessionId = remoteConsoleSessionId,
          managedSystemSessionId = managedSystemSessionId,
          authAlg,
          integAlg,
          confAlg
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult](RmcpPlusAndRakpStatusCodeErrors.lookup)
  }

  case class CommandResult(
    privilegeLevel: PrivilegeLevel,
    remoteConsoleSessionId: RemoteConsoleSessionId,
    managedSystemSessionId: ManagedSystemSessionId,
    authenticationAlgorithm: AuthenticationAlgorithm,
    integrityAlgorithm: IntegrityAlgorithm,
    confidentialityAlgorithm: ConfidentialityAlgorithm
  ) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b ++= requestedPrivilegeLevel.toBin
        b += 0
        b += 0
        b ++= remoteConsoleSessionId.toBin

        // Authentication 'payload'
        b += 0
        b += 0
        b += 0
        b += 0x08
        b ++= cipherSuite.authenticationAlgorithm.toBin
        b += 0
        b += 0
        b += 0

        // Integrity 'payload'
        b += 0x01
        b += 0
        b += 0
        b += 0x08
        b ++= cipherSuite.integrityAlgorithm.toBin
        b += 0
        b += 0
        b += 0

        // Confidentiality 'payload'
        b += 0x02
        b += 0
        b += 0
        b += 0x08
        b ++= cipherSuite.confidentialityAlgorithm.toBin
        b += 0
        b += 0
        b += 0

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, OpenSession.CommandResult]
  }

  case class Command(
    remoteConsoleSessionId: RemoteConsoleSessionId,
    cipherSuite: CipherSuite,
    requestedPrivilegeLevel: PrivilegeLevel
  ) extends IpmiSessionActivationCommand {
    def payloadType: PayloadType = PayloadType.OpenSessionReq
  }

}
