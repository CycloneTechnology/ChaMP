package com.cyclone.util

import org.scalatest.{Matchers, WordSpec}
import CaseInsensitive._

/**
  * Tests for [[CaseInsensitive]]
  */
class CaseInsensitiveTest extends WordSpec with Matchers {

  "a case insensitive string" must {
    "provide case insensitive equals" in {
      "hello".i shouldBe "heLLo".i
    }

    "provide case insensitive comparison" in {
      "hello".i compareTo "heLLo".i shouldBe 0
      "a".i < "Z".i shouldBe true
      "A".i < "z".i shouldBe true
    }

    "provide case insensitive startsWith" in {
      "heLLo".i startsWith "hell".i shouldBe true
      "heLLo".i startsWith "ll".i shouldBe false
    }

    "can be trimmed" in {
      "heLLo".i.trim shouldBe "heLLo".i
      " \t\n heLLo \t\n".i.trim shouldBe "heLLo".i
    }

    "has size and empty methods" in {
      "heLLo".i.length shouldBe 5
      "heLLo".i.isEmpty shouldBe false
      "".i.isEmpty shouldBe true
    }
  }

}
