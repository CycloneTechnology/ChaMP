package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait OwnerIdType

object OwnerIdType {
  implicit val decoder: Decoder[OwnerIdType] = new Decoder[OwnerIdType] {

    def decode(data: ByteString): OwnerIdType =
      if (data(0).bit0) SystemSoftwareId else SlaveAddress
  }

  case object SlaveAddress extends OwnerIdType

  case object SystemSoftwareId extends OwnerIdType

}

case class SensorOwnerId(id: Byte, idType: OwnerIdType)

object SensorOwnerId {
  implicit val decoder: Decoder[SensorOwnerId] = new Decoder[SensorOwnerId] {

    def decode(data: ByteString) =
      SensorOwnerId(data(0).bits1To7, data(0).as[OwnerIdType])
  }
}
