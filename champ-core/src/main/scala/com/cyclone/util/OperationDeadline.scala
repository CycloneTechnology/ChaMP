package com.cyclone.util

import java.util.concurrent.TimeUnit

import scala.concurrent.duration._

object OperationDeadline {

  /**
    * Deadline from now based on a timeout
    */
  def fromNow(timeout: FiniteDuration): AbsoluteDeadline = AbsoluteDeadline(timeout.fromNow)

  /**
    * A deadline that can be re-used as a fixed timeout for multiple commands in an operation.
    */
  def reusableTimeout(timeout: FiniteDuration): ResettingDeadline = ResettingDeadline(timeout)

  /**
    * The status (as an immutable snapshot at a point in time) of a deadline
    */
  sealed trait DeadlineState {
    def deadlineReached: Boolean
  }

  /**
    * Trait mixed in to those deadline states where the timeout has not been reached (or there is no deadline)
    */
  trait NotReachedDeadline {
    def shortestOfTimeRemainingAnd(timeout: FiniteDuration): FiniteDuration

    val deadlineReached = false
  }

  case class TimeRemains(timeRemaining: FiniteDuration) extends DeadlineState with NotReachedDeadline {

    def shortestOfTimeRemainingAnd(timeout: FiniteDuration): FiniteDuration =
      timeout min timeRemaining
  }

  case object NoDeadline extends DeadlineState with NotReachedDeadline {
    def shortestOfTimeRemainingAnd(timeout: FiniteDuration): FiniteDuration = timeout
  }

  case object Overdue extends DeadlineState {
    val deadlineReached = true
  }

  /**
    * Similar to OperationDeadline.currentState but allows an optional deadline that evaluates to no deadline.
    */
  def currentState(
    operationDeadline: Option[OperationDeadline],
    smallestTimeout: FiniteDuration
  ): DeadlineState =
    operationDeadline.map(_.currentState(smallestTimeout)).getOrElse(NoDeadline)

  def currentState(
    operationDeadline: Option[OperationDeadline],
    timeUnit: TimeUnit
  ): DeadlineState =
    operationDeadline.map(_.currentState(timeUnit)).getOrElse(NoDeadline)
}

/**
  * A deadline for an operation which may consist of multiple commands.
  */
trait OperationDeadline {

  import OperationDeadline._

  def timeRemaining: FiniteDuration

  /**
    * Create a timeout value that ideally
    * should be reached after the deadline (provided that deadline is detected in a timely fashion).
    *
    * Used so that other APIs requiring timeouts can be used in such a way that the operation
    * deadline is reached before the API times out.
    */
  def largerTimeout(factor: Int = 2, minimumTimeout: FiniteDuration = 1.milli): FiniteDuration =
    minimumTimeout max (timeRemaining * factor)

  /**
    * Determines the current deadline state.
    *
    * @param smallestTimeout the smallest time remaining beyond which the deadline should be considered overdue.
    *                        This is typically the minimum time that the API used for the operation will allow.
    *                        E.g. a sub-millisecond nanosecond time may be truncated to zero milliseconds
    *                        and be interpreted in many APIs as an infinite wait or no timeout.
    * @return the deadline state
    */
  def currentState(smallestTimeout: FiniteDuration = 1.millisecond): DeadlineState = {
    val tr = timeRemaining

    if (tr < smallestTimeout) Overdue else TimeRemains(tr)
  }

  def currentStateJava(): DeadlineState = currentState(TimeUnit.MILLISECONDS)

  def currentState(timeUnit: TimeUnit): DeadlineState =
    currentState(Duration(1, timeUnit))
}

case class AbsoluteDeadline(deadline: Deadline) extends OperationDeadline {
  def timeRemaining: FiniteDuration = deadline.timeLeft max 0.nanos
}

case class ResettingDeadline(timeRemaining: FiniteDuration) extends OperationDeadline
