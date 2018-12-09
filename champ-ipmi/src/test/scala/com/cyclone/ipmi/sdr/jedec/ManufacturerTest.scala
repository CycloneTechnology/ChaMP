package com.cyclone.ipmi.sdr.jedec

import akka.util.ByteString
import com.cyclone.ipmi.sdr.jedec.Manufacturer._
import org.scalatest.{Matchers, WordSpec}

/**
  * Tests for JEDEC lookup of [[Manufacturer]]
  */
class ManufacturerTest extends WordSpec with Matchers {
  "a JEDEC lookup for a manufacturer" when {
    "getting with specified bank" when {
      "bank exists" must {
        "return the manufacturer" in {
          decodeUsingContinuationCount(ByteString(0x04, 0xA8.toByte)) shouldBe Some(Manufacturer("US Modular"))
        }
      }

      "bank exists and odd parity bit 6 set" must {
        "return the manufacturer" in {
          decodeUsingContinuationCount(ByteString(0x80, 0x04.toByte)) shouldBe Some(Manufacturer("Fujitsu"))
        }
      }

      "bank does not exist" must {
        "return None" in {
          decodeUsingContinuationCount(ByteString(0x10, 0xFE.toByte)) shouldBe None
        }
      }

      "not in specified bank" must {
        "return unknown manufacturer" in {
          decodeUsingContinuationCount(ByteString(0x01, 0x77)) shouldBe None
        }
      }
    }

    "getting using continuation flag" when {
      "first bank" must {
        "return the manufacturer" in {
          decodeUsingContinuationFlag(ByteString(0x89.toByte)) shouldBe Some(Manufacturer("Intel"))
        }
      }

      "subsequent bank" must {
        "return the manufacturer" in {
          decodeUsingContinuationFlag(ByteString(0x7f, 0x7f, 0x85.toByte)) shouldBe Some(Manufacturer("HYPERTEC"))
        }
      }

      "not in specified bank" must {
        "return None" in {
          decodeUsingContinuationFlag(ByteString(0x7f, 0x77)) shouldBe None
        }
      }

      "no id after continuation code" must {
        "return None" in {
          decodeUsingContinuationFlag(ByteString.fromArray(Array.fill(3)(0x7f.toByte))) shouldBe None
        }
      }

      "too few banks" must {
        "return None" in {
          decodeUsingContinuationFlag(ByteString.fromArray(Array.fill(20)(0x7f.toByte))) shouldBe None
        }
      }

    }
  }
}
