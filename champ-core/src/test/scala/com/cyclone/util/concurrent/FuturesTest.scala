package com.cyclone.util.concurrent

import java.io.IOException
import java.util.concurrent.TimeoutException

import com.cyclone.util.concurrent.Futures._
import org.jmock.{Expectations, Mockery}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, WordSpec}
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Try}

/**
  * Tests for [[Futures]]
  */
class FuturesTest extends WordSpec with Matchers with ScalaFutures with IntegrationPatience {

  val timeout = 1.seconds

  val mockery = new Mockery

  val nonRunningTask = mockery.mock(classOf[() => Future[String]])

  val task = { () =>
    Future.successful("OK")
  }

  val e = new RuntimeException("Oops!")

  val failingTask = { () =>
    Future.failed(e)
  }

  mockery.checking(new Expectations {
    never(nonRunningTask).apply()
  })

  def checkAlreadyTimedOut[A](f: Future[A]): Unit =
    f.value match {
      case Some(Failure(to: TimeoutException)) => //OK
      case Some(v: Try[A])                     => fail(s"Completed with $v")
      case _                                   => fail("Not ready")
    }

  "Futures" when {

    "sequentiallyUntilSuccess" when {
      "no tasks" must {
        "throw illegal argument exception" in {
          intercept[IllegalArgumentException] {
            sequentiallyUntilSuccess(Seq.empty)
          }
        }
      }

      "first task successful" must {
        "submit no further tasks" in {
          val f = sequentiallyUntilSuccess(Seq(task, nonRunningTask))

          f.futureValue shouldBe "OK"

          mockery.assertIsSatisfied()
        }
      }

      "first task fails" must {
        "use second result" in {
          val f = sequentiallyUntilSuccess(Seq(failingTask, task, nonRunningTask))

          f.futureValue shouldBe "OK"

          mockery.assertIsSatisfied()
        }
      }

      "no task succeeds" must {
        "hold final failure" in {
          val f = sequentiallyUntilSuccess(Seq(failingTask, failingTask))

          f.failed.futureValue shouldBe e

          mockery.assertIsSatisfied()
        }
      }
    }

    "sequentiallyUntilSuccess" must {
      "run tasks serially" in {
        val sleepTask = () =>
          Future {
            Thread.sleep(1000)
            "OK"
        }

        val f = sequentiallyUntilSuccess(Seq(sleepTask))

        f.isCompleted shouldBe false
      }
    }

    "firstSuccess" must {
      "return None if no tasks" in {
        Futures.firstSuccess(Seq.empty[Int])(i => Future.successful(i.right)).futureValue shouldBe None
      }

      "return the first result if it is good" in {
        Futures
          .firstSuccess(Seq(1, 2))(i => Future.successful((i == 1).option(i).toRightDisjunction("Error")))
          .futureValue shouldBe Some(1.right)
      }

      "return the second result if the first is not good" in {
        Futures
          .firstSuccess(Seq(1, 2, 3))(i => Future.successful((i == 2).option(i).toRightDisjunction("Error")))
          .futureValue shouldBe Some(2.right)
      }

      "return the last error if no results good" in {
        Futures
          .firstSuccess(Seq(1, 2, 3))(i => Future.successful((i > 3).option(i).toRightDisjunction("Error")))
          .futureValue shouldBe Some("Error".left)
      }
    }

    "findSerially" when {
      "first input is true" must {
        "return first input" in {
          Futures.findSerially(Seq(1, 2))(i => Future.successful(i > 0)).futureValue shouldBe Some(1)
        }

        "does not run further tasks" must {
          "return that input" in {
            val promise = Promise[Unit]

            Futures
              .findSerially(Seq(1, 2)) { i =>
                if (i == 2) promise.trySuccess(())
                Future.successful(true)
              }
              .futureValue shouldBe Some(1)

            intercept[scala.concurrent.TimeoutException] {
              Await.ready(promise.future, 300.millis)
            }
          }
        }
      }

      "subsequent input is true" must {
        "return that input" in {
          Futures.findSerially(Seq(0, 1))(i => Future.successful(i > 0)).futureValue shouldBe Some(1)
        }
      }

      "no input is true" must {
        "return None" in {
          Futures.findSerially(Seq(0, -1))(i => Future.successful(i > 0)).futureValue shouldBe None
        }
      }

      "future fails" must {
        "result is that failed future" in {
          val e = new IOException
          Futures.findSerially(Seq(0, 1))(i => Future.failed(e)).failed.futureValue shouldBe e
        }
      }
    }
  }

}
