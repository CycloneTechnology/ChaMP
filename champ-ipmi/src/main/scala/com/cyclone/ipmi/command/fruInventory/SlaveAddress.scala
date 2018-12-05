package com.cyclone.ipmi.command.fruInventory

import akka.util.ByteString
import com.cyclone.ipmi.codec._

case class SlaveAddress(id: Byte) extends AnyVal

object SlaveAddress {
  implicit val codec: Codec[SlaveAddress] = new Codec[SlaveAddress] {
    def decode(data: ByteString) = SlaveAddress(data(0).bits1To7)

    def encode(a: SlaveAddress) = ByteString(a.id << 1)
  }
}
