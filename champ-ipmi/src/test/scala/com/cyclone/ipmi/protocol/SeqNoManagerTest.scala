package com.cyclone.ipmi.protocol

import akka.actor.PoisonPill
import akka.testkit.{ImplicitSender, TestProbe}
import com.cyclone.akka.{ActorSystemShutdown, TestKitSupport}
import com.cyclone.ipmi.protocol.packet.SeqNo
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._

/**
  * Tests for [[SeqNoManager]]
  */
class SeqNoManagerTest
  extends TestKitSupport
    with WordSpecLike
    with Matchers
    with ImplicitSender
    with ActorSystemShutdown {

  class Fixture {
    val requestHandler = TestProbe().ref
    val requestHandler2 = TestProbe().ref
    val requestHandler3 = TestProbe().ref
    val requestHandler4 = TestProbe().ref

    val mgr = system.actorOf(SeqNoManager.props(SeqNo.seqNosFrom(1 to 2)))
  }

  "a SeqNoManager" when {
    "there are free sequence numbers" must {
      "hand out sequence numbers when requested" in new Fixture {
        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(1)))

        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(2)))
      }
    }

    "no sequence numbers are free" must {
      "hand out sequence number to client after one is released" in new Fixture {
        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(1)))

        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(2)))

        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler)
        expectNoMsg(300.millis)

        mgr ! SeqNoManager.ReleaseSequenceNumber(SeqNo(1))
        expectMsg(SeqNoManager.SequenceNumberReleased)

        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(1)))
      }

      "not hand out sequence number if requester terminates" in new Fixture {
        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(1)))

        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler2)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(2)))

        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler3)
        requestHandler3 ! PoisonPill
        expectMsg(SeqNoManager.SequenceNumberNotAcquiredHandlerTerminated(requestHandler3))

        mgr ! SeqNoManager.ReleaseSequenceNumber(SeqNo(1))
        expectMsg(SeqNoManager.SequenceNumberReleased)

        // the waiter should not have acquired the released number - so we should be able to get it...
        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler4)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(1)))
      }
    }

    "a sequence number has been handed out" must {
      "release it when requested" in new Fixture {
        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(1)))

        mgr ! SeqNoManager.ReleaseSequenceNumber(SeqNo(1))
        expectMsg(SeqNoManager.SequenceNumberReleased)

        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(2)))
      }

      "ignore requests to release other sequence numbers" in new Fixture {
        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(1)))

        mgr ! SeqNoManager.ReleaseSequenceNumber(SeqNo(2))
        expectMsg(SeqNoManager.SequenceNumberNotHeld)
      }

      "automatically release a sequence number immediately acquired when its requester terminates" in new Fixture {
        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(1)))

        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler2)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(2)))

        requestHandler2 ! PoisonPill

        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler4)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(2)))
      }

      "automatically release a sequence number acquired after waiting when its requester terminates" in new Fixture {
        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(1)))

        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler2)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(2)))

        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler3)

        mgr ! SeqNoManager.ReleaseSequenceNumber(SeqNo(1))
        expectMsg(SeqNoManager.SequenceNumberReleased)

        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(1)))

        requestHandler3 ! PoisonPill

        mgr ! SeqNoManager.AcquireSequenceNumberFor(requestHandler4)
        expectMsg(SeqNoManager.SequenceNumberAcquired(SeqNo(1)))
      }
    }
  }
}
