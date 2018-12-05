package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * Contains the key and body of a compact sensor record
  */
case class CompactSdrKeyAndBody(
  baseSensorId: SensorId,
  ownerId: SensorOwnerId,
  ownerLun: SensorOwnerLun,
  baseSensorNumber: SensorNumber,
  entityId: EntityId,
  entityInstance: EntityInstance,
  sensorInitialization: SensorInitialization,
  sensorCapabilities: SensorCapabilities,
  sensorType: SensorType,
  sensorMasks: SensorMasks,
  sensorUnits: SensorUnits,
  sensorDirection: SensorDirection,
  sensorRecordSharing: SensorRecordSharing,
  positiveGoingThresholdHysteresis: Hysteresis,
  negativeGoingThresholdHysteresis: Hysteresis
) extends SharingSdrKeyAndBody
    with EventSdrKeyAndBody {
  val recordType: SensorDataRecordType = SensorDataRecordType.Compact
  val optSensorType = Some(sensorType)
}

object CompactSdrKeyAndBody {
  implicit val decoder: Decoder[CompactSdrKeyAndBody] = new Decoder[CompactSdrKeyAndBody] {

    def decode(data: ByteString): CompactSdrKeyAndBody = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val ownerId = is.readByte.as[SensorOwnerId]
      val ownerLun = is.readByte.as[SensorOwnerLun]
      val sensorNumber = is.readByte.as[SensorNumber]

      val entityId = is.readByte.as[EntityId]
      val entityInstance = is.readByte.as[EntityInstance]

      val sensorInit = is.readByte.as[SensorInitialization]
      val sensorCaps = is.readByte.as[SensorCapabilities]
      val sensorType = is.readByte.as[SensorType]

      val eventReadingType = is.readByte.as[EventReadingType]

      val sensorMasks =
        is.read(6).as[SensorMasks](SensorMasks.decoder(eventReadingType, sensorType))

      // Strange - why does compact have units (although no linearization)?
      val sensorUnits = is.read(3).as[SensorUnits]

      val sensorRecordSharingBytes = is.read(2)

      val sensorDirection = sensorRecordSharingBytes(0).bits6To7.as[SensorDirection]
      val sensorRecordSharing = sensorRecordSharingBytes.as[SensorRecordSharing]

      val positiveGoingHyst = is.readByte.as[Hysteresis]
      val negativeGoingHyst = is.readByte.as[Hysteresis]

      // Reserved
      is.skip(3)

      // OEM
      is.skip(1)

      val sensorId = iterator.toByteString.as[SensorId]

      CompactSdrKeyAndBody(
        ownerId = ownerId,
        ownerLun = ownerLun,
        baseSensorNumber = sensorNumber,
        entityId = entityId,
        entityInstance = entityInstance,
        baseSensorId = sensorId,
        sensorInitialization = sensorInit,
        sensorCapabilities = sensorCaps,
        sensorType = sensorType,
        sensorMasks = sensorMasks,
        sensorUnits = sensorUnits,
        sensorDirection = sensorDirection,
        sensorRecordSharing = sensorRecordSharing,
        positiveGoingThresholdHysteresis = positiveGoingHyst,
        negativeGoingThresholdHysteresis = negativeGoingHyst
      )
    }
  }
}
