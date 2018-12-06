package com.cyclone.wsman.impl

import com.cyclone.wsman.impl.SeqUtils.{replace, replaceIndexedOccurrence, replaceIndexedOccurrenceFromEnd}
import org.junit.Test
import org.scalatest.junit.JUnitSuite

class SeqUtilsTest extends JUnitSuite {

  @Test
  def replace_replacesSingleOccurrence(): Unit = {
    val s = seqFor("abcdef")

    assert(replace(s, seqFor("bcd"), seqFor("x")) === seqFor("axef"))
  }

  @Test
  def replace_replacesMultipleOccurrences(): Unit = {
    val s = seqFor("abcdefbcd")

    assert(replace(s, seqFor("bcd"), seqFor("x")) === seqFor("axefx"))
  }

  @Test
  def replace_replaceWithSameNoop(): Unit = {
    val s = seqFor("abbbbc")

    assert(replace(s, seqFor("b"), seqFor("b")) === seqFor("abbbbc"))
  }

  @Test
  def replace_noopIfNoOccurrences(): Unit = {
    val s = seqFor("abcdef")

    assert(replace(s, seqFor("bcdf"), seqFor("xx")) === seqFor("abcdef"))
  }

  @Test
  def replaceIndexedOccurrence_noopIfNoOccurrences(): Unit = {
    val s = seqFor("aaa xxx aaa xxx aaa xxx")

    assert(replaceIndexedOccurrence(0, s, seqFor("bbb"), seqFor("y")) === seqFor("aaa xxx aaa xxx aaa xxx"))
  }

  @Test
  def replaceIndexedOccurrence_first_leavesOtherOccurrences(): Unit = {
    val s = seqFor("aaa xxx aaa xxx aaa xxx")

    assert(replaceIndexedOccurrence(0, s, seqFor("xxx"), seqFor("y")) === seqFor("aaa y aaa xxx aaa xxx"))
  }

  @Test
  def replaceIndexedOccurrence_second_leavesOtherOccurrences(): Unit = {
    val s = seqFor("aaa xxx aaa xxx aaa xxx")

    assert(replaceIndexedOccurrence(1, s, seqFor("xxx"), seqFor("y")) === seqFor("aaa xxx aaa y aaa xxx"))
  }

  @Test
  def replaceIndexedOccurrence_noopIfFewerOccurrences(): Unit = {
    val s = seqFor("aaa xxx aaa xxx aaa xxx")

    assert(replaceIndexedOccurrence(3, s, seqFor("xxx"), seqFor("y")) === seqFor("aaa xxx aaa xxx aaa xxx"))
  }

  @Test
  def replaceIndexedOccurrenceFromEnd_noopIfNoOccurrences(): Unit = {
    val s = seqFor("aaa xxx aaa xxx aaa xxx")

    assert(replaceIndexedOccurrenceFromEnd(0, s, seqFor("bbb"), seqFor("y")) === seqFor("aaa xxx aaa xxx aaa xxx"))
  }

  @Test
  def replaceIndexedOccurrenceFromEnd_first_leavesOtherOccurrences(): Unit = {
    val s = seqFor("aaa xxx aaa xxx aaa xxx")

    assert(replaceIndexedOccurrenceFromEnd(0, s, seqFor("xxx"), seqFor("y")) === seqFor("aaa xxx aaa xxx aaa y"))
  }

  @Test
  def replaceIndexedOccurrenceFromEnd_second_leavesOtherOccurrences(): Unit = {
    val s = seqFor("aaa xxx aaa xxx aaa xxx")

    assert(replaceIndexedOccurrenceFromEnd(1, s, seqFor("xxx"), seqFor("y")) === seqFor("aaa xxx aaa y aaa xxx"))
  }

  @Test
  def replaceIndexedOccurrenceFromEnd_noopIfFewerOccurrences(): Unit = {
    val s = seqFor("aaa xxx aaa xxx aaa xxx")

    assert(replaceIndexedOccurrenceFromEnd(3, s, seqFor("xxx"), seqFor("y")) === seqFor("aaa xxx aaa xxx aaa xxx"))
  }

  implicit private def seqFor(string: String): Vector[Char] =
    Vector(string.toCharArray(): _*)

  implicit private def stringFor(seq: Seq[Char]): String =
    new String(seq.toArray)
}
