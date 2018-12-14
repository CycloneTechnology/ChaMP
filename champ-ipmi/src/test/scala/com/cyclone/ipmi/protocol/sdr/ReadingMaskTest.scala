package com.cyclone.ipmi.protocol.sdr

import com.cyclone.ipmi.protocol.sdr.SensorType.PhysicalSecurity
import com.cyclone.ipmi.protocol.readingoffset.{PhysicalSecurityEventOffset, StateEventOffset, UsageEventOffset}
import org.scalatest.{Matchers, WordSpec}

/**
  * Tests for [[ReadingMask]]
  */
class ReadingMaskTest extends WordSpec with Matchers {
  "a discrete states reading mask" must {
    "allow evaluation of offsets" in {
      ReadingMask
        .DiscreteStates(EventBits.full, EventReadingType.Usage)
        .evaluateOffsets(EventBits(0x5)) shouldBe
      Set(UsageEventOffset.TransitionToIdle, UsageEventOffset.TransitionToBusy)
    }

    "ignore non-masked offsets" in {
      ReadingMask
        .DiscreteStates(EventBits(0x1), EventReadingType.Usage)
        .evaluateOffsets(EventBits(0x5)) shouldBe
      Set(UsageEventOffset.TransitionToIdle)
    }
  }

  "a digital (binary) reading mask" must {
    "use bit 0 value" in {
      ReadingMask
        .DigitalStates(EventBits.full, EventReadingType.State)
        .evaluateOffsets(EventBits(0x0)) shouldBe
      Set(StateEventOffset.StateDeasserted)

      ReadingMask
        .DigitalStates(EventBits.full, EventReadingType.State)
        .evaluateOffsets(EventBits(0x1)) shouldBe
      Set(StateEventOffset.StateAsserted)
    }

    "ignore non-masked offsets" in {
      ReadingMask
        .DigitalStates(EventBits.empty, EventReadingType.State)
        .evaluateOffsets(EventBits(0x1)) shouldBe
      Set.empty
    }
  }

  "a sensor-specific reading mask" must {
    "use sensor-specific offsets if required" in {
      ReadingMask
        .SensorSpecificDiscreteStates(EventBits.full, SensorType.PhysicalSecurity)
        .evaluateOffsets(EventBits(0x9)) shouldBe
      Set(
        PhysicalSecurityEventOffset.GeneralChassisIntrusion,
        PhysicalSecurityEventOffset.ProcessorAreaIntrusion
      )
    }

    "ignore non-masked offsets" in {
      ReadingMask
        .SensorSpecificDiscreteStates(EventBits.full, SensorType.PhysicalSecurity)
        .evaluateOffsets(EventBits(0x1)) shouldBe
      Set(PhysicalSecurityEventOffset.GeneralChassisIntrusion)
    }
  }

  "a threshold reading mask" must {
    "allow evaluation of offsets" in {
      ReadingMask
        .Threshold(EventBits.full)
        .evaluateOffsets(EventBits(0x5)) shouldBe
      Set(ThresholdComparison.LowerNonRecoverable, ThresholdComparison.LowerNonCritical)
    }

    "ignore non-masked offsets" in {
      ReadingMask
        .Threshold(EventBits(0x1))
        .evaluateOffsets(EventBits(0x5)) shouldBe
      Set(ThresholdComparison.LowerNonCritical)
    }
  }

}
