package com.cyclone.ipmi.sdr

import com.cyclone.ipmi.sdr.EventReadingType.{State, Usage}
import com.cyclone.ipmi.sdr.SensorType.PhysicalSecurity
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
      Set(Usage.UsageEventOffset.TransitionToIdle, Usage.UsageEventOffset.TransitionToBusy)
    }

    "ignore non-masked offsets" in {
      ReadingMask
        .DiscreteStates(EventBits(0x1), EventReadingType.Usage)
        .evaluateOffsets(EventBits(0x5)) shouldBe
      Set(Usage.UsageEventOffset.TransitionToIdle)
    }
  }

  "a digital (binary) reading mask" must {
    "use bit 0 value" in {
      ReadingMask
        .DigitalStates(EventBits.full, EventReadingType.State)
        .evaluateOffsets(EventBits(0x0)) shouldBe
      Set(State.StateEventOffset.StateDeasserted)

      ReadingMask
        .DigitalStates(EventBits.full, EventReadingType.State)
        .evaluateOffsets(EventBits(0x1)) shouldBe
      Set(State.StateEventOffset.StateAsserted)
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
        PhysicalSecurity.PhysicalSecurityEventOffset.GeneralChassisIntrusion,
        PhysicalSecurity.PhysicalSecurityEventOffset.ProcessorAreaIntrusion
      )
    }

    "ignore non-masked offsets" in {
      ReadingMask
        .SensorSpecificDiscreteStates(EventBits.full, SensorType.PhysicalSecurity)
        .evaluateOffsets(EventBits(0x1)) shouldBe
      Set(PhysicalSecurity.PhysicalSecurityEventOffset.GeneralChassisIntrusion)
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
