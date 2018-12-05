package com.cyclone.command

import com.cyclone.command.RequestTimeouts.Exponential
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

/**
  * Test for [[RequestTimeouts]]
  */
class RequestTimeoutsTest extends WordSpec with Matchers {
  "an exponential request timeout" must {
    "generate the correct sequence of retry timeouts" in {

      val to1 = Exponential(2.seconds, timeoutFactor = 2, maxAttempts = 4)

      val (it2, Some(to2)) = to1.next
      it2 shouldBe 2.seconds
      to2 shouldBe Exponential(4.seconds, timeoutFactor = 2, maxAttempts = 3)

      val (it3, Some(to3)) = to2.next
      it3 shouldBe 4.seconds
      to3 shouldBe Exponential(8.seconds, timeoutFactor = 2, maxAttempts = 2)

      val (it4, Some(to4)) = to3.next
      it4 shouldBe 8.seconds
      to4 shouldBe Exponential(16.seconds, timeoutFactor = 2, maxAttempts = 1)

      val (it5, None) = to4.next
      it5 shouldBe 16.seconds
    }
  }
}
