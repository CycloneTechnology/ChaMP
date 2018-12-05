package com.cyclone.akka

import akka.actor.Status.Failure
import akka.testkit.{ImplicitSender, TestProbe}
import com.cyclone.akka.FirstResponseSender.Requests
import org.scalatest.WordSpecLike
import scala.concurrent.duration._

class FirstResponseSenderTest
  extends TestKitSupport
    with WordSpecLike
    with ImplicitSender
    with ActorSystemShutdown {

  val actor1 = TestProbe()
  val actor2 = TestProbe()

  case object Request1

  case object Request2

  case object Response

  "FirstReponseSender" when {
    "a response comes from one actor" must {
      val firstResponseSender = actorSystem.actorOf(FirstResponseSender.props[Response.type](20.seconds))
      watch(firstResponseSender)

      "send to all actors" in {
        firstResponseSender ! Requests(Seq((actor1.ref, Request1), (actor2.ref, Request2)))

        actor1.expectMsg(Request1)
        actor2.expectMsg(Request2)
      }

      "respond with the first response" in {
        actor2.reply(Response)
        expectMsg(Response)
      }

      "then shutdown" in {
        expectTerminated(firstResponseSender)
      }
    }

    "no response comes from any actor" must {
      val firstResponseSender = actorSystem.actorOf(FirstResponseSender.props[Response.type](500.millis))
      watch(firstResponseSender)

      "shutdown after timeout" in {
        firstResponseSender ! Requests(Seq((actor1.ref, Request1)))
        expectTerminated(firstResponseSender)
      }
    }

    "requests are empty" must {
      val firstResponseSender = actorSystem.actorOf(FirstResponseSender.props[Response.type](20.seconds))
      watch(firstResponseSender)

      "immediately fail" in {
        firstResponseSender ! Requests(Nil)
        expectMsgType[Failure]
      }

      "then shutdown" in {
        expectTerminated(firstResponseSender)
      }
    }

  }
}