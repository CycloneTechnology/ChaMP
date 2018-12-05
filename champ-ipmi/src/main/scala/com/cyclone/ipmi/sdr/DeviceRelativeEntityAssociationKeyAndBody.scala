package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * Contains the data of an Device Related Entity Association Record
  */
case class DeviceRelativeEntityAssociationKeyAndBody(
                                                      data: ByteString) extends SdrKeyAndBody {
  val sensorIds: Seq[SensorId] = Nil
  val sensorNumbers: Seq[SensorNumber] = Nil
  val recordType: SensorDataRecordType.EntityAssociation.type = SensorDataRecordType.EntityAssociation
  val optSensorType: Option[SensorType] = None
}

object DeviceRelativeEntityAssociationKeyAndBody {
  implicit val decoder: Decoder[DeviceRelativeEntityAssociationKeyAndBody] = new Decoder[DeviceRelativeEntityAssociationKeyAndBody] {
    def decode(data: ByteString): DeviceRelativeEntityAssociationKeyAndBody = {
      DeviceRelativeEntityAssociationKeyAndBody(data)
      // TODO interpret these bytes as per spec ^^^
    }
  }
}