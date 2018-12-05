package com.cyclone.util

import com.cyclone.util.StringUtils._
import org.scalatest.{Matchers, WordSpec}

/**
  * Tests for [[StringUtils]]
  */
class StringUtilsTest extends WordSpec with Matchers {
  "StringUtils" when {
    "hasText" must {
      "work" in {
        hasText("") shouldBe false
        hasText(" ") shouldBe false
        hasText("\n\t") shouldBe false
        hasText(null) shouldBe false
        hasText("ABC") shouldBe true
        hasText(" ABC ") shouldBe true
      }
    }

    "toOption" must {
      "work" in {
        toOption("") shouldBe None
        toOption(" ") shouldBe None
        toOption("\n\t") shouldBe None
        toOption(null) shouldBe None
        toOption("ABC") shouldBe Some("ABC")
        toOption(" ABC ") shouldBe Some(" ABC ")
      }
    }
  }
}
