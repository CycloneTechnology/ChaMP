package com.cyclone.ipmi.command.global

import akka.util.ByteString
import com.cyclone.ipmi.codec.Codec

case class DeviceId(id: Byte) extends AnyVal

object DeviceId {
  implicit val codec: Codec[DeviceId] = new Codec[DeviceId] {
    def decode(data: ByteString) = DeviceId(data(0))

    def encode(a: DeviceId) = ByteString(a.id)
  }
}
