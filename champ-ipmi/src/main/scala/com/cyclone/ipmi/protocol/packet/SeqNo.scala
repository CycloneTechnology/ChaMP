package com.cyclone.ipmi.protocol.packet

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * Wrapper for a sequence number used to tie up requests with responses.
  *
  * '''This sequence number is not to be confused with [[SessionSequenceNumber]].'''
  */
case class SeqNo(value: Byte) extends AnyVal with Ordered[SeqNo] {
  def compare(that: SeqNo): Int = value compare that.value
}

object SeqNo {
  implicit val codec: Codec[SeqNo] = new Codec[SeqNo] {
    def encode(a: SeqNo) = ByteString(a.value.bits0To5 << 2)

    def decode(data: ByteString) = SeqNo(data(0).bits2To7)
  }

  /**
    * All available sequence numbers (6 bits)
    */
  val allSeqNos: Seq[SeqNo] = seqNosFrom(1 until 64)

  def seqNosFrom(range: Range): Seq[SeqNo] = range.map(_.toByte).map(SeqNo(_))
}
