package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.protocol.readingoffset.EventReadingOffset

sealed trait ThresholdComparison extends EventReadingOffset

object ThresholdComparison {

  val readingOffsets: Map[Int, ThresholdComparison] = Map(
    0x05 -> UpperNonRecoverable,
    0x04 -> UpperCritical,
    0x03 -> UpperNonCritical,
    0x02 -> LowerNonRecoverable,
    0x01 -> LowerCritical,
    0x00 -> LowerNonCritical
  )

  implicit val setDecoder: Decoder[Set[ThresholdComparison]] =
    new Decoder[Set[ThresholdComparison]] {

      import scala.collection._

      def decode(data: ByteString): Predef.Set[ThresholdComparison] =
        immutable.BitSet
          .fromBitMask(Array(data(0).bits0To5.toUnsignedInt))
          .foldLeft(Set.empty[ThresholdComparison]) { (acc, bitNum) =>
            acc + readingOffsets(bitNum)
          }
          .toSet
    }

  case object LowerNonCritical extends ThresholdComparison

  case object LowerCritical extends ThresholdComparison

  case object LowerNonRecoverable extends ThresholdComparison

  case object UpperNonCritical extends ThresholdComparison

  case object UpperCritical extends ThresholdComparison

  case object UpperNonRecoverable extends ThresholdComparison

}
