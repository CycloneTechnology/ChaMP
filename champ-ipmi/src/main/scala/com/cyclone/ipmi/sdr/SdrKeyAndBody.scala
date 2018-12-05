package com.cyclone.ipmi.sdr

import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.codec.Base26NumberEncoding
import com.cyclone.ipmi.fru.FruDescriptor
import com.cyclone.ipmi.sdr.SensorDataRecordType._

/**
  * The key and body of an SDR record
  */
trait SdrKeyAndBody {
  def sensorIds: Seq[SensorId]

  def sensorNumbers: Seq[SensorNumber]

  /**
    * Gets sensor ids paired up with corresponding sensor numbers
    */
  def sensorNumbersAndIds: Seq[(SensorNumber, SensorId)] = sensorNumbers zip sensorIds

  def optSensorType: Option[SensorType]

  def recordType: SensorDataRecordType
}

object SdrKeyAndBody {
  def decodeBody(record: SensorDataRecord): IpmiErrorOr[SdrKeyAndBody] = {
    val decoder = record.recordType match {
      case Full                            => FullSdrKeyAndBody.decoder
      case Compact                         => CompactSdrKeyAndBody.decoder
      case EventOnly                       => EventOnlySdrKeyAndBody.decoder
      case FruDeviceLocator                => FruDeviceLocatorRecordKeyAndBody.decoder
      case McDeviceLocator                 => McDeviceLocatorRecordKeyAndBody.decoder
      case Oem                             => OemRecordKeyAndBody.decoder
      case EntityAssociation               => EntityAssociationKeyAndBody.decoder
      case BmcMessageChannelInfo           => BmcMessageChannelInfoSdrKeyAndBody.decoder
      case DeviceRelativeEntityAssociation => DeviceRelativeEntityAssociationKeyAndBody.decoder
      case GenericDeviceLocator            => GenericDeviceLocatorKeyAndBody.decoder
      case McConfirmation                  => McConfirmationRecordKeyAndBody.decoder
    }

    decoder.handleExceptions.decode(record.bodyData)
  }
}

trait AnalogSdrKeyAndBody extends SdrKeyAndBody {
  def sensorUnits: SensorUnits

  def linearization: Linearization

  def readingFactors: ReadingFactors

  def analogDataFormat: AnalogDataFormat

  def sensorDirection: SensorDirection

  def nominalReading: Option[RawSensorValue]

  def normalMaximum: Option[RawSensorValue]

  def normalMinimum: Option[RawSensorValue]

  def positiveGoingThresholdHysteresis: Hysteresis

  def negativeGoingThresholdHysteresis: Hysteresis
}

trait EventSdrKeyAndBody extends SdrKeyAndBody {
  def sensorMasks: SensorMasks
}

/**
  * A [[SdrKeyAndBody]] that can be shared by multiple sensors.
  */
trait SharingSdrKeyAndBody extends SdrKeyAndBody {
  def baseSensorId: SensorId

  def baseSensorNumber: SensorNumber

  def sensorRecordSharing: SensorRecordSharing

  lazy val sensorIds: Seq[SensorId] =
    (0 until sensorRecordSharing.shareCount)
      .map { i =>

        val offset = i + sensorRecordSharing.sensorIdOffset
        val offsetString = sensorRecordSharing.sensorIdModifierType match {
          case SharingModifierType.Numeric => s"$offset"
          case SharingModifierType.Alpha   => Base26NumberEncoding.encode(offset)
        }

        SensorId(baseSensorId.id + offsetString)
      }

  lazy val sensorNumbers: Seq[SensorNumber] =
    (0 until sensorRecordSharing.shareCount)
      .map(i => SensorNumber(i + baseSensorNumber.value))
}

/**
  * An SDR record that may reference FRU data
  */
trait FruSdrKeyAndBody extends SdrKeyAndBody {
  /**
    * Gets the [[FruDescriptor]] to use to get the FRU record.
    */
  def optFruDescriptor: Option[FruDescriptor]
}