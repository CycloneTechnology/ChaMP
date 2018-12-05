package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec.Decoder

/**
  * The type of an SDR record
  */
sealed trait SensorDataRecordType {
  def code: Byte

  val canProvideFruDescriptor: Boolean = false
}

object SensorDataRecordType {

  implicit val decoder: Decoder[SensorDataRecordType] = new Decoder[SensorDataRecordType] {
    def decode(data: ByteString): SensorDataRecordType = fromCode(data(0))
  }

  case object Full extends SensorDataRecordType {
    val code: Byte = 0x01.toByte
  }

  case object Compact extends SensorDataRecordType {
    val code: Byte = 0x02.toByte
  }


  case object EventOnly extends SensorDataRecordType {
    val code: Byte = 0x03.toByte
  }

  case object EntityAssociation extends SensorDataRecordType {
    val code: Byte = 0x08.toByte
  }

  case object DeviceRelativeEntityAssociation extends SensorDataRecordType {
    val code: Byte = 0x09.toByte
  }

  case object GenericDeviceLocator extends SensorDataRecordType {
    val code: Byte = 0x10.toByte
  }

  case object FruDeviceLocator extends SensorDataRecordType {
    val code: Byte = 0x11.toByte
    override val canProvideFruDescriptor: Boolean = true
  }

  case object McDeviceLocator extends SensorDataRecordType {
    val code: Byte = 0x12.toByte
    override val canProvideFruDescriptor: Boolean = true
  }

  case object McConfirmation extends SensorDataRecordType {
    val code: Byte = 0x13.toByte
  }

  case object BmcMessageChannelInfo extends SensorDataRecordType {
    val code: Byte = 0x14.toByte
  }

  case object Oem extends SensorDataRecordType {
    val code: Byte = 0xc0.toByte
  }

  def fromCode(code: Byte): SensorDataRecordType = code match {
    case Full.code                            => Full
    case Compact.code                         => Compact
    case EventOnly.code                       => EventOnly
    case EntityAssociation.code               => EntityAssociation
    case DeviceRelativeEntityAssociation.code => DeviceRelativeEntityAssociation
    case GenericDeviceLocator.code            => GenericDeviceLocator
    case FruDeviceLocator.code                => FruDeviceLocator
    case McDeviceLocator.code                 => McDeviceLocator
    case McConfirmation.code                  => McConfirmation
    case BmcMessageChannelInfo.code           => BmcMessageChannelInfo
    case Oem.code                             => Oem
  }
}
