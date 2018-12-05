package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * Contains the data of an Entity Association Record
  */
case class EntityAssociationKeyAndBody(data: ByteString) extends SdrKeyAndBody {
  val sensorIds: Seq[SensorId] = Nil
  val sensorNumbers: Seq[SensorNumber] = Nil
  val recordType: SensorDataRecordType = SensorDataRecordType.EntityAssociation
  val optSensorType: Option[SensorType] = None
}

object EntityAssociationKeyAndBody {
  implicit val decoder: Decoder[EntityAssociationKeyAndBody] =
    new Decoder[EntityAssociationKeyAndBody] {

      def decode(data: ByteString): EntityAssociationKeyAndBody = {
        EntityAssociationKeyAndBody(data)
        // TODO interpret these bytes as per spec ^^^
      }
    }
}
