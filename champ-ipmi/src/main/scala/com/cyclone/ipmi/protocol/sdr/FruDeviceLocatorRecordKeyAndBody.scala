package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.fruInventory.SlaveAddress
import com.cyclone.ipmi.command.global.{DeviceAddress, DeviceId}
import com.cyclone.ipmi.protocol.fru.{FruDescriptor, FruType}
import com.cyclone.ipmi.protocol.sdr.DeviceType.FruInventoryDeviceBehindMc
import com.cyclone.ipmi.protocol.sdr.DeviceTypeModifier.{DimmMemoryId, IpmiFruInventory}

sealed trait FruAddress

case class DeviceIdFruAddress(deviceId: DeviceId) extends FruAddress

case class SlaveFruAddress(slaveAddress: SlaveAddress) extends FruAddress

/**
  * Contains the key and body of an FRU Device Locator Record
  */
case class FruDeviceLocatorRecordKeyAndBody(
  deviceAccessAddress: DeviceAddress,
  address: FruAddress,
  channelNumber: ChannelNumber,
  entityId: EntityId,
  entityInstance: EntityInstance,
  deviceIdString: DeviceIdString,
  deviceType: DeviceType,
  deviceTypeModifier: Option[DeviceTypeModifier]
) extends FruSdrKeyAndBody {
  val sensorIds: Seq[SensorId] = Nil
  val sensorNumbers: Seq[SensorNumber] = Nil
  val recordType: SensorDataRecordType = SensorDataRecordType.FruDeviceLocator
  val optSensorType: Option[SensorType] = None

  def optFruDescriptor: Option[FruDescriptor] = {
    val fruTypeAndDeviceId = (deviceType, deviceTypeModifier, address) match {
      case (FruInventoryDeviceBehindMc, Some(IpmiFruInventory), DeviceIdFruAddress(devId)) =>
        Some((FruType.Standard, devId))
      case (_, Some(DimmMemoryId), DeviceIdFruAddress(devId)) => Some((FruType.Dimm, devId))
      case _                                                  => None
    }

    fruTypeAndDeviceId.map {
      case (tpe, devId) =>
        FruDescriptor(
          fruType = tpe,
          deviceId = devId,
          deviceAddress = deviceAccessAddress,
          deviceIdString = Some(deviceIdString),
          deviceType = Some(deviceType),
          deviceTypeModifier = deviceTypeModifier,
          entityId = Some(entityId)
        )
    }
  }

}

object FruDeviceLocatorRecordKeyAndBody {
  implicit val decoder: Decoder[FruDeviceLocatorRecordKeyAndBody] =
    new Decoder[FruDeviceLocatorRecordKeyAndBody] {

      def decode(data: ByteString): FruDeviceLocatorRecordKeyAndBody = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val deviceAccessAddress = is.readByte.as[DeviceAddress]
        val addressByte = is.readByte
        val byte8 = is.readByte

        val logicalFru = byte8.bit7
        //      val lun = byte8.bits3To4
        //      val busId = byte8.bits0To2

        val address =
          if (logicalFru)
            DeviceIdFruAddress(addressByte.as[DeviceId])
          else
            SlaveFruAddress(addressByte.as[SlaveAddress])

        val channelNumberByte = is.readByte
        val channelNumber = channelNumberByte.bits4To7.as[ChannelNumber]

        // reserved
        is.skip(1)

        val deviceType = is.readByte.as[DeviceType]
        val deviceTypeModifier = deviceType.typeModifierFor(is.readByte)

        val entityId = is.readByte.as[EntityId]
        val entityInstance = is.readByte.as[EntityInstance]

        // OEM
        is.skip(1)

        val deviceIdString = iterator.toByteString.as[DeviceIdString]

        FruDeviceLocatorRecordKeyAndBody(
          address = address,
          channelNumber = channelNumber,
          deviceAccessAddress = deviceAccessAddress,
          entityId = entityId,
          entityInstance = entityInstance,
          deviceIdString = deviceIdString,
          deviceType = deviceType,
          deviceTypeModifier = deviceTypeModifier
        )
      }
    }
}
