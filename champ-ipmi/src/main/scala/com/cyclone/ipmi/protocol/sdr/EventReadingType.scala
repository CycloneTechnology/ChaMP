package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.protocol.readingoffset._

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

/**
  * Indicates the type of reading and/or events that the sensor supports.
  * Table 42-1
  */
sealed trait EventReadingType {
  type EROffset <: EventReadingOffset

  val code: Byte

  def category: EventReadingCategory

  def eventOffsetFor(bit: Int): Option[EROffset]
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
    type EROffset = ThresholdEventOffset

    val code: Byte = 0x01.toByte

    val category: EventReadingCategory = EventReadingCategory.Threshold

    def eventOffsetFor(bit: Int): Option[EROffset] = ThresholdEventOffset.offsetFor(bit)
  }

  case object Usage extends EventReadingType {
    type EROffset = UsageEventOffset

    val code: Byte = 0x02.toByte

    val category: EventReadingCategory = EventReadingCategory.Discrete

    def eventOffsetFor(bit: Int): Option[EROffset] = UsageEventOffset.offsetFor(bit)
  }

  case object State extends EventReadingType {
    type EROffset = StateEventOffset

    val code: Byte = 0x03.toByte

    val category: EventReadingCategory = EventReadingCategory.Digital

    def eventOffsetFor(bit: Int): Option[EROffset] = StateEventOffset.offsetFor(bit)
  }

  case object Predictive extends EventReadingType {
    type EROffset = PredictiveEventOffset

    val code: Byte = 0x04.toByte

    val category: EventReadingCategory = EventReadingCategory.Digital

    def eventOffsetFor(bit: Int): Option[EROffset] = PredictiveEventOffset.offsetFor(bit)
  }

  case object Limit extends EventReadingType {
    type EROffset = LimitEventOffset

    val code: Byte = 0x05.toByte

    val category: EventReadingCategory = EventReadingCategory.Digital

    def eventOffsetFor(bit: Int): Option[EROffset] = LimitEventOffset.offsetFor(bit)
  }

  case object Performance extends EventReadingType {
    type EROffset = PerformanceEventOffset

    val code: Byte = 0x06.toByte

    val category: EventReadingCategory = EventReadingCategory.Digital

    def eventOffsetFor(bit: Int): Option[EROffset] = PerformanceEventOffset.offsetFor(bit)

  }

  case object Severity extends EventReadingType {
    type EROffset = SeverityEventOffset

    val code: Byte = 0x07.toByte

    val category: EventReadingCategory = EventReadingCategory.Discrete

    def eventOffsetFor(bit: Int): Option[EROffset] = SeverityEventOffset.offsetFor(bit)

  }

  case object Presence extends EventReadingType {
    type EROffset = PresenceEventOffset

    val code: Byte = 0x08.toByte

    val category: EventReadingCategory = EventReadingCategory.Digital

    def eventOffsetFor(bit: Int): Option[EROffset] = PresenceEventOffset.offsetFor(bit)
  }

  case object Enablement extends EventReadingType {
    type EROffset = EnablementEventOffset

    val code: Byte = 0x09.toByte

    val category: EventReadingCategory = EventReadingCategory.Digital

    def eventOffsetFor(bit: Int): Option[EROffset] = EnablementEventOffset.offsetFor(bit)
  }

  case object Status extends EventReadingType {
    type EROffset = StatusEventOffset

    val code: Byte = 0xa.toByte

    val category: EventReadingCategory = EventReadingCategory.Discrete

    def eventOffsetFor(bit: Int): Option[EROffset] = StatusEventOffset.offsetFor(bit)
  }

  case object Redundancy extends EventReadingType {
    type EROffset = RedundancyEventOffset

    val code: Byte = 0x0b.toByte

    val category: EventReadingCategory = EventReadingCategory.Discrete

    def eventOffsetFor(bit: Int): Option[EROffset] = RedundancyEventOffset.offsetFor(bit)
  }

  case object ACPIPower extends EventReadingType {
    type EROffset = ACPIPowerEventOffset

    val code: Byte = 0x0c.toByte

    val category: EventReadingCategory = EventReadingCategory.Discrete

    def eventOffsetFor(bit: Int): Option[EROffset] = ACPIPowerEventOffset.offsetFor(bit)

  }

  case object SensorSpecificRange extends EventReadingType {
    type EROffset = Nothing

    val code: Byte = 0x6f.toByte
    val category: EventReadingCategory = EventReadingCategory.SensorSpecific

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case class OemRange(code: Byte) extends EventReadingType {
    type EROffset = Nothing

    val category: EventReadingCategory = EventReadingCategory.Oem

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }
}
