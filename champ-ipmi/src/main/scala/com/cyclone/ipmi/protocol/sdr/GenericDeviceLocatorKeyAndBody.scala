package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber

/**
  * Contains the data of an OEM Record
  */
case class GenericDeviceLocatorKeyAndBody(manufacturerId: IanaEnterpriseNumber, data: ByteString)
    extends SdrKeyAndBody {
  val sensorIds: Seq[SensorId] = Nil
  val sensorNumbers: Seq[SensorNumber] = Nil
  val recordType: SensorDataRecordType = SensorDataRecordType.Oem
  val optSensorType: Option[SensorType] = None
}

object GenericDeviceLocatorKeyAndBody {
  implicit val decoder: Decoder[GenericDeviceLocatorKeyAndBody] =
    new Decoder[GenericDeviceLocatorKeyAndBody] {

      def decode(data: ByteString): GenericDeviceLocatorKeyAndBody = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val manufacturerId = is.read(3).as[IanaEnterpriseNumber]
        val oemData = iterator.toByteString

        GenericDeviceLocatorKeyAndBody(manufacturerId, oemData)
      }
    }
}
