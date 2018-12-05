package com.cyclone.ipmi.protocol

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._

case class Oem(value: Int, payloadId1: Byte, payloadId2: Byte)

object Oem {
  implicit val codec: Codec[Oem] = new Codec[Oem] {

    def encode(a: Oem): ByteString = {
      import a._
      val b = new ByteStringBuilder

      b ++= value.toBin.take(3)
      b += 0
      b += payloadId1
      b += payloadId2

      b.result()
    }

    def decode(data: ByteString): Oem = {
      val is = data.iterator.asInputStream

      val value = (is.read(3) ++ ByteString(0.toByte)).as[Int]
      is.skip(1)

      val id1 = is.readByte
      val id2 = is.readByte

      Oem(value, id1, id2)
    }
  }
}
