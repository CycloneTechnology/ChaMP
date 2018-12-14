package com.cyclone.ipmi.protocol.fru

import akka.util.ByteString
import org.scalatest.{Matchers, WordSpec}

/**
  * Tests for [[FieldsIterator]]
  */
class FieldsIteratorTest extends WordSpec with Matchers {

  implicit val languageCode = LanguageCode(0x0)
  val noMore = ByteString(0xc1.toByte)

  "a FieldsIterator" must {
    "decode bytes to a sequence of a single fields" in {
      FieldsIterator
        .from(
          ByteString(5, 1, 2, 3, 4, 5) ++
          noMore
        )
        .toSeq shouldBe Seq(BinaryField(ByteString(1, 2, 3, 4, 5)))
    }

    "decode bytes to a sequence of a multiple fields" in {
      FieldsIterator
        .from(
          ByteString(5, 1, 2, 3, 4, 5) ++
          ByteString(2, 1, 2) ++
          noMore
        )
        .toSeq shouldBe Seq(
        BinaryField(ByteString(1, 2, 3, 4, 5)),
        BinaryField(ByteString(1, 2))
      )
    }

    val iterator = FieldsIterator.from(
      ByteString(5, 1, 2, 3, 4, 5) ++
      ByteString(2, 1, 2) ++
      ByteString(3, 1, 2, 3) ++
      ByteString(4, 1, 2, 3, 4) ++
      noMore
    )

    "allow iteration" in {
      iterator.next() shouldBe BinaryField(ByteString(1, 2, 3, 4, 5))
      iterator.next() shouldBe BinaryField(ByteString(1, 2))
    }

    "support getting the remainder after iteration" in {
      iterator.toSeq shouldBe Seq(BinaryField(ByteString(1, 2, 3)), BinaryField(ByteString(1, 2, 3, 4)))
    }

    "allow optional iteration" in {
      val it = FieldsIterator.from(
        ByteString(5, 1, 2, 3, 4, 5) ++
        noMore
      )
      it.nextOpt() shouldBe Some(BinaryField(ByteString(1, 2, 3, 4, 5)))
      it.nextOpt() shouldBe None
      it.nextOpt() shouldBe None

      it.toSeq shouldBe Seq.empty
    }
  }
}
