package com.cyclone.ipmi.protocol

import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.event.LoggingReceive
import com.cyclone.ipmi.protocol.SeqNoManager._
import com.cyclone.ipmi.protocol.packet.SeqNo

import scala.collection.immutable.Queue

object SeqNoManager {
  def props(allSeqNos: Seq[SeqNo]) = Props(new SeqNoManager(allSeqNos))

  sealed trait Command

  case class AcquireSequenceNumberFor(requestHandler: ActorRef) extends Command

  case class ReleaseSequenceNumber(seqNo: SeqNo) extends Command

  case class SequenceNumberAcquired(seqNo: SeqNo)

  case class SequenceNumberNotAcquiredHandlerTerminated(requestHandler: ActorRef)

  case object SequenceNumberReleased

  case object SequenceNumberNotHeld

}

/**
  * Hands out sequence numbers for requests from a set of possible sequence numbers.
  *
  * Sequence numbers are not re-used until they are release.
  */
class SeqNoManager(allSeqNos: Seq[SeqNo]) extends Actor {
  var availableSeqNos: Vector[SeqNo] = allSeqNos.toVector

  var acquiredSeqNos = Map.empty[SeqNo, ActorRef]

  case class Waiter(client: ActorRef, requestHandler: ActorRef)

  def receive: Receive = running(Queue.empty)

  def running(waiters: Queue[Waiter]): Receive = LoggingReceive {
    case cmd: Command =>
      cmd match {
        case AcquireSequenceNumberFor(requestHandler) =>
          context watch requestHandler

          availableSeqNos.headOption match {
            case None =>
              context become running(waiters :+ Waiter(sender, requestHandler))

            case Some(seqNo) =>
              sender ! SequenceNumberAcquired(seqNo)
              doAcquire(seqNo, requestHandler)
          }

        case ReleaseSequenceNumber(seqNo) =>
          if (acquiredSeqNos.contains(seqNo)) {
            sender() ! SequenceNumberReleased

            waiters match {
              case Waiter(client, requestHandler) +: ws =>
                // To update the current recipient of the sequence number...
                doRelease(seqNo)
                doAcquire(seqNo, requestHandler)

                client ! SequenceNumberAcquired(seqNo)
                context become running(ws)

              case _ =>
                doRelease(seqNo)
            }
          }
          else
            sender() ! SequenceNumberNotHeld
      }

    case Terminated(ref) =>
      val (remainingWaiters, waitersForTerminated) = waiters.partition(w => w.requestHandler != ref)
      context become running(remainingWaiters)

      // Let them know...
      waitersForTerminated.foreach(waiter => waiter.client ! SequenceNumberNotAcquiredHandlerTerminated(ref))

      val seqNosToRelease = acquiredSeqNos.collect { case (seqNo, requestHandler) if requestHandler == ref => seqNo }

      seqNosToRelease.foreach(seqNo => self ! ReleaseSequenceNumber(seqNo))
  }

  private def doAcquire(seqNo: SeqNo, requestHandler: ActorRef): Unit = {
    availableSeqNos = availableSeqNos filterNot (_ == seqNo)
    acquiredSeqNos += (seqNo -> requestHandler)
  }

  private def doRelease(seqNo: SeqNo): Unit = {
    // In an attempt to avoid immediate re-use of sequence numbers
    // put them at the end of the sequence of available ones...
    availableSeqNos = availableSeqNos :+ seqNo
    acquiredSeqNos -= seqNo
  }
}
