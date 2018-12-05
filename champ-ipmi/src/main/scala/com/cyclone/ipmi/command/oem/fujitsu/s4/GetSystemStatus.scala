package com.cyclone.ipmi.command.oem.fujitsu.s4

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}
import org.joda.time.Instant

/**
  * GetSystemStatus command and response
  */
object GetSystemStatus {

  object SystemStatus {
    implicit val decoder: Decoder[SystemStatus] = new Decoder[SystemStatus] {

      def decode(data: ByteString): SystemStatus = {
        val byte = data(0)

        SystemStatus(
          systemOn = byte.bit7,
          reserved6 = byte.bit6,
          reserved5 = byte.bit5,
          selEntriesAvailable = byte.bit4,
          reserved3 = byte.bit3,
          watchdogActive = byte.bit2,
          agentConnected = byte.bit1,
          postState = byte.bit0
        )
      }
    }
  }

  case class SystemStatus(
    systemOn: Boolean,
    reserved6: Boolean,
    reserved5: Boolean,
    selEntriesAvailable: Boolean,
    reserved3: Boolean,
    watchdogActive: Boolean,
    agentConnected: Boolean,
    postState: Boolean
  )

  object Signaling {
    implicit val decoder: Decoder[Signaling] = new Decoder[Signaling] {

      def decode(data: ByteString): Signaling = {
        val byte = data(0)

        Signaling(
          localizeLed = byte.bit7,
          reserved6 = byte.bit6,
          reserved5 = byte.bit5,
          reserved4 = byte.bit4,
          cssLed0 = byte.bit3,
          cssLed1 = byte.bit2,
          globalErrorLed0 = byte.bit1,
          globalErrorLed1 = byte.bit0
        )
      }
    }
  }

  case class Signaling(
    localizeLed: Boolean,
    reserved6: Boolean,
    reserved5: Boolean,
    reserved4: Boolean,
    cssLed0: Boolean,
    cssLed1: Boolean,
    globalErrorLed0: Boolean,
    globalErrorLed1: Boolean
  )

  object Notifications {
    implicit val decoder: Decoder[Notifications] = new Decoder[Notifications] {

      def decode(data: ByteString): Notifications = {
        val byte = data(0)

        Notifications(
          selModifiedNewSelEntry = byte.bit7,
          selModifiedSelCleared = byte.bit6,
          sdrModified = byte.bit5,
          nonvolatileIpmiVariableModified = byte.bit4,
          configSpaceModified = byte.bit3,
          reserved2 = byte.bit2,
          reserved1 = byte.bit1,
          newOutputOnLocalViewDisplay = byte.bit0
        )
      }
    }
  }

  case class Notifications(
    selModifiedNewSelEntry: Boolean,
    selModifiedSelCleared: Boolean,
    sdrModified: Boolean,
    nonvolatileIpmiVariableModified: Boolean,
    configSpaceModified: Boolean,
    reserved2: Boolean,
    reserved1: Boolean,
    newOutputOnLocalViewDisplay: Boolean
  )

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        /*val requestType = */
        is.read(3)
          .as[IanaEnterpriseNumber] // Should match the IANA value present in the request (0x80 0x28 0x00 in this case)

        val systemStatus = is.readByteOptional.map(_.as[SystemStatus])
        val signaling = is.readByteOptional.map(_.as[Signaling])
        val notifications = is.readByteOptional.map(_.as[Notifications])
        val postCode = is.readByte

        CommandResult(systemStatus, signaling, notifications, postCode)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    systemStatus: Option[SystemStatus],
    signaling: Option[Signaling],
    notifications: Option[Notifications],
    postCode: Byte
  ) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin

        b += 0x10.toByte // Command Specifier
        b ++= timestamp.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command(timestamp: Instant = new Instant()) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuGroupRequest
    val commandCode = CommandCode(0xf5.toByte)
  }

}
