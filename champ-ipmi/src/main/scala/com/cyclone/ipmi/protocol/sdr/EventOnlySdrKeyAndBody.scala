package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * Contains the key and body of an event-only record
  */
case class EventOnlySdrKeyAndBody(
  baseSensorId: SensorId,
  ownerId: SensorOwnerId,
  ownerLun: SensorOwnerLun,
  baseSensorNumber: SensorNumber,
  entityId: EntityId,
  entityInstance: EntityInstance,
  sensorType: SensorType,
  eventReadingType: EventReadingType,
  sensorDirection: SensorDirection,
  sensorRecordSharing: SensorRecordSharing
) extends SharingSdrKeyAndBody {
  val optSensorId = Some(baseSensorId)
  val recordType: SensorDataRecordType = SensorDataRecordType.EventOnly
  val optSensorType = Some(sensorType)
}

// TODO ?not used (when reading) - only for 'events'
object EventOnlySdrKeyAndBody {
  implicit val decoder: Decoder[EventOnlySdrKeyAndBody] = new Decoder[EventOnlySdrKeyAndBody] {

    def decode(data: ByteString): EventOnlySdrKeyAndBody = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val ownerId = is.readByte.as[SensorOwnerId]
      val ownerLun = is.readByte.as[SensorOwnerLun]
      val sensorNumber = is.readByte.as[SensorNumber]

      val entityId = is.readByte.as[EntityId]
      val entityInstance = is.readByte.as[EntityInstance]

      val sensorType = is.readByte.as[SensorType]
      val eventReadingType = is.readByte.as[EventReadingType]

      val sensorRecordSharingBytes = is.read(2)

      val sensorDirection = sensorRecordSharingBytes(0).bits6To7.as[SensorDirection]
      val sensorRecordSharing = sensorRecordSharingBytes.as[SensorRecordSharing]

      // Reserved
      is.skip(1)

      // OEM
      is.skip(1)

      val sensorId = iterator.toByteString.as[SensorId]

      EventOnlySdrKeyAndBody(
        ownerId = ownerId,
        ownerLun = ownerLun,
        baseSensorNumber = sensorNumber,
        entityId = entityId,
        entityInstance = entityInstance,
        baseSensorId = sensorId,
        sensorType = sensorType,
        eventReadingType = eventReadingType,
        sensorDirection = sensorDirection,
        sensorRecordSharing = sensorRecordSharing
      )
    }
  }
}
