package com.cyclone.ipmi

import org.scalatest.{Matchers, WordSpec}

import IpmiVersionRequirement._

/**
  * Tests for [[IpmiVersionRequirement]]
  */
class IpmiVersionRequirementTest extends WordSpec with Matchers {
  "an IpmiVersionRequirement" must {
    def testParse(versionRequirement: IpmiVersionRequirement) = {
      parse(versionRequirement.toString) shouldBe Some(versionRequirement)
      parse(versionRequirement.toString.toLowerCase) shouldBe Some(versionRequirement)
    }

    "be able to parse strings" in {
      testParse(V15Only)
      testParse(V20Only)
      testParse(V20IfSupported)

      parse("SomeOther") shouldBe None
    }
  }

}
