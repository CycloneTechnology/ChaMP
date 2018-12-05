package com.cyclone.ipmi.codec

import scala.annotation.tailrec

/**
  * Encodes numbers  0 -> A, 25 -> Z, 26 -> AA and so on.
  */
object Base26NumberEncoding {
  def encode(number: Int): String = {
    @tailrec
    def loop(acc: String, i: Int): String = {
      val quot = i / 26
      val rem = i % 26

      val newAcc = ('A' + rem).toChar.toString + acc

      if (quot == 0) newAcc
      else loop(newAcc, quot - 1)
    }

    loop("", number).toString
  }
}
