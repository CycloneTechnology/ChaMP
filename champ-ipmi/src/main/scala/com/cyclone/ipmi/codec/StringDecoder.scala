package com.cyclone.ipmi.codec

import java.nio.charset.Charset

import akka.util.ByteString
import com.google.common.base.Charsets

/**
  * For decoding a [[ByteString]] into a String
  */
trait StringDecoder extends Decoder[String]

object StringDecoder {

  case class WithCharacterDecoder(charset: Charset, trim: Boolean) extends StringDecoder {
    def decode(bs: ByteString): String = {
      val s = bs.decodeString(charset.name)

      if (trim) s.trim else s
    }
  }

  val AsciiLatin = WithCharacterDecoder(Charsets.ISO_8859_1, trim = true)
  val Utf8 = WithCharacterDecoder(Charsets.UTF_8, trim = true)
  val Unicode = WithCharacterDecoder(Charsets.UTF_16LE, trim = true)

  object Hex extends StringDecoder {
    def decode(bs: ByteString): String = bs.toHexString()
  }

  /**
    * 6-bit ASCII string encoding (see section 13)
    */
  object SixBitAsciiDecoder extends StringDecoder {
    def decode(data: ByteString): String = {
      // 6-bit ascii packs first character into least sig bits of first bytes
      // with the remaining two bits forming the least significant bits of the next character, etc
      val binaryChars = data.reverse.flatMap(b => Integer.toBinaryString(b.toUnsignedInt + 0x100).substring(1))

      binaryChars.grouped(6).map { chars =>
        val bin = chars.mkString("")
        (Integer.parseInt(bin, 2) + ' ').toChar
      }.toSeq.reverse.mkString("").trim
    }
  }

  /**
    * A BCD numeric string encoding
    */
  object BcdDecoder extends StringDecoder {
    def decode(data: ByteString): String = {
      val chars = data.map(_.toUnsignedInt).collect {
        case 0xa                         => ' '
        case 0xb                         => '-'
        case 0xc                         => '.'
        case i: Int if 0 to 9 contains i => (i + '0').toChar
      }.toArray

      new String(chars)
    }
  }

}
