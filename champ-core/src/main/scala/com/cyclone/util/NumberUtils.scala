package com.cyclone.util

import com.google.common.primitives.Ints

/**
  * Number related utilities
  */
object NumberUtils {

  /**
    * Determines whether a string can be parsed to an integer
    */
  def isInteger(value: String): Boolean = parseInt(value).nonEmpty

  /**
    * Parses a string as an integer if possible.
    */
  //noinspection IfElseToOption
  // ^^^ Because get NPE for Integer2int implicit conversion cannot use Option(...)
  def parseInt(value: String): Option[Int] = {
    if (value == null)
      None
    else {
      val parse: Integer = Ints.tryParse(value)

      if (parse != null) Some(parse) else None
    }
  }
}
