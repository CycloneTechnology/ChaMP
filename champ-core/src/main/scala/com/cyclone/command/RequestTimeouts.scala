package com.cyclone.command

import scala.concurrent.duration._

/**
  * Determines a sequence of timeouts to be used for successive timeout
  * (or other retry-able error code) retries.
  */
trait RequestTimeouts {
  def next: (FiniteDuration, Option[RequestTimeouts])
}

object RequestTimeouts {

  val default: RequestTimeouts =
    Exponential(initialTimeout = 2.seconds, timeoutFactor = 2, maxAttempts = 4)

  def simple(timeout: FiniteDuration = 2.seconds, maxAttempts: Int = 4): RequestTimeouts =
    Exponential(initialTimeout = timeout, timeoutFactor = 1, maxAttempts = maxAttempts)

  case class Exponential(initialTimeout: FiniteDuration, timeoutFactor: Int, maxAttempts: Int) extends RequestTimeouts {
    require(maxAttempts > 0)

    def next: (FiniteDuration, Option[Exponential]) = (
      initialTimeout,
      if (maxAttempts > 1)
        Some(Exponential(initialTimeout * timeoutFactor, timeoutFactor, maxAttempts - 1))
      else
        None
    )

    override def toString =
      s"Exponential(initialTimeout=$initialTimeout, timeoutFactor=$timeoutFactor, maxAttempts=$maxAttempts)"
  }

}
