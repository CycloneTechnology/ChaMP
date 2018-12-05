package com.cyclone.ipmi.command.global

import akka.util.ByteString
import com.cyclone.ipmi.codec.Codec

case class DeviceAddress(id: Byte) extends AnyVal

object DeviceAddress {
  implicit val codec: Codec[DeviceAddress] = new Codec[DeviceAddress] {
    def decode(data: ByteString) = DeviceAddress(data(0))

    def encode(a: DeviceAddress) = ByteString(a.id)
  }

  val BmcAddress = DeviceAddress(0x20.toByte)
  val RemoteConsoleAddress = DeviceAddress(0x81.toByte)
}
