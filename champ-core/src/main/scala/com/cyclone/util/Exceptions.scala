package com.cyclone.util

import com.google.common.base.Throwables

import scala.collection.JavaConverters._
import scala.util.control.NonFatal


/**
  * Exception/Throwable utilities
  *
  * @author Jeremy.Stone
  */
object Exceptions {
  def firstMessage(exception: Throwable): Option[String] =
    Throwables.getCausalChain(exception).asScala.map(_.getMessage).find(_ != null)

  def firstMessageOrClassName(exception: Throwable): String =
    firstMessage(exception).getOrElse(exception.getClass.getName)

  def firstMatching(exception: Throwable)(filter: Throwable => Boolean): Option[Throwable] =
    Throwables.getCausalChain(exception).asScala.find(filter)

  def causalChainContains(exception: Throwable)(filter: Throwable => Boolean): Boolean =
    Throwables.getCausalChain(exception).asScala.exists(filter)

  def quietly(task: => Unit): Unit = {
    try {
      task
    } catch {
      case NonFatal(_)  => // ignore
      case t: Throwable => throw t
    }
  }
}