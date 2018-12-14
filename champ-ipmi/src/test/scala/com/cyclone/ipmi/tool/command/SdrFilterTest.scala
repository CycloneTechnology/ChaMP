package com.cyclone.ipmi.tool.command

import com.cyclone.ipmi.protocol.sdr._
import com.cyclone.ipmi.tool.command.SdrFilter.{BySensorIds, BySensorNumbers}
import org.scalatest.{Matchers, WordSpec}

/**
  * Tests for [[SdrFilter]]
  */
class SdrFilterTest extends WordSpec with Matchers {

  val fullRecord = FullSdrKeyAndBody(
    ownerId = SensorOwnerId(0, OwnerIdType.SlaveAddress),
    ownerLun = SensorOwnerLun(0),
    sensorNumber = SensorNumber(0),
    sensorId = SensorId(""),
    entityId = EntityId.Unspecified,
    entityInstance = EntityInstance(0),
    sensorInitialization = SensorInitialization(0),
    sensorCapabilities = SensorCapabilities(0),
    sensorType = SensorType.Voltage,
    sensorMasks =
      SensorMasks(ReadingMask.DiscreteStates(EventBits.empty, EventReadingType.Performance), EventMask.empty),
    sensorUnits = SensorUnits.Simple(SensorUnit.Volts),
    linearization = Linearization.Linear,
    analogDataFormat = AnalogDataFormat.Unsigned,
    readingFactors = ReadingFactors(1, 0, 0, 0, RawTolerance(0), RawAccuracy(0)),
    sensorDirection = SensorDirection.Output,
    nominalReading = None,
    normalMaximum = None,
    normalMinimum = None,
    sensorMaxumumReading = RawSensorValue(0),
    sensorMinimumReading = RawSensorValue(0),
    positiveGoingThresholdHysteresis = Hysteresis.NoHysteresis,
    negativeGoingThresholdHysteresis = Hysteresis.NoHysteresis
  )

  val compactRecord = CompactSdrKeyAndBody(
    ownerId = SensorOwnerId(0, OwnerIdType.SlaveAddress),
    ownerLun = SensorOwnerLun(0),
    baseSensorNumber = SensorNumber(0),
    baseSensorId = SensorId(""),
    entityId = EntityId.Unspecified,
    entityInstance = EntityInstance(0),
    sensorInitialization = SensorInitialization(0),
    sensorCapabilities = SensorCapabilities(0),
    sensorType = SensorType.Voltage,
    sensorMasks =
      SensorMasks(ReadingMask.DiscreteStates(EventBits.empty, EventReadingType.Performance), EventMask.empty),
    sensorUnits = SensorUnits.Simple(SensorUnit.Volts),
    sensorDirection = SensorDirection.Output,
    sensorRecordSharing = SensorRecordSharing.NoSharing,
    positiveGoingThresholdHysteresis = Hysteresis.NoHysteresis,
    negativeGoingThresholdHysteresis = Hysteresis.NoHysteresis
  )

  "an BySensorId filter" when {
    "used for a full record" must {
      val record = fullRecord.copy(sensorId = SensorId("abcde"))

      "include only if the sensorId is included" in {
        BySensorIds(SensorId("xyz"), SensorId("abcde")).predicate(record) shouldBe true
        BySensorIds(SensorId("xyz"), SensorId("xabcde")).predicate(record) shouldBe false
      }
    }

    "used for a compact/event record with alphabetic modification" must {
      val record = compactRecord.copy(
        baseSensorId = SensorId("abcde "),
        sensorRecordSharing = SensorRecordSharing(shareCount = 2, SharingModifierType.Alpha, sensorIdOffset = 26)
      )

      "include only if the share count includes" in {
        BySensorIds(SensorId("abcde AA")).predicate(record) shouldBe true
        BySensorIds(SensorId("abcde AB")).predicate(record) shouldBe true
        BySensorIds(SensorId("abcde AC")).predicate(record) shouldBe false
      }
    }

    "used for a compact/event record with numeric modification" must {
      val record = compactRecord.copy(
        baseSensorId = SensorId("abcde "),
        sensorRecordSharing = SensorRecordSharing(shareCount = 2, SharingModifierType.Numeric, sensorIdOffset = 5)
      )

      "include only if the share count includes" in {
        BySensorIds(SensorId("abcde 5")).predicate(record) shouldBe true
        BySensorIds(SensorId("abcde 6")).predicate(record) shouldBe true
        BySensorIds(SensorId("abcde 7")).predicate(record) shouldBe false
      }
    }
  }

  "an BySensorNumber filter" when {
    "used for a full record" must {
      val record = fullRecord.copy(sensorNumber = SensorNumber(1))

      "include only if the sensor number is included" in {
        BySensorNumbers(SensorNumber(0), SensorNumber(1)).predicate(record) shouldBe true
        BySensorNumbers(SensorNumber(0), SensorNumber(2)).predicate(record) shouldBe false
      }
    }

    "used for a compact/event record" must {
      val record = compactRecord.copy(
        baseSensorNumber = SensorNumber(10),
        sensorRecordSharing = SensorRecordSharing(shareCount = 2)
      )

      "include only if the share count includes" in {
        BySensorNumbers(SensorNumber(10)).predicate(record) shouldBe true
        BySensorNumbers(SensorNumber(11)).predicate(record) shouldBe true
        BySensorNumbers(SensorNumber(12)).predicate(record) shouldBe false
      }
    }
  }
}
