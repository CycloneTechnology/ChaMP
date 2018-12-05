package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.global.{DeviceAddress, DeviceId}
import com.cyclone.ipmi.fru.{FruDescriptor, FruType}

// Supporting cast of thousands...
sealed trait EventMessageAndControllerInitialize

object EventMessageAndControllerInitialize {
  implicit val decoder: Decoder[EventMessageAndControllerInitialize] =
    new Decoder[EventMessageAndControllerInitialize] {

      def decode(data: ByteString): EventMessageAndControllerInitialize =
        data(0).bits0To1.toUnsignedInt match {
          case 0 => EnableEventMessageGenerationFromController
          case 1 => DisableEventMessageGenerationFromController
          case 2 => DoNotInitializeController
          case 3 => Reserved
        }
    }

  case object EnableEventMessageGenerationFromController extends EventMessageAndControllerInitialize

  case object DisableEventMessageGenerationFromController extends EventMessageAndControllerInitialize

  case object DoNotInitializeController extends EventMessageAndControllerInitialize

  case object Reserved extends EventMessageAndControllerInitialize

}

/**
  * Contains the key and body of an Management Controller Device Locator Record
  */
case class McDeviceLocatorRecordKeyAndBody(
  deviceAccessAddress: DeviceAddress,
  channelNumber: ChannelNumber,
  acpiSystemPowerStateNotificationRequired: Boolean,
  acpiDevicePowerStateNotificationRequired: Boolean,
  controllerShouldBePresent: Boolean,
  controllerLogsInitializationErrors: Boolean,
  logsInitializationAgentErrors: Boolean,
  eventMessage: EventMessageAndControllerInitialize,
  deviceCapabilities: DeviceCapabilities,
  entityId: EntityId,
  entityInstance: EntityInstance,
  deviceIdString: DeviceIdString
) extends FruSdrKeyAndBody {
  val sensorIds: Seq[SensorId] = Nil
  val sensorNumbers: Seq[SensorNumber] = Nil
  val recordType: SensorDataRecordType = SensorDataRecordType.McDeviceLocator
  val optSensorType: Option[SensorType] = None

  def optFruDescriptor: Option[FruDescriptor] =
    if (deviceCapabilities.fruInventoryDevice)
      Some(
        FruDescriptor(
          fruType = FruType.Standard,
          deviceId = DeviceId(0),
          deviceAddress = deviceAccessAddress,
          deviceIdString = Some(deviceIdString),
          deviceType = None,
          deviceTypeModifier = None,
          entityId = Some(entityId)
        )
      )
    else
      None
}

object McDeviceLocatorRecordKeyAndBody {

  implicit val decoder: Decoder[McDeviceLocatorRecordKeyAndBody] =
    new Decoder[McDeviceLocatorRecordKeyAndBody] {

      def decode(data: ByteString): McDeviceLocatorRecordKeyAndBody = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val deviceAccessAddress = is.readByte.as[DeviceAddress]

        val channelNumberByte = is.readByte
        val channelNumber = channelNumberByte.bits0To3.as[ChannelNumber]

        val powerStateNotificationGlobalInitialization = is.readByte

        // Power State Notification
        val acpiSystemPowerStateNotificationRequired =
          powerStateNotificationGlobalInitialization.bit7
        val acpiDevicePowerStateNotificationRequired =
          powerStateNotificationGlobalInitialization.bit6
        val controllerShouldBePresent = powerStateNotificationGlobalInitialization.bit5

        // GlobalInitialization
        val controllerLogsInitializationErrors = powerStateNotificationGlobalInitialization.bit3
        val logsInitializationAgentErrors = powerStateNotificationGlobalInitialization.bit2

        val eventMessage =
          powerStateNotificationGlobalInitialization.as[EventMessageAndControllerInitialize]

        val deviceCapabilities = is.readByte.as[DeviceCapabilities]

        // Reserved
        is.skip(3)

        val entityId = is.readByte.as[EntityId]
        val entityInstance = is.readByte.as[EntityInstance]

        // OEM Reserved
        is.skip(1)

        val deviceIdString = iterator.toByteString.as[DeviceIdString]

        McDeviceLocatorRecordKeyAndBody(
          deviceAccessAddress,
          channelNumber,
          acpiSystemPowerStateNotificationRequired = acpiSystemPowerStateNotificationRequired,
          acpiDevicePowerStateNotificationRequired = acpiDevicePowerStateNotificationRequired,
          controllerShouldBePresent = controllerShouldBePresent,
          controllerLogsInitializationErrors = controllerLogsInitializationErrors,
          logsInitializationAgentErrors = logsInitializationAgentErrors,
          eventMessage,
          deviceCapabilities,
          entityId,
          entityInstance,
          deviceIdString
        )
      }
    }
}
