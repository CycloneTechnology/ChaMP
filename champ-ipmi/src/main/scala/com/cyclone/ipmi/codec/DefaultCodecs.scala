package com.cyclone.ipmi.codec

import java.nio.ByteOrder

import org.joda.time.{DateTime, Instant, Minutes}
import akka.util.ByteString
import akka.util.ByteStringBuilder

/**
  * Codecs for common types.
  */
trait DefaultCodecs {
  implicit val shortCodec: Codec[Short] = new Codec[Short] {
    def encode(a: Short): ByteString = {
      val b = new ByteStringBuilder

      b.putShort(a)(ByteOrder.LITTLE_ENDIAN)

      b.result
    }

    def decode(data: ByteString): Short =
      data.iterator.getShort(ByteOrder.LITTLE_ENDIAN)
  }

  implicit val intCodec: Codec[Int] = new Codec[Int] {
    def encode(a: Int): ByteString = {
      val b = new ByteStringBuilder

      b.putInt(a)(ByteOrder.LITTLE_ENDIAN)

      b.result
    }

    def decode(data: ByteString): Int =
      data.iterator.getInt(ByteOrder.LITTLE_ENDIAN)
  }

  implicit val longCodec: Codec[Long] = new Codec[Long] {
    def encode(a: Long): ByteString = {
      val b = new ByteStringBuilder

      b.putLong(a)(ByteOrder.LITTLE_ENDIAN)

      b.result
    }

    def decode(data: ByteString): Long =
      data.iterator.getLong(ByteOrder.LITTLE_ENDIAN)
  }

  implicit val instantCodec: Codec[Instant] = new Codec[Instant] {
    def encode(a: Instant): ByteString = {

      val b = new ByteStringBuilder

      val seconds = a.getMillis / 1000
      b.putShort(seconds.toInt)(ByteOrder.LITTLE_ENDIAN)

      b.result
    }

    def decode(data: ByteString): Instant = {
      val seconds = data.iterator.getInt(ByteOrder.LITTLE_ENDIAN).toLong
      new Instant(seconds * 1000)
    }
  }
}

