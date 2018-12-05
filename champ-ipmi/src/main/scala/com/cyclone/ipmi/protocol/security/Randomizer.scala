package com.cyclone.ipmi.protocol.security

import java.security.SecureRandom

import akka.util.ByteString

/**
  * Generates random bytes
  */
object Randomizer {
  private val random = new SecureRandom()

  /**
    * Generates a string of random bytes of a required length
    */
  def randomBytes(length: Int): ByteString = {
    val bytes = Array.ofDim[Byte](length)
    random.nextBytes(bytes)

    ByteString(bytes)
  }
}
