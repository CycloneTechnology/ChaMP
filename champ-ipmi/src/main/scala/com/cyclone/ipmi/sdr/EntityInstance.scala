package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec.Decoder

case class EntityInstance(value: Byte)

object EntityInstance {
  implicit val decoder: Decoder[EntityInstance] = new Decoder[EntityInstance] {
    def decode(data: ByteString) = EntityInstance(data(0))
  }
}
