package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait EventReadingCategory

// Table 42-1
object EventReadingCategory {

  /**
    * Generic discrete event/reading
    */
  case object Discrete extends EventReadingCategory

  /**
    * Discrete but single bit used for on/off when reading
    */
  case object Digital extends EventReadingCategory

  /**
    * [[ThresholdComparison]] values are used instead for reading
    */
  case object Threshold extends EventReadingCategory

  /**
    * Indicates that [[SensorType]] should be used instead
    */
  case object SensorSpecific extends EventReadingCategory

  case object Oem extends EventReadingCategory

}

trait EventReadingOffset

/**
  * Indicates the type of reading and/or events that the sensor supports.
  * Table 42-1
  */
sealed trait EventReadingType {
  val code: Byte

  def category: EventReadingCategory

  def eventOffsetFor(bit: Int): Option[EventReadingOffset]
}

object EventReadingType {
  implicit val decoder: Decoder[EventReadingType] = new Decoder[EventReadingType] {

    def decode(data: ByteString): EventReadingType =
      data(0) match {
        case Threshold.code           => Threshold
        case Usage.code               => Usage
        case State.code               => State
        case Predictive.code          => Predictive
        case Limit.code               => Limit
        case Performance.code         => Performance
        case Severity.code            => Severity
        case Presence.code            => Presence
        case Enablement.code          => Enablement
        case Status.code              => Status
        case Redundancy.code          => Redundancy
        case ACPIPower.code           => ACPIPower
        case SensorSpecificRange.code => SensorSpecificRange
        case b if b.in(0x70 to 0x7f)  => OemRange(b)
      }
  }

  case object Threshold extends EventReadingType {
    val code: Byte = 0x01.toByte

    val category: EventReadingCategory = EventReadingCategory.Threshold

    sealed trait ThresholdEventOffset

    object ThresholdEventOffset {

      case object LowerNonCriticalGoingLow extends EventReadingOffset

      case object LowerNonCriticalGoingHigh extends EventReadingOffset

      case object LowerCriticalGoingLow extends EventReadingOffset

      case object LowerCriticalGoingHigh extends EventReadingOffset

      case object LowerNonRecoverableGoingLow extends EventReadingOffset

      case object LowerNonRecoverableGoingHigh extends EventReadingOffset

      case object UpperNonCriticalGoingLow extends EventReadingOffset

      case object UpperNonCriticalGoingHigh extends EventReadingOffset

      case object UpperCriticalGoingLow extends EventReadingOffset

      case object UpperCriticalGoingHigh extends EventReadingOffset

      case object UpperNonRecoverableGoingLow extends EventReadingOffset

      case object UpperNonRecoverableGoingHigh extends EventReadingOffset

    }

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(ThresholdEventOffset.LowerNonCriticalGoingLow)
      case 0x01 => Some(ThresholdEventOffset.LowerNonCriticalGoingHigh)
      case 0x02 => Some(ThresholdEventOffset.LowerCriticalGoingLow)
      case 0x03 => Some(ThresholdEventOffset.LowerCriticalGoingHigh)
      case 0x04 => Some(ThresholdEventOffset.LowerNonRecoverableGoingLow)
      case 0x05 => Some(ThresholdEventOffset.LowerNonRecoverableGoingHigh)
      case 0x06 => Some(ThresholdEventOffset.UpperNonCriticalGoingLow)
      case 0x07 => Some(ThresholdEventOffset.UpperNonCriticalGoingHigh)
      case 0x08 => Some(ThresholdEventOffset.UpperCriticalGoingLow)
      case 0x09 => Some(ThresholdEventOffset.UpperCriticalGoingHigh)
      case 0x0A => Some(ThresholdEventOffset.UpperNonRecoverableGoingLow)
      case 0x0B => Some(ThresholdEventOffset.UpperNonRecoverableGoingHigh)
      case _    => None
    }
  }

  case object Usage extends EventReadingType {
    val code: Byte = 0x02.toByte

    val category: EventReadingCategory = EventReadingCategory.Discrete

    sealed trait UsageEventOffset

    object UsageEventOffset {

      case object TransitionToIdle extends EventReadingOffset

      case object TransitionToActive extends EventReadingOffset

      case object TransitionToBusy extends EventReadingOffset

    }

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(UsageEventOffset.TransitionToIdle)
      case 0x01 => Some(UsageEventOffset.TransitionToActive)
      case 0x02 => Some(UsageEventOffset.TransitionToBusy)
      case _    => None
    }
  }

  case object State extends EventReadingType {
    val code: Byte = 0x03.toByte

    val category: EventReadingCategory = EventReadingCategory.Digital

    sealed trait StateEventOffset

    object StateEventOffset {

      case object StateDeasserted extends EventReadingOffset

      case object StateAsserted extends EventReadingOffset

    }

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(StateEventOffset.StateDeasserted)
      case 0x01 => Some(StateEventOffset.StateAsserted)
      case _    => None
    }
  }

  case object Predictive extends EventReadingType {
    val code: Byte = 0x04.toByte

    val category: EventReadingCategory = EventReadingCategory.Digital

    sealed trait PredictiveEventOffset

    object PredictiveEventOffset {

      case object PredictiveFailureDeasserted extends EventReadingOffset

      case object PredictiveFailureAsserted extends EventReadingOffset

    }

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(PredictiveEventOffset.PredictiveFailureDeasserted)
      case 0x01 => Some(PredictiveEventOffset.PredictiveFailureAsserted)
      case _    => None
    }
  }

  case object Limit extends EventReadingType {
    val code: Byte = 0x05.toByte

    val category: EventReadingCategory = EventReadingCategory.Digital

    sealed trait LimitEventOffset

    object LimitEventOffset {

      case object LimitNotExceeded extends EventReadingOffset

      case object LimitExceeded extends EventReadingOffset

    }

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(LimitEventOffset.LimitNotExceeded)
      case 0x01 => Some(LimitEventOffset.LimitExceeded)
      case _    => None
    }
  }

  case object Performance extends EventReadingType {
    val code: Byte = 0x06.toByte

    val category: EventReadingCategory = EventReadingCategory.Digital

    sealed trait PerformanceEventOffset

    object PerformanceEventOffset {

      case object PerformanceMet extends EventReadingOffset

      case object PerformanceLags extends EventReadingOffset

    }

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(PerformanceEventOffset.PerformanceMet)
      case 0x01 => Some(PerformanceEventOffset.PerformanceLags)
      case _    => None
    }
  }

  case object Severity extends EventReadingType {
    val code: Byte = 0x07.toByte

    val category: EventReadingCategory = EventReadingCategory.Discrete

    sealed trait SeverityEventOffset

    object SeverityEventOffset {

      case object TransitionToOK extends EventReadingOffset

      case object TransitionToNonCriticalFromOK extends EventReadingOffset

      case object TransitionToCriticalFromLessSevere extends EventReadingOffset

      case object TransitionToNonRecoverableFromLessSevere extends EventReadingOffset

      case object TransitionToNonCriticalFromMoreSevere extends EventReadingOffset

      case object TransitionToCriticalFromNonRecoverable extends EventReadingOffset

      case object TransitionToNonRecoverable extends EventReadingOffset

      case object Monitor extends EventReadingOffset

      case object Informational extends EventReadingOffset

    }

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(SeverityEventOffset.TransitionToOK)
      case 0x01 => Some(SeverityEventOffset.TransitionToNonCriticalFromOK)
      case 0x02 => Some(SeverityEventOffset.TransitionToCriticalFromLessSevere)
      case 0x03 => Some(SeverityEventOffset.TransitionToNonRecoverableFromLessSevere)
      case 0x04 => Some(SeverityEventOffset.TransitionToNonCriticalFromMoreSevere)
      case 0x05 => Some(SeverityEventOffset.TransitionToCriticalFromNonRecoverable)
      case 0x06 => Some(SeverityEventOffset.TransitionToNonRecoverable)
      case 0x07 => Some(SeverityEventOffset.Monitor)
      case 0x08 => Some(SeverityEventOffset.Informational)
      case _    => None
    }
  }

  case object Presence extends EventReadingType {
    val code: Byte = 0x08.toByte

    val category: EventReadingCategory = EventReadingCategory.Digital

    sealed trait PresenceEventOffset

    object PresenceEventOffset {

      case object DeviceRemovedDeviceAbsent extends EventReadingOffset

      case object DeviceInsertedDevicePresent extends EventReadingOffset

    }

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(PresenceEventOffset.DeviceRemovedDeviceAbsent)
      case 0x01 => Some(PresenceEventOffset.DeviceInsertedDevicePresent)
      case _    => None
    }
  }

  case object Enablement extends EventReadingType {
    val code: Byte = 0x09.toByte

    val category: EventReadingCategory = EventReadingCategory.Digital

    sealed trait EnablementEventOffset

    object EnablementEventOffset {

      case object DeviceDisabled extends EventReadingOffset

      case object DeviceEnabled extends EventReadingOffset

    }

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(EnablementEventOffset.DeviceDisabled)
      case 0x01 => Some(EnablementEventOffset.DeviceEnabled)
      case _    => None
    }
  }

  case object Status extends EventReadingType {
    val code: Byte = 0xa.toByte

    val category: EventReadingCategory = EventReadingCategory.Discrete

    sealed trait StatusEventOffset

    object StatusEventOffset {

      case object TransitionToRunning extends EventReadingOffset

      case object TransitionToInTest extends EventReadingOffset

      case object TransitionToPowerOff extends EventReadingOffset

      case object TransitionToOnLine extends EventReadingOffset

      case object TransitionToOffLine extends EventReadingOffset

      case object TransitionToOffDuty extends EventReadingOffset

      case object TransitionToDegraded extends EventReadingOffset

      case object TransitionToPowerSave extends EventReadingOffset

      case object InstallError extends EventReadingOffset

    }

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(StatusEventOffset.TransitionToRunning)
      case 0x01 => Some(StatusEventOffset.TransitionToInTest)
      case 0x02 => Some(StatusEventOffset.TransitionToPowerOff)
      case 0x03 => Some(StatusEventOffset.TransitionToOnLine)
      case 0x04 => Some(StatusEventOffset.TransitionToOffLine)
      case 0x05 => Some(StatusEventOffset.TransitionToOffDuty)
      case 0x06 => Some(StatusEventOffset.TransitionToDegraded)
      case 0x07 => Some(StatusEventOffset.TransitionToPowerSave)
      case 0x08 => Some(StatusEventOffset.InstallError)
      case _    => None
    }
  }

  case object Redundancy extends EventReadingType {
    val code: Byte = 0x0b.toByte

    val category: EventReadingCategory = EventReadingCategory.Discrete

    sealed trait RedundancyEventOffset

    object RedundancyEventOffset {

      case object FullyRedundant extends EventReadingOffset

      case object RedundancyLost extends EventReadingOffset

      case object RedundancyDegraded extends EventReadingOffset

      case object NonRedundantSufficientResourcesFromRedundant extends EventReadingOffset

      case object NonRedundantSufficientResourcesFromInsufficientResources extends EventReadingOffset

      case object NonRedundantInsufficientResources extends EventReadingOffset

      case object RedundancyDegradedFromFullyRedundant extends EventReadingOffset

      case object RedundancyDegradedFromNonRedundant extends EventReadingOffset

    }

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(RedundancyEventOffset.FullyRedundant)
      case 0x01 => Some(RedundancyEventOffset.RedundancyLost)
      case 0x02 => Some(RedundancyEventOffset.RedundancyDegraded)
      case 0x03 => Some(RedundancyEventOffset.NonRedundantSufficientResourcesFromRedundant)
      case 0x04 =>
        Some(RedundancyEventOffset.NonRedundantSufficientResourcesFromInsufficientResources)
      case 0x05 => Some(RedundancyEventOffset.NonRedundantInsufficientResources)
      case 0x06 => Some(RedundancyEventOffset.RedundancyDegradedFromFullyRedundant)
      case 0x07 => Some(RedundancyEventOffset.RedundancyDegradedFromNonRedundant)
      case _    => None
    }
  }

  case object ACPIPower extends EventReadingType {
    val code: Byte = 0x0c.toByte

    val category: EventReadingCategory = EventReadingCategory.Discrete

    sealed trait ACPIPowerEventOffset

    object ACPIPowerEventOffset {

      case object D0PowerState extends EventReadingOffset

      case object D1PowerState extends EventReadingOffset

      case object D2PowerState extends EventReadingOffset

      case object D3PowerState extends EventReadingOffset

    }

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(ACPIPowerEventOffset.D0PowerState)
      case 0x01 => Some(ACPIPowerEventOffset.D1PowerState)
      case 0x02 => Some(ACPIPowerEventOffset.D2PowerState)
      case 0x03 => Some(ACPIPowerEventOffset.D3PowerState)
      case _    => None
    }
  }

  case object SensorSpecificRange extends EventReadingType {
    val code: Byte = 0x6f.toByte
    val category: EventReadingCategory = EventReadingCategory.SensorSpecific

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case class OemRange(code: Byte) extends EventReadingType {
    val category: EventReadingCategory = EventReadingCategory.Oem

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

}
