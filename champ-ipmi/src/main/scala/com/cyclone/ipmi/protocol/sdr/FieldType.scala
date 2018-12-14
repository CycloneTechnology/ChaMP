package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec.StringDecoder._
import com.cyclone.ipmi.codec._

/**
  * An FRU field type
  */
sealed trait FieldType {
  def decode(data: ByteString): Field
}

object FieldType {

  case object Bcd extends FieldType {
    def decode(data: ByteString) = BcdField(data.as(BcdDecoder))
  }

  case object SixBitAscii extends FieldType {
    def decode(data: ByteString) = StringField(data.as(SixBitAsciiDecoder))
  }

  case object AsciiString extends FieldType {
    def decode(data: ByteString) = StringField(data.as(StringDecoder.AsciiLatin))
  }

  case object UnicodeString extends FieldType {
    def decode(data: ByteString) = StringField(data.as(StringDecoder.Unicode))
  }

}
