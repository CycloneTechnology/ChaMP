package com.cyclone.util

import com.cyclone.util.NumberUtils._
import org.scalatest.{Matchers, WordSpec}

/**
  * Tests for [[NumberUtils]]
  */
class NumberUtilsTest extends WordSpec with Matchers {
  "NumberUtils" when {
    "isInteger" must {
      "work" in {
        isInteger("1") shouldBe true
        isInteger("-1") shouldBe true

        isInteger(" 1 ") shouldBe false
        isInteger("1.0") shouldBe false
        isInteger("A") shouldBe false
        isInteger("1.0E-4") shouldBe false
        isInteger(null) shouldBe false
      }
    }

    "parseInt" must {
      "work" in {
        parseInt("1") shouldBe Some(1)
        parseInt("-1") shouldBe Some(-1)

        parseInt(" 1 ") shouldBe None
        parseInt("1.0") shouldBe None
        parseInt("A") shouldBe None
        parseInt("1.0E-4") shouldBe None
        parseInt(null) shouldBe None
      }
    }
  }
}
