package com.cyclone.ipmi.codec
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}

/**
  * Tests for [[MoreCodecs]]
  */
class MoreCodecsTest extends WordSpec with Matchers with GeneratorDrivenPropertyChecks {
  val usAsciiStringGen = Gen.containerOf[Array, Char](Gen.choose[Char](1, 127)).map(_.mkString)

  val maxLen = 255

  "MoreCodecs" must {

    def test(codec: Codec[String]) =
      forAll(usAsciiStringGen) { str =>
        val string = str.take(maxLen)

        val encoded = codec.encode(string)
        val decoded = codec.decode(encoded)

        decoded shouldBe string
      }

    "encode and decode null terminated ascii" in {
      test(MoreCodecs.nullTerminatedStringCoder(maxLen))
    }

    "encode and decode length prefixed strings" in {
      test(MoreCodecs.shortStringLengthPrefixedStringCodec)
    }
  }
}
