package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.global._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber

/**
  * Contains the key and body of an Management Controller Confirmation Record
  */
case class McConfirmationRecordKeyAndBody(
  deviceAccessAddress: DeviceAddress,
  deviceId: DeviceId,
  channelNumber: ChannelNumber,
  deviceRevision: DeviceRevision,
  firmwareRevision: FirmwareRevision,
  ipmiVersion: IpmiVersion,
  manufacturer: IanaEnterpriseNumber,
  productId: Short,
  guid: DeviceGuid
) extends SdrKeyAndBody {
  val sensorIds: Seq[SensorId] = Nil
  val sensorNumbers: Seq[SensorNumber] = Nil
  val recordType: SensorDataRecordType = SensorDataRecordType.McConfirmation
  val optSensorType: Option[SensorType] = None
}

object McConfirmationRecordKeyAndBody {
  implicit val decoder: Decoder[McConfirmationRecordKeyAndBody] =
    new Decoder[McConfirmationRecordKeyAndBody] {

      def decode(data: ByteString): McConfirmationRecordKeyAndBody = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val deviceAccessAddress = is.readByte.as[DeviceAddress]

        val deviceId = is.readByte.as[DeviceId]

        val channelNumberByte = is.readByte

        val channelNumber = channelNumberByte.bits4To7.as[ChannelNumber]
        val deviceRevision = channelNumberByte.as[DeviceRevision]

        val firmwareRevision = is.read(2).as[FirmwareRevision]
        val ipmiVersion = is.readByte.as[IpmiVersion]

        val manufacturer = is.read(3).as[IanaEnterpriseNumber]

        val productId = is.read(2).as[Short]

        val guid = is.read(16).as[DeviceGuid]

        McConfirmationRecordKeyAndBody(
          deviceAccessAddress,
          deviceId,
          channelNumber,
          deviceRevision,
          firmwareRevision,
          ipmiVersion,
          manufacturer,
          productId,
          guid
        )
      }
    }
}
