package com.cyclone.ipmi.codec

import akka.util.ByteString
import org.scalatest.{Matchers, WordSpec}

/**
  * Some tests for [[RichByteString]]
  */
class RichByteStringTest extends WordSpec with Matchers {
  "a RichByteString" when {
    "splitting on a predicate" must {
      "return an empty list for an empty ByteString" in {
        ByteString.empty.splitWhenever(_ => true) shouldBe List.empty
      }

      "return a singleton list containing the input when the predicate is never true" in {
        ByteString(1, 2, 3, 4, 5).splitWhenever(_ => false) shouldBe List(ByteString(1, 2, 3, 4, 5))
      }

      "split before the predicate is true" in {
        ByteString(2, 3, 4, 5).splitWhenever(_ % 2 != 0) shouldBe
        List(ByteString(2), ByteString(3, 4), ByteString(5))
      }

      "split before the predicate is true (first element satisfies predicate)" in {
        ByteString(1, 2, 3, 4, 5).splitWhenever(_ % 2 != 0) shouldBe
        List(ByteString(1, 2), ByteString(3, 4), ByteString(5))
      }
    }
  }
}
