package com.cyclone.ipmi.protocol

import akka.actor.{Actor, ActorRef, Props, Terminated}
import com.cyclone.ipmi.protocol.SeqNoManagerFactory.{GetSeqNoManagerFor, Key}
import com.cyclone.ipmi.protocol.packet.SeqNo

import scala.collection.immutable.SortedSet


object SeqNoManagerFactory {
  type Key = Any

  def props(allSeqNos: Seq[SeqNo]) = Props(new SeqNoManagerFactory(allSeqNos))

  case class GetSeqNoManagerFor(key: Key, client: ActorRef)

  case class SeqNoManager(ref: ActorRef)

}

/**
  * Factory for [[SeqNoManager]] instances.
  *
  * Used for creating a separate [[SeqNoManager]] for each
  * managed device to avoid contention for seqNos while ensuring
  * that each in-progress request for a device uses a separate seqNo.
  *
  * It keeps track of termination of clients so that [[SeqNoManager]] instances can
  * be terminated.
  */
class SeqNoManagerFactory(allSeqNos: Seq[SeqNo]) extends Actor {
  var sequenceNoManagers = Map.empty[Key, (ActorRef, Set[ActorRef])]

  def receive: PartialFunction[Any, Unit] = {
    case GetSeqNoManagerFor(key, client) =>
      val seqNoManager = sequenceNoManagers.get(key) match {
        case Some((mgr, clients)) =>
          sequenceNoManagers += (key -> (mgr, clients + client))
          mgr

        case None =>
          val mgr = context.actorOf(SeqNoManager.props(allSeqNos))
          sequenceNoManagers += (key -> (mgr, Set(client)))
          mgr
      }

      context watch client

      sender ! SeqNoManagerFactory.SeqNoManager(seqNoManager)

    case Terminated(client) =>
      sequenceNoManagers = sequenceNoManagers.map {
        case (k, (mgr, clients)) => (k, (mgr, clients - client))
      }

      val keysAndMgrsWithNoClients: Map[Key, ActorRef] = sequenceNoManagers.collect {
        case (k, (mgr, clients)) if clients.isEmpty => (k, mgr)
      }

      sequenceNoManagers = sequenceNoManagers -- keysAndMgrsWithNoClients.keySet

      keysAndMgrsWithNoClients.values.foreach(context stop _)
  }

}
