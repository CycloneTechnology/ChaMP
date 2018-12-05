package com.cyclone.ipmi.codec

import akka.util.{ByteString, ByteStringBuilder}
import com.google.common.base.Charsets
import org.joda.time.DateTime

/**
  * Additional useful (but non-implicit) codecs
  */
object MoreCodecs {

  def nullTerminatedStringCoder(length: Int): Codec[String] = new Codec[String] {

    def encode(a: String): ByteString = {
      val as = a.getBytes(Charsets.US_ASCII)
      val pad = (length - as.length) max 0

      (ByteString(as) ++ ByteString(Array.fill(pad)(0.toByte))).take(length)
    }

    def decode(data: ByteString): String =
      data.take(length).takeWhile(_ != 0).decodeString(Charsets.US_ASCII.name())
  }

  val defaultTerminatedStringCodec: Codec[String] = nullTerminatedStringCoder(16)

  /**
    * Codec for short (<256 chars strings). Uses a single byte to encode the length.
    */
  val shortStringLengthPrefixedStringCodec: Codec[String] = new Codec[String] {

    def encode(a: String): ByteString = {
      val b = new ByteStringBuilder

      b += a.length.toByte
      b ++= a.getBytes(Charsets.US_ASCII)

      b.result
    }

    def decode(data: ByteString): String = {
      val is = data.iterator.asInputStream
      val length = is.readByte.toUnsignedInt
      val bytes = is.read(length)

      bytes.decodeString(Charsets.US_ASCII.name())
    }
  }

  private val base1996Time = new DateTime(1996, 1, 1, 0, 0)

  val datetimeCodecFrom1996InMinutes: Decoder[DateTime] = new Decoder[DateTime] {

    def decode(data: ByteString): DateTime = {
      // Codec required 3 bytes - pad with 0
      val mins = (data :+ 0.toByte).as[Int]
      base1996Time.plusMinutes(mins)
    }
  }

}
