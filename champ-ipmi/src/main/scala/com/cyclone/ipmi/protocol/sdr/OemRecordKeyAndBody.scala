package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber

/**
  * Contains the data of an OEM Record
  */
case class OemRecordKeyAndBody(manufacturerId: IanaEnterpriseNumber, data: ByteString) extends SdrKeyAndBody {
  val sensorIds: Seq[SensorId] = Nil
  val sensorNumbers: Seq[SensorNumber] = Nil

  val recordType: SensorDataRecordType = SensorDataRecordType.Oem
  val optSensorType: Option[SensorType] = None
}

object OemRecordKeyAndBody {
  implicit val decoder: Decoder[OemRecordKeyAndBody] = new Decoder[OemRecordKeyAndBody] {

    def decode(data: ByteString): OemRecordKeyAndBody = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val manufacturerId = is.read(3).as[IanaEnterpriseNumber]
      val oemData = iterator.toByteString

      OemRecordKeyAndBody(manufacturerId, oemData)
    }
  }
}
