package com.cyclone.ipmi.command.chassis

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.global.DeviceAddress
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * Get Chassis Capabilities command and response
  */
object GetChassisCapabilities {

  object Capabilities {
    implicit val decoder: Decoder[Capabilities] = new Decoder[Capabilities] {
      def decode(data: ByteString): Capabilities = {
        val byte = data(0)

        Capabilities(
          powerProvidesInterlock = byte.bit3,
          providesDiagnosticInterrupt = byte.bit2,
          providesFrontPanelLockout = byte.bit1,
          chassisProvidesIntrusion = byte.bit0)
      }
    }
  }

  case class Capabilities(
    powerProvidesInterlock: Boolean,
    providesDiagnosticInterrupt: Boolean,
    providesFrontPanelLockout: Boolean,
    chassisProvidesIntrusion: Boolean)

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val capabilities = is.readByte.as[Capabilities]

        val chassisFRUInfoDeviceAddress = is.readByte.as[DeviceAddress]

        val chassisSDRDeviceAddress = is.readByte.as[DeviceAddress]
        val chassisSELDeviceAddress = is.readByte.as[DeviceAddress]
        val chassisSystemManagementDeviceAddress = is.readByte.as[DeviceAddress]

        val chassisBridgeDeviceAddress = is.readByteOptional.map(_.as[DeviceAddress])

        CommandResult(
          capabilities,
          chassisFRUInfoDeviceAddress,
          chassisSDRDeviceAddress,
          chassisSELDeviceAddress,
          chassisSystemManagementDeviceAddress,
          chassisBridgeDeviceAddress
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    capabilities: Capabilities,
    chassisFRUInfoDeviceAddress: DeviceAddress,
    chassisSDRDeviceAddress: DeviceAddress,
    chassisSELDeviceAddress: DeviceAddress,
    chassisSystemManagementDeviceAddress: DeviceAddress,
    chassisBridgeDeviceAddress: Option[DeviceAddress]) extends IpmiCommandResult

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {
      def encode(request: Command.type): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] = CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.ChassisRequest
    val commandCode = CommandCode(0x00)
  }

}
