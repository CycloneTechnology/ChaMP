package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

import scalaz.Scalaz._

/**
  * Contains the key and body of a full sensor record
  */
case class FullSdrKeyAndBody(
  sensorId: SensorId,
  ownerId: SensorOwnerId,
  ownerLun: SensorOwnerLun,
  sensorNumber: SensorNumber,
  entityId: EntityId,
  entityInstance: EntityInstance,
  sensorInitialization: SensorInitialization,
  sensorCapabilities: SensorCapabilities,
  sensorType: SensorType,
  sensorMasks: SensorMasks,
  sensorUnits: SensorUnits,
  linearization: Linearization,
  analogDataFormat: AnalogDataFormat,
  readingFactors: ReadingFactors,
  sensorDirection: SensorDirection,
  nominalReading: Option[RawSensorValue],
  normalMaximum: Option[RawSensorValue],
  normalMinimum: Option[RawSensorValue],
  sensorMaxumumReading: RawSensorValue,
  sensorMinimumReading: RawSensorValue,
  positiveGoingThresholdHysteresis: Hysteresis,
  negativeGoingThresholdHysteresis: Hysteresis
) extends SdrKeyAndBody
  with AnalogSdrKeyAndBody
  with EventSdrKeyAndBody {
  val sensorIds: Seq[SensorId] = Seq(sensorId)
  val sensorNumbers: Seq[SensorNumber] = Seq(sensorNumber)
  val recordType: SensorDataRecordType = SensorDataRecordType.Full
  val optSensorType = Some(sensorType)
}

object FullSdrKeyAndBody {
  implicit val decoder: Decoder[FullSdrKeyAndBody] = new Decoder[FullSdrKeyAndBody] {
    def decode(data: ByteString): FullSdrKeyAndBody = {
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

      val sensorMasks = is.read(6).as[SensorMasks](SensorMasks.decoder(eventReadingType, sensorType))

      val sensorUnitsBytes = is.read(3)

      val analogDataFormat = sensorUnitsBytes.as[AnalogDataFormat]

      val sensorUnits = sensorUnitsBytes.as[SensorUnits]

      val linearization = is.readByte.as[Linearization]

      val readingFactorBytes = is.read(6)
      val readingFactor = readingFactorBytes.as[ReadingFactors]

      val sensorDirection = readingFactorBytes(4).as[SensorDirection]

      val analogCharacteristics = is.readByte.as[AnalogCharacteristics]
      val nominalReading = is.readByte.map(b => analogCharacteristics.nominalReadingSpecified.option(b.as[RawSensorValue]))
      val normalMaximum = is.readByte.map(b => analogCharacteristics.normalMaxSpecified.option(b.as[RawSensorValue]))
      val normalMinimum = is.readByte.map(b => analogCharacteristics.normalMinSpecified.option(b.as[RawSensorValue]))

      val sensorMaximum = is.readByte.as[RawSensorValue]
      val sensorMinimum = is.readByte.as[RawSensorValue]

      // Ignore sensor thresholds - only set if set threshold bit set
      // and can more easily get from Get Sensor Threshold command
      is.skip(6)

      val positiveGoingHyst = is.readByte.as[Hysteresis]
      val negativeGoingHyst = is.readByte.as[Hysteresis]

      // Reserved
      is.skip(2)

      // OEM
      is.skip(1)

      val sensorId = iterator.toByteString.as[SensorId]

      FullSdrKeyAndBody(
        ownerId = ownerId,
        ownerLun = ownerLun,
        sensorNumber = sensorNumber,
        entityId = entityId,
        entityInstance = entityInstance,
        sensorInitialization = sensorInit,
        sensorCapabilities = sensorCaps,
        sensorType = sensorType,
        sensorMasks = sensorMasks,
        sensorUnits = sensorUnits,
        linearization = linearization,
        analogDataFormat = analogDataFormat,
        readingFactors = readingFactor,
        sensorDirection = sensorDirection,
        nominalReading = nominalReading,
        normalMaximum = normalMaximum,
        normalMinimum = normalMinimum,
        sensorMaxumumReading = sensorMaximum,
        sensorMinimumReading = sensorMinimum,
        positiveGoingThresholdHysteresis = positiveGoingHyst,
        negativeGoingThresholdHysteresis = negativeGoingHyst,
        sensorId = sensorId
      )
    }
  }
}
