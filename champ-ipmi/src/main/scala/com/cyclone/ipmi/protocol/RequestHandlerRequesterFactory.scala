package com.cyclone.ipmi.protocol

import akka.actor.{ActorContext, ActorRef}
import akka.pattern.{AskTimeoutException, ask}
import akka.util.Timeout
import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.command.global.DeviceAddress
import com.cyclone.ipmi.protocol.packet._
import com.cyclone.ipmi.{DeadlineReached, IpmiError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._

trait RequestHandlerRequesterFactoryComponent extends RequesterFactoryComponent {
  lazy val requesterFactory: RequesterFactory = RequestHandlerRequesterFactory
}

/**
  * Requester that uses a separate [[RequestHandler]] for each request.
  */
object RequestHandlerRequesterFactory extends RequesterFactory {
  def requester(
    actorContext: ActorContext,
    hub: ActorRef,
    seqNoManager: ActorRef): Requester = new Requester {

    def makeRequest[Cmd <: IpmiCommand, Res <: IpmiCommandResult](
      command: Cmd,
      version: IpmiVersion,
      sessionContext: SessionContext,
      targetAddress: DeviceAddress)
      (implicit timeoutContext: TimeoutContext, codec: CommandResultCodec[Cmd, Res]): Future[IpmiError \/ Res] = {

      val requestHandler =
        actorContext.actorOf(
          RequestHandler.props(hub = hub, version, sessionContext, timeoutContext))

      def makeRequest(seqNo: SeqNo) = {
        // Double the timeout for ask so that it doesn't time out before the deadline...
        implicit val timeout: Timeout = timeoutContext.deadline.largerTimeout()

        (requestHandler ? RequestHandler.SendRequest(seqNo, command, targetAddress))
          .mapTo[RequestHandler.RequestResult]
          .map {
            case RequestHandler.RequestResult(\/-(res)) => res.asInstanceOf[Res].right
            case RequestHandler.RequestResult(-\/(e))   => e.left
          }
      }

      def acquireSeqNo: Future[IpmiErrorOr[SeqNo]] = {
        implicit val timeout: Timeout = timeoutContext.deadline.largerTimeout()

        (seqNoManager ? SeqNoManager.AcquireSequenceNumberFor(requestHandler))
          .mapTo[SeqNoManager.SequenceNumberAcquired]
          .map(_.seqNo.right[IpmiError])
          .recover {
            case _: AskTimeoutException => DeadlineReached.left
          }
      }

      val result = for {
        seqNo <- eitherT(acquireSeqNo)
        requestResult <- eitherT(makeRequest(seqNo))
      } yield requestResult

      result.run
    }
  }
}
