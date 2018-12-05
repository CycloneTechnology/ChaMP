package com.cyclone.util

import scala.language.implicitConversions

object CaseInsensitive {

  implicit class StringExt(private val s: String) extends AnyVal {
    def i = CaseInsensitive(s)
  }

}

/**
  * A case insensitive string see http://stackoverflow.com/a/1746014
  *
  * @author Jeremy.Stone
  */
case class CaseInsensitive(s: String) extends Proxy with Ordered[CaseInsensitive] {
  val self: String = s.toLowerCase

  def startsWith(other: CaseInsensitive): Boolean = self.startsWith(other.self)

  def trim: CaseInsensitive = CaseInsensitive(s.trim)

  def isEmpty: Boolean = self.isEmpty

  def length: Int = self.length

  def compare(other: CaseInsensitive): Int = self compareTo other.self

  override def toString: String = s
}
