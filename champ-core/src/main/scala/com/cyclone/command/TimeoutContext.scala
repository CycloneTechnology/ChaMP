package com.cyclone.command

import com.cyclone.util.OperationDeadline

import scala.concurrent.duration._

/**
  * Timeout information for an operation.
  *
  * @param deadline        the absolute deadline for the operation for which the request forms part
  * @param requestTimeouts the timeouts that can be used for retry-able internal requests
  *                        (where supported by the protocol)
  */
case class TimeoutContext(
  deadline: OperationDeadline,
  requestTimeouts: RequestTimeouts = RequestTimeouts.default) {
  def deadlineReached: Boolean = deadline.currentState().deadlineReached

  def withTimeouts(requestTimeouts: RequestTimeouts): TimeoutContext =
    copy(requestTimeouts = requestTimeouts)
}

object TimeoutContext {
  def fromDeadline(deadline: FiniteDuration): TimeoutContext =
    TimeoutContext(deadline = OperationDeadline.fromNow(deadline))

  val default: TimeoutContext = TimeoutContext(deadline = OperationDeadline.reusableTimeout(5.minutes))
}


