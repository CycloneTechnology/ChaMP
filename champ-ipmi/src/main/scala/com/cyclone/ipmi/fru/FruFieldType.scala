package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec.StringDecoder._
import com.cyclone.ipmi.codec._

/**
  * An FRU field type
  */
sealed trait FruFieldType {

  def decode(data: ByteString, treatAsEnglish: Boolean)(
    implicit languageCode: LanguageCode
  ): FruField
}

object FruFieldType {

  case object Binary extends FruFieldType {

    def decode(data: ByteString, treatAsEnglish: Boolean)(implicit languageCode: LanguageCode) =
      BinaryField(data)
  }

  case object Bcd extends FruFieldType {

    def decode(data: ByteString, treatAsEnglish: Boolean)(implicit languageCode: LanguageCode) =
      StringField(data.as(BcdDecoder))
  }

  case object SixBitAscii extends FruFieldType {

    def decode(data: ByteString, treatAsEnglish: Boolean)(implicit languageCode: LanguageCode) =
      StringField(SixBitAsciiDecoder.decode(data))
  }

  case object StandardString extends FruFieldType {

    def decode(data: ByteString, treatAsEnglish: Boolean)(
      implicit languageCode: LanguageCode
    ): StringField =
      if (languageCode.isEnglish || treatAsEnglish)
        StringField(data.as(StringDecoder.AsciiLatin))
      else
        StringField(data.as(StringDecoder.Unicode))
  }

}
