package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * Represents the 'type/length' prefix of a field FRU data areas.
  */
sealed trait FieldPrefix

object FieldPrefix {
  implicit val decoder: Decoder[FieldPrefix] = new Decoder[FieldPrefix] {

    def decode(data: ByteString): FieldPrefix = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val byte = is.readByte

      (byte.bits6To7.toInt, byte.bits0To4.toInt) match {
        case (_, 0)               => FieldPrefix.EmptyField
        case (0, len)             => FieldPrefix.FixedLengthField(FieldType.UnicodeString, len)
        case (1, len)             => FieldPrefix.FixedLengthField(FieldType.Bcd, len)
        case (2, len)             => FieldPrefix.FixedLengthField(FieldType.SixBitAscii, len)
        case (3, len) if len > 1  => FieldPrefix.FixedLengthField(FieldType.AsciiString, len)
        case (3, len) if len == 1 => FieldPrefix.NoMoreFields
      }
    }
  }

  case class FixedLengthField(fieldType: FieldType, length: Int) extends FieldPrefix {

    def decodeField(data: ByteString): Field =
      fieldType.decode(data.take(length))
  }

  case object EmptyField extends FieldPrefix

  case object NoMoreFields extends FieldPrefix

}
