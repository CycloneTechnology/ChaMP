package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

import scala.collection.{BitSet, immutable}

/**
  * Represents bits set (asserted) or masked for an event.
  */
case class EventBits(bits: BitSet) extends AnyVal {
  def isSet(bit: Int): Boolean = bits.contains(bit)

  def nonEmpty: Boolean = bits.nonEmpty
}

object EventBits {
  implicit val decoder: Decoder[EventBits] = new Decoder[EventBits] {
    def decode(data: ByteString): EventBits = {
      // So that we can call with one byte
      val padded = data.padTo(2, 0.toByte)

      EventBits(
        immutable.BitSet.fromBitMask(
          Array(((padded(0).toUnsignedInt << 8) + padded(1).toUnsignedInt).toLong)
        )
      )
    }
  }

  def apply(value: Int): EventBits = EventBits(
    immutable.BitSet.fromBitMask(Array(value.toLong))
  )

  val empty = EventBits(BitSet.empty)

  val full: EventBits = ByteString(0xff.toByte, 0xff.toByte).as[EventBits]
}