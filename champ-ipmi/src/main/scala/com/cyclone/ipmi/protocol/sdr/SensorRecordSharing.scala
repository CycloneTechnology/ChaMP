package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait SharingModifierType

object SharingModifierType {
  implicit val decoder: Decoder[SharingModifierType] = new Decoder[SharingModifierType] {

    def decode(data: ByteString): SharingModifierType = data(0).bits4To5.toUnsignedInt match {
      case 0 => Numeric
      case 1 => Alpha
    }
  }

  case object Numeric extends SharingModifierType

  case object Alpha extends SharingModifierType

}

case class SensorRecordSharing(
  shareCount: Int,
  sensorIdModifierType: SharingModifierType = SharingModifierType.Numeric,
  sensorIdOffset: Int = 0,
  instanceShared: Boolean = false
)

object SensorRecordSharing {
  implicit val decoder: Decoder[SensorRecordSharing] = new Decoder[SensorRecordSharing] {

    def decode(data: ByteString) =
      SensorRecordSharing(
        shareCount = data(0).bits0To3.toUnsignedInt,
        sensorIdModifierType = data(0).as[SharingModifierType],
        sensorIdOffset = data(1).bits0To6.toUnsignedInt,
        instanceShared = !data(1).bit7
      )
  }

  val NoSharing = SensorRecordSharing(shareCount = 1, SharingModifierType.Alpha)
}
