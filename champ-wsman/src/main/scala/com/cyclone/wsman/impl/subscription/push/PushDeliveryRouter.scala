package com.cyclone.wsman.impl.subscription.push

import akka.NotUsed
import akka.stream._
import akka.stream.scaladsl.{BroadcastHub, Keep, MergeHub, Sink, Source}
import akka.stream.stage._
import com.cyclone.akka.MaterializerComponent
import com.cyclone.wsman.impl.WSManEnumItem
import com.cyclone.wsman.impl.subscription.WSManSubscriptionExpiryException
import com.cyclone.wsman.impl.subscription.push.PushedMessage.Item
import com.cyclone.wsman.subscription.SubscriptionId

import scala.concurrent.duration.FiniteDuration

sealed trait PushedMessage {
  def localSubscriptionId: SubscriptionId
}

object PushedMessage {

  case class Item(item: WSManEnumItem, localSubscriptionId: SubscriptionId) extends PushedMessage

  case class Heartbeat(localSubscriptionId: SubscriptionId) extends PushedMessage

}

/**
  * Routes events to subscribers according to their (local) subscription id.
  */
trait PushDeliveryRouter {

  /**
    * Gets a sink into which messages can be added.
    *
    * The sink is backed by a [[MergeHub]] with each materialization merged together
    */
  def inputSink: Sink[List[PushedMessage], NotUsed]

  /**
    * Creates a source for a subscriber
    *
    * @param id     the subscription id
    * @param expiry optional expiry whereby the subscription ends should no heartbeats occur for the subscription
    *               within this time
    */
  def newSubscriberSource(id: SubscriptionId, expiry: Option[FiniteDuration]): Source[WSManEnumItem, NotUsed]
}

trait PushDeliveryRouterComponent {
  def pushDeliveryRouter: PushDeliveryRouter
}

trait DefaultPushDeliveryRouterComponent
  extends PushDeliveryRouterComponent {
  self: MaterializerComponent
    with StateHousekeeperComponent =>

  // TODO ideally something like PartitionHub required so that each source does not have to
  // filter. However with PartitionHub seems to be no way to influence of get at the identifiers from the outside...?
  lazy val (sink, hub) =
  MergeHub.source[List[PushedMessage]](perProducerBufferSize = 1)
    .flatMapConcat(Source(_))
    .toMat(BroadcastHub.sink)(Keep.both)
    .run

  lazy val pushDeliveryRouter: PushDeliveryRouter = new PushDeliveryRouter {
    def inputSink: Sink[List[PushedMessage], NotUsed] = sink

    def newSubscriberSource(id: SubscriptionId, expiry: Option[FiniteDuration]): Source[WSManEnumItem, NotUsed] =
      hub
        .filter(_.localSubscriptionId == id)
        .via(expiryStage(expiry))
        .collect {
          case Item(item, _) => item
        }
        .alsoTo(Sink.onComplete(_ => stateHousekeeper.cleanupStateFor(id)))
  }

  private def expiryStage(expiry: Option[FiniteDuration]) = new GraphStage[FlowShape[PushedMessage, PushedMessage]] {
    val in: Inlet[PushedMessage] = Inlet("Expiry")
    val out: Outlet[PushedMessage] = Outlet("Expiry")
    override val shape: FlowShape[PushedMessage, PushedMessage] = FlowShape(in, out)

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
      new TimerGraphStageLogic(shape) {

        override def preStart(): Unit =
          startExpiryTimer()

        private def startExpiryTimer(): Unit =
          expiry.foreach(scheduleOnce(None, _))

        setHandler(in, new InHandler {
          def onPush(): Unit = {
            push(out, grab(in))

            startExpiryTimer()
          }
        })

        setHandler(out, new OutHandler {
          override def onPull(): Unit = pull(in)
        })

        override protected def onTimer(timerKey: Any): Unit =
          failStage(WSManSubscriptionExpiryException)
      }
  }
}
