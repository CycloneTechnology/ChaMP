package com.cyclone.akka

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout, Status}
import com.cyclone.akka.FirstResponseSender.Requests

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.reflect.ClassTag

/**
  * General purpose single-use actor that sends multiple messages corresponding to a
  * request. It responds to the original sender with the first response it receives.
  */
object FirstResponseSender {
  /**
    * @param timeout timeout for receiving a response from the time that the request is received.
    *                If the timeout if infinite (i.e. no timeout) then the actor should be
    *                stopped externally if no response is received.
    */
  def props[Resp: ClassTag](timeout: Duration): Props = Props(new FirstResponseSender[Resp](timeout))

  type Request = (ActorRef, Any)

  case class Requests(requests: Seq[Request])

}

class FirstResponseSender[Resp: ClassTag](timeout: Duration)
  extends Actor {

  def receive: Receive = {
    case Requests(reqs) =>
      val client = sender()

      if (reqs.isEmpty) {
        client ! Status.Failure(new RuntimeException(s"Empty request"))
        context stop self
      }
      else {
        timeout match {
          case finite: FiniteDuration => context.setReceiveTimeout(finite)
          case _                      => // No timeout
        }

        reqs.foreach {
          case (actor, msg) => actor ! msg
        }
      }

      context become {
        case response: Resp =>
          client ! response
          context stop self

        case ReceiveTimeout =>
          context stop self
      }
  }

}


