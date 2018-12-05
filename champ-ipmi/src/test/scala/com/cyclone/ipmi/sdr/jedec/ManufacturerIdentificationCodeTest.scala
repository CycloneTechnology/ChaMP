package com.cyclone.ipmi.sdr.jedec

import akka.util.ByteString
import com.cyclone.ipmi.sdr.jedec.Bank01.{Fujitsu, Intel}
import com.cyclone.ipmi.sdr.jedec.Bank02.ThreeCom
import com.cyclone.ipmi.sdr.jedec.Bank03.Hypertec
import com.cyclone.ipmi.sdr.jedec.Bank05.UsModular
import com.cyclone.ipmi.sdr.jedec.ManufacturerIdentificationCode._
import org.scalatest.{Matchers, WordSpec}

/**
  * Tests for JEDEC lookup of [[ManufacturerIdentificationCode]]
  */
class ManufacturerIdentificationCodeTest extends WordSpec with Matchers {
  "a JEDEC lookup for a manufacturer" when {
    "getting with specified bank" when {
      "bank exists" must {
        "return the manufacturer" in {
          decodeUsingContinuationCount(ByteString(0x04, UsModular.code)) shouldBe Some(UsModular)
        }
      }

      "bank exists and odd parity bit 6 set" must {
        "return the manufacturer" in {
          decodeUsingContinuationCount(ByteString(0x80, Fujitsu.code)) shouldBe Some(Fujitsu)
        }
      }

      "bank does not exist" must {
        "return None" in {
          decodeUsingContinuationCount(ByteString(0x10, ThreeCom.code)) shouldBe None
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
          decodeUsingContinuationFlag(ByteString(Intel.code)) shouldBe Some(Intel)
        }
      }

      "subsequent bank" must {
        "return the manufacturer" in {
          decodeUsingContinuationFlag(ByteString(0x7f, 0x7f, Hypertec.code)) shouldBe Some(Hypertec)
        }
      }

      "not in specified bank" must {
        "return None" in {
          decodeUsingContinuationFlag(ByteString(0x7f, 0x77)) shouldBe None
        }
      }

      "no id after continuation code" must {
        "return None" in {
          decodeUsingContinuationFlag(ByteString.fromArray(Array.fill(3)(0x7f))) shouldBe None
        }
      }

      "too few banks" must {
        "return None" in {
          decodeUsingContinuationFlag(ByteString.fromArray(Array.fill(20)(0x7f))) shouldBe None
        }
      }

    }
  }
}
