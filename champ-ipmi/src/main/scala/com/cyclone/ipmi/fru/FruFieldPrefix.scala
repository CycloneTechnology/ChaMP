package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * Represents the 'type/length' prefix of a field FRU data areas.
  */
sealed trait FruFieldPrefix

object FruFieldPrefix {
  implicit val decoder: Decoder[FruFieldPrefix] = new Decoder[FruFieldPrefix] {

    def decode(data: ByteString): FruFieldPrefix = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val byte = is.readByte

      (byte.bits6To7.toInt, byte.bits0To5.toInt) match {
        case (_, 0)   => FruFieldPrefix.EmptyField
        case (0, len) => FruFieldPrefix.FixedLengthField(FruFieldType.Binary, len)
        case (1, len) => FruFieldPrefix.FixedLengthField(FruFieldType.Bcd, len)
        case (2, len) => FruFieldPrefix.FixedLengthField(FruFieldType.SixBitAscii, len)
        case (3, len) if len > 1 =>
          FruFieldPrefix.FixedLengthField(FruFieldType.StandardString, len)
        case (3, len) if len == 1 => FruFieldPrefix.NoMoreFields
      }
    }
  }

  case class FixedLengthField(fieldType: FruFieldType, length: Int) extends FruFieldPrefix {

    def decodeField(data: ByteString, treatAsEnglish: Boolean = false)(
      implicit languageCode: LanguageCode
    ): FruField =
      fieldType.decode(data.take(length), treatAsEnglish)
  }

  case object EmptyField extends FruFieldPrefix

  case object NoMoreFields extends FruFieldPrefix

}
