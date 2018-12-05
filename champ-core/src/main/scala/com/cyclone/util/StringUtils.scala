package com.cyclone.util

import scalaz.Scalaz._

/**
  * String utilities
  */
object StringUtils {

  /**
    * Determines whether the string has any non-whitespace characters
    */
  def hasText(string: String): Boolean =
    if (string == null)
      false
    else
      string.exists(c => !c.isWhitespace)

  /**
    * @return an option containing string if it has non-whitespace characters else None
    */
  def toOption(string: String): Option[String] =
    hasText(string).option(string)

  /**
    * Gets a line and column position in a string corresponding to an index.
    */
  def lineAndColumn(string: String, index: Int): (Int, Int) = {
    var line = 1
    var col = 1
    var i = 0
    while (i < index) {
      if (string(i) == '\n') {
        col = 1
        line += 1
      } else {
        col += 1
      }
      i += 1
    }

    (line, col)
  }
}
