package com.cyclone.ipmi.sdr

/**
  * Mask that indicates which offsets are asserted or de-asserted by a sensor when getting sensor events
  * (discrete or threshold) (via Get Sensor Event Status command).
  *
  * Compare with [[ReadingMask]].
  */
case class EventMask(
  assertionOffsetBits: EventBits,
  deassertionOffsetBits: EventBits
)

object EventMask {
  val empty = EventMask(EventBits.empty, EventBits.empty)
}
