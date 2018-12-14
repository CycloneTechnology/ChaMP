package com.cyclone.ipmi.command.global

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import com.cyclone.ipmi.protocol.sdr.DeviceCapabilities

/**
  * Get Device ID command and response
  */
object GetDeviceId {

  sealed trait DeviceAvailable

  object DeviceAvailable {

    case object NormalOperation extends DeviceAvailable

    case object DeviceFirmware extends DeviceAvailable

    implicit val codec: Decoder[DeviceAvailable] = new Decoder[DeviceAvailable] {

      def decode(data: ByteString): DeviceAvailable =
        if (data(0).bit7) DeviceFirmware else NormalOperation
    }
  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val deviceId = is.readByte.as[DeviceId]

        val deviceRevisionByte = is.readByte
        val deviceSdrsProvided = deviceRevisionByte.bit7
        val deviceRevision = deviceRevisionByte.as[DeviceRevision]

        val firmwareRevisionBytes = is.read(2)

        val firmwareRevision = firmwareRevisionBytes.as[FirmwareRevision]
        val deviceAvailable = firmwareRevisionBytes(0).as[DeviceAvailable]

        val ipmiVersion = is.readByte.as[IpmiVersion]

        val additionalDeviceSupport = is.readByte.as[DeviceCapabilities]
        val manufacturerId = is.read(3).as[IanaEnterpriseNumber]
        val productId = is.read(2).as[ProductId]

        val remaining = iterator.toByteString
        val auxillaryFirmwareRevisionInformation =
          if (remaining.length == 4)
            Some(remaining)
          else None

        CommandResult(
          deviceId,
          deviceSdrsProvided,
          deviceRevision,
          deviceAvailable,
          firmwareRevision,
          ipmiVersion,
          additionalDeviceSupport,
          manufacturerId,
          productId,
          auxillaryFirmwareRevisionInformation
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    deviceId: DeviceId,
    deviceSdrsProvided: Boolean,
    deviceRevision: DeviceRevision,
    deviceAvailable: DeviceAvailable,
    firmwareRevision: FirmwareRevision,
    ipmiVersion: IpmiVersion,
    deviceCapabilities: DeviceCapabilities,
    manufacturerId: IanaEnterpriseNumber,
    productId: ProductId,
    auxiliaryFirmwareRevisionInformation: Option[ByteString]
  ) extends IpmiCommandResult

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {

      def encode(request: Command.type): ByteString =
        ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.ApplicationRequest
    val commandCode = CommandCode(0x01)
  }

}
