package com.cyclone.ipmi.protocol

import akka.actor.PoisonPill
import akka.testkit.{ImplicitSender, TestProbe}
import com.cyclone.akka.{ActorSystemShutdown, TestKitSupport}
import com.cyclone.ipmi.protocol.packet.SeqNo
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._

/**
  * Test for [[SeqNoManagerFactory]]
  */
class SeqNoManagerFactoryTest
    extends TestKitSupport
    with WordSpecLike
    with Matchers
    with ImplicitSender
    with ActorSystemShutdown {

  class Fixture {
    val factory = system.actorOf(SeqNoManagerFactory.props(SeqNo.seqNosFrom(1 to 2)))
  }

  "a seq no manager factory" when {
    "no SeqNoManager is cached for a key (host and port)" must {
      "issue a new one" in new Fixture {
        factory ! SeqNoManagerFactory.GetSeqNoManagerFor("key1", self)
        val SeqNoManagerFactory.SeqNoManager(ref1) = expectMsgType[SeqNoManagerFactory.SeqNoManager]

        factory ! SeqNoManagerFactory.GetSeqNoManagerFor("key2", self)
        val SeqNoManagerFactory.SeqNoManager(ref2) = expectMsgType[SeqNoManagerFactory.SeqNoManager]

        ref2 shouldNot equal(ref1)
      }
    }

    "a SeqNoManager is cached for a key (host and port)" must {
      "issue it" in new Fixture {
        factory ! SeqNoManagerFactory.GetSeqNoManagerFor("key", self)
        val SeqNoManagerFactory.SeqNoManager(ref1) = expectMsgType[SeqNoManagerFactory.SeqNoManager]

        factory ! SeqNoManagerFactory.GetSeqNoManagerFor("key", self)
        val SeqNoManagerFactory.SeqNoManager(ref2) = expectMsgType[SeqNoManagerFactory.SeqNoManager]

        ref2 should equal(ref1)
      }
    }

    "only some requesters issued with a SeqNoManager terminate" must {
      "not stop the SeqNoManager and remove it from the cache" in new Fixture {
        val client1 = TestProbe()
        factory ! SeqNoManagerFactory.GetSeqNoManagerFor("key", client1.ref)
        val SeqNoManagerFactory.SeqNoManager(ref) = expectMsgType[SeqNoManagerFactory.SeqNoManager]

        val client2 = TestProbe()
        factory ! SeqNoManagerFactory.GetSeqNoManagerFor("key", client2.ref)
        expectMsgType[SeqNoManagerFactory.SeqNoManager]

        watch(ref)
        client1.ref ! PoisonPill
        expectNoMsg(300.millis)

        factory ! SeqNoManagerFactory.GetSeqNoManagerFor("key", self)
        val SeqNoManagerFactory.SeqNoManager(ref2) = expectMsgType[SeqNoManagerFactory.SeqNoManager]

        ref2 should equal(ref)
      }
    }

    "all requesters issued with a SeqNoManager terminate" must {
      "stop the SeqNoManager and remove it from the cache" in new Fixture {
        val client1 = TestProbe()
        factory ! SeqNoManagerFactory.GetSeqNoManagerFor("key", client1.ref)
        val SeqNoManagerFactory.SeqNoManager(ref) = expectMsgType[SeqNoManagerFactory.SeqNoManager]

        val client2 = TestProbe()
        factory ! SeqNoManagerFactory.GetSeqNoManagerFor("key", client2.ref)
        expectMsgType[SeqNoManagerFactory.SeqNoManager]

        watch(ref)
        client1.ref ! PoisonPill
        client2.ref ! PoisonPill
        expectTerminated(ref)

        factory ! SeqNoManagerFactory.GetSeqNoManagerFor("key", self)
        val SeqNoManagerFactory.SeqNoManager(ref2) = expectMsgType[SeqNoManagerFactory.SeqNoManager]

        ref2 shouldNot equal(ref)
      }
    }
  }

}
