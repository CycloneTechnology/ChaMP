package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import org.joda.time.format.ISODateTimeFormat
import org.scalatest.{Inside, Matchers, WordSpec}

/**
  * Tests for [[StandardFru]] decoding
  */
class StandardFruTest extends WordSpec with Matchers with Inside {

  "a standard fru decoder" must {
    "handle cases with optional fruFileId fields" in {
      val bs = ByteString(1, 10, 0, 1, 0, 0, 0, -12, 1, 9, 0, -54, -82, 107, -125, 100, -55, -78, -34, 80, 111, 119,
        101, 114, 69, 100, 103, 101, 77, 55, 49, 48, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
        -50, 67, 78, 55, 48, 49, 54, 51, 57, 54, 50, 48, 48, 50, 50, -55, 48, 78, 53, 56, 51, 77, 65, 48, 49, -63, 2,
        -63, 0, 0, 45, 1, 68, 69, 76, 76, -13, 0, 0, 1, 2, 0, 0, 1, 32, -50, 5, 11, 112, 0, 12, -72, 0, 16, -56, 0, 14,
        -40, 0, 15, -24, 0, 0, 11, 72, 0, -83, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 12, 16, 0, -26, 2, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 16, 16, 0, -32, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 14, 16, 0, -69, 1, 9, 6, 3, 0, 0, 2, 9, 6, 3, 0, 0, 15, 16, 0, -34, 0, 0, 0, 1, 0, 1, 1, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

      val fru = bs.as[StandardFru]

      inside(fru.boardInfo) {
        case Some(bi) =>
          bi.manufacturer shouldBe "DELL"
          bi.productName shouldBe "PowerEdgeM710"
          bi.manufactureDate shouldBe ISODateTimeFormat.dateTimeParser().parseDateTime("2009-06-01T19:18:00.000+01:00")
      }
    }
  }
}
