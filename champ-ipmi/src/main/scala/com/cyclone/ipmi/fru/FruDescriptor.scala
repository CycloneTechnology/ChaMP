package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.codec.Decoder
import com.cyclone.ipmi.command.global.{DeviceAddress, DeviceId}
import com.cyclone.ipmi.sdr.{DeviceIdString, DeviceType, DeviceTypeModifier, EntityId}
import com.typesafe.scalalogging.LazyLogging

/**
  * Stores information from the SDR including information used to locate the FRU.
  */
case class FruDescriptor(
  fruType: FruType,
  deviceId: DeviceId,
  deviceAddress: DeviceAddress,
  deviceIdString: Option[DeviceIdString],
  deviceType: Option[DeviceType],
  deviceTypeModifier: Option[DeviceTypeModifier],
  entityId: Option[EntityId]
) {
  def decode(data: ByteString): IpmiErrorOr[Fru] = fruType.decoder.handleExceptions.decode(data)
}

sealed trait FruType {
  def decoder: Decoder[Fru]
}

object FruType {

  case object Standard extends FruType {
    def decoder: Decoder[StandardFru] with LazyLogging = StandardFru.decoder
  }

  case object Dimm extends FruType {
    def decoder: Decoder[MemoryModule] = MemoryModule.decoder
  }

}
