package com.cyclone.wsman.impl

import scala.annotation.tailrec

/**
  * Sequence utilities
  *
  * @author Jeremy.Stone
  */
object SeqUtils {
  def replace[T](in: Seq[T], oldValue: Seq[T], newValue: Seq[T]): Seq[T] =
    replace(0, in, oldValue, newValue)

  @tailrec
  def replace[T](from: Int, in: Seq[T], oldValue: Seq[T], newValue: Seq[T]): Seq[T] = {
    val index = in.indexOfSlice(oldValue, from)

    if (index == -1) {
      in
    } else {
      val updated = in.patch(index, newValue, oldValue.size)
      replace(index + oldValue.size, updated, oldValue, newValue)
    }
  }

  def replaceIndexedOccurrence[T](n: Int, in: Seq[T], oldValue: Seq[T], newValue: Seq[T]): Seq[T] = {
    val index = indexOfOccurrence(n, 0, in, oldValue)

    if (index == -1) {
      in
    } else {
      in.patch(index, newValue, oldValue.size)
    }
  }

  @tailrec
  private def indexOfOccurrence[T](n: Int, start: Int, in: Seq[T], value: Seq[T]): Int = {
    val index = in.indexOfSlice(value, start)

    if (n == 0) {
      index
    } else if (index == -1)
      -1
    else
      indexOfOccurrence(n - 1, index + value.size, in, value)
  }

  def replaceIndexedOccurrenceFromEnd[T](n: Int, in: Seq[T], oldValue: Seq[T], newValue: Seq[T]): Seq[T] = {
    val index = indexOfOccurrenceFromEnd(n, in.size, in, oldValue)

    if (index == -1) {
      in
    } else {
      in.patch(index, newValue, oldValue.size)
    }
  }

  @tailrec
  private def indexOfOccurrenceFromEnd[T](n: Int, start: Int, in: Seq[T], value: Seq[T]): Int = {

    val index = in.lastIndexOfSlice(value, start)

    if (n == 0) {
      index
    } else if (index == -1)
      -1
    else
      indexOfOccurrenceFromEnd(n - 1, index - 1, in, value)
  }
}
