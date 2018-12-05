package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * Determines which discrete states or threshold comparison values can be read from a sensor
  * (via Get Sensor Reading command).
  *
  * Compare with [[EventMask]].
  */
sealed trait ReadingMask {
  def evaluateOffsets(eventBits: EventBits): Set[EventReadingOffset]
}

object ReadingMask {

  case class Threshold(stateOffsetsReadable: EventBits) extends ReadingMask {

    def evaluateOffsets(eventBits: EventBits): Set[EventReadingOffset] =
      eventBits.bits
        .foldLeft(Set.empty[EventReadingOffset]) { (acc, bit) =>
          if (stateOffsetsReadable.isSet(bit))
            acc + ThresholdComparison.readingOffsets(bit)
          else acc
        }
  }

  object Threshold {
    val empty = Threshold(EventBits.empty)
    val full = Threshold(ByteString(0xff.toByte, 0xff.toByte).as[EventBits])
  }

  case class DigitalStates(
    stateOffsetsReadable: EventBits,
    eventReadingType: EventReadingType) extends ReadingMask {

    def evaluateOffsets(eventBits: EventBits): Set[EventReadingOffset] =
      if (stateOffsetsReadable.isSet(0))
        if (eventBits.nonEmpty)
          eventReadingType.eventOffsetFor(1).toSet
        else
          eventReadingType.eventOffsetFor(0).toSet
      else
        Set.empty
  }

  case class DiscreteStates(
    stateOffsetsReadable: EventBits,
    eventReadingType: EventReadingType) extends ReadingMask {

    def evaluateOffsets(eventBits: EventBits): Set[EventReadingOffset] =
      eventBits.bits
        .foldLeft(Set.empty[EventReadingOffset]) { (acc, bit) =>
          (stateOffsetsReadable.isSet(bit), eventReadingType.eventOffsetFor(bit)) match {
            case (true, Some(offset)) => acc + offset
            case _                    => acc
          }
        }
  }

  case class SensorSpecificDiscreteStates(
    stateOffsetsReadable: EventBits,
    sensorType: SensorType) extends ReadingMask {

    def evaluateOffsets(eventBits: EventBits): Set[EventReadingOffset] =
      eventBits.bits
        .foldLeft(Set.empty[EventReadingOffset]) { (acc, bit) =>
          (stateOffsetsReadable.isSet(bit), sensorType.eventOffsetFor(bit)) match {
            case (true, Some(offset)) => acc + offset
            case _                    => acc
          }
        }
  }

  // TODO...
  case object Oem extends ReadingMask {
    def evaluateOffsets(eventBits: EventBits): Set[EventReadingOffset] = Set.empty
  }

}
