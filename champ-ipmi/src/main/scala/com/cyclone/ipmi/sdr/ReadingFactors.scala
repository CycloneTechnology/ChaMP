package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

case class ReadingFactors(
  m: Int,
  b: Int,
  bExp: Int,
  rExp: Int,
  tolerance: RawTolerance,
  accuracy: RawAccuracy
)

object ReadingFactors {
  implicit val decoder: Decoder[ReadingFactors] = new Decoder[ReadingFactors] {

    def decode(data: ByteString): ReadingFactors = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      // M and tolerance
      val mLS = is.readByte
      val mMSAndTolerance = is.readByte

      val tolerance = mMSAndTolerance.as[RawTolerance]
      val m = tenBitTwosComplement(mLS, mMSAndTolerance.bits6To7)

      // B and accuracy
      val bLS = is.readByte
      val bMSAndAccuracyLS = is.readByte

      val b = tenBitTwosComplement(bLS, bMSAndAccuracyLS.bits6To7)

      val accuracyMSAndExp = is.readByte

      val accuracyLS = bMSAndAccuracyLS.bits0To5
      val accuracyMS = accuracyMSAndExp.bits4To7
      val accuracyExp = accuracyMSAndExp.bits2To3.toUnsignedInt

      val accuracy = accuracyMS.toUnsignedInt << 6 + accuracyLS.toUnsignedInt

      val expByte = is.readByte
      val rExp = fourBitTwosComplement(expByte.bits4To7)
      val bExp = fourBitTwosComplement(expByte.bits0To3)

      ReadingFactors(
        m = m,
        b = b,
        bExp = bExp,
        rExp = rExp,
        tolerance = tolerance,
        accuracy = RawAccuracy.fromPerTenThouAndExp(accuracy, accuracyExp)
      )
    }

    private def tenBitUnsigned(ls: Byte, ms: Byte): Int = ByteString(ls, ms, 0, 0).as[Int]

    private def tenBitTwosComplement(ls: Byte, ms: Byte): Int = {
      val unsigned = tenBitUnsigned(ls, ms)

      if (unsigned < 512) unsigned else unsigned - 1024
    }

    private def fourBitTwosComplement(byte: Byte): Int = {
      val unsigned = byte.toUnsignedInt // Should be < 128 anyhow

      if (unsigned < 8) unsigned else unsigned - 16
    }
  }
}
