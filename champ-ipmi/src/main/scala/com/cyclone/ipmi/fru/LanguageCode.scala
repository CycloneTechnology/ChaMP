package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec.{Decoder, _}

/**
  * A language code for FRU data
  */
case class LanguageCode(code: Int) {
  def isEnglish: Boolean = code == 0 || code == 25
}

object LanguageCode {
  implicit val decoder: Decoder[LanguageCode] = new Decoder[LanguageCode] {
    def decode(data: ByteString) = LanguageCode(data(0).toUnsignedInt)
  }
}
