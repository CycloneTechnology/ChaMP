package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

case class DeviceCapabilities(
  chassisDevice: Boolean,
  bridge: Boolean,
  ipmbEventGenerator: Boolean,
  ipmbEventReceiver: Boolean,
  fruInventoryDevice: Boolean,
  selDevice: Boolean,
  sdrRepositoryDevice: Boolean,
  sensorDevice: Boolean
)

object DeviceCapabilities {
  implicit val decoder: Decoder[DeviceCapabilities] = new Decoder[DeviceCapabilities] {

    def decode(data: ByteString): DeviceCapabilities = {
      val byte = data(0)

      DeviceCapabilities(
        chassisDevice = byte.bit7,
        bridge = byte.bit6,
        ipmbEventGenerator = byte.bit5,
        ipmbEventReceiver = byte.bit4,
        fruInventoryDevice = byte.bit3,
        selDevice = byte.bit2,
        sdrRepositoryDevice = byte.bit1,
        sensorDevice = byte.bit0
      )
    }
  }

  implicit val encoder: Coder[DeviceCapabilities] = new Coder[DeviceCapabilities] {

    def encode(a: DeviceCapabilities): ByteString = {
      ByteString(
        (a.chassisDevice.toBit7.toUnsignedInt &
        a.bridge.toBit6.toUnsignedInt &
        a.ipmbEventGenerator.toBit5.toUnsignedInt &
        a.ipmbEventReceiver.toBit4.toUnsignedInt &
        a.fruInventoryDevice.toBit3.toUnsignedInt &
        a.selDevice.toBit2.toUnsignedInt &
        a.sdrRepositoryDevice.toBit1.toUnsignedInt &
        a.sensorDevice.toBit0.toUnsignedInt).toByte
      )
    }
  }
}
