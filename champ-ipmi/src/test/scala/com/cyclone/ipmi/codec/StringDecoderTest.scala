package com.cyclone.ipmi.codec

import akka.util.ByteString
import com.cyclone.ipmi.codec.StringDecoder._
import org.scalatest.{Matchers, WordSpec}

/**
  * Tests for [[StringDecoder]]
  */
class StringDecoderTest extends WordSpec with Matchers {
  "a 6-bit ascii encoding" must {
    "decode" in {
      SixBitAsciiDecoder.decode(ByteString(0x29, 0xdc, 0xa6)) shouldBe "IPMI"

      // Check it trims spaces from unused parts of the encoding
      // (i.e. use only 3 of the maximum 4 chars in 3 bytes)
      SixBitAsciiDecoder.decode(ByteString(0x29, 0xdc, 0x2)) shouldBe "IPM"
    }
  }

  "a base 26 encoding" must {
    "encode 0 to 25 as A to Z" in {
      for (i <- 0 to 25) {
        Base26NumberEncoding.encode(i) shouldBe (i + 'A').toChar.toString
      }
    }

    "encode large numbers correctly" in {
      Base26NumberEncoding.encode(26) shouldBe "AA"
      Base26NumberEncoding.encode(27) shouldBe "AB"

      Base26NumberEncoding.encode(701) shouldBe "ZZ"
      Base26NumberEncoding.encode(702) shouldBe "AAA"
    }
  }

  "a bcd encoding" must {
    "decode" in {
      BcdDecoder.decode(ByteString(0xb, 1, 2, 3, 0xc, 0)) shouldBe "-123.0"
    }
  }
}
