package com.cyclone.ipmi.protocol.packet

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * Wrapper for a session sequence number.
  *
  * The client and the BMC keep track of sequence numbers separately,
  * incrementing for each message that they send.
  *
  * '''This sequence number is not to be confused with [[SeqNo]].'''
  * The latter number is not incremented for resends and is echoed back in command responses.
  */
case class SessionSequenceNumber(value: Int) extends AnyVal {
  def +(inc: Int) = SessionSequenceNumber(value + inc)
}

object SessionSequenceNumber {
  implicit val codec: Codec[SessionSequenceNumber] = new Codec[SessionSequenceNumber] {
    def encode(a: SessionSequenceNumber): ByteString = a.value.toBin

    def decode(data: ByteString) =
      SessionSequenceNumber(data.as[Int])
  }

  val NoSession = SessionSequenceNumber(0)
}
