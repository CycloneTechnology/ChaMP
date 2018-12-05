package com.cyclone.ipmi.codec

import akka.util.ByteString


/**
  * Providers additional codec related functionality
  */
trait CodecSupport {
  def checksum(bytes: ByteString): Byte = {
    val cs = bytes.foldLeft(0) { (acc, b) => (acc + b.toUnsignedInt) % 256 }

    (-cs.toByte).toByte
  }
}