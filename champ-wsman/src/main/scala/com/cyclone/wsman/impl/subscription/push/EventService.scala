package com.cyclone.wsman.impl.subscription.push

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.cyclone.akka.MaterializerComponent
import com.cyclone.util.kerberos.KerberosDeploymentComponent
import com.cyclone.util.spnego.{SpnegoDirectives, Token}
import com.cyclone.wsman.subscription.SubscriptionId
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Route that handles POSTed WSMan push events.
  */
trait EventService extends Directives with SpnegoDirectives with LazyLogging {
  self: PushDeliveryRouterComponent
    with MaterializerComponent
    with PushEventXmlParserComponent
    with KerberosTokenCacheComponent
    with KerberosDeploymentComponent =>

  // For Kerberos/Microsoft operation. See...
  // https://msdn.microsoft.com/en-us/library/aa380496(VS.85).aspx

  private val postEventKerberos = (post & parameter('pushId)) { id =>
    val localSubscriptionId = SubscriptionId(id)

    kerberosTokenCache.getTokenFor(localSubscriptionId) match {
      case None =>
        spnegoAuthenticate() { token =>
          respondWithHeaders(token.challengeHeader) {
            logger.debug("Authenticated")

            kerberosTokenCache.putTokenFor(localSubscriptionId, token)
            handle(token, localSubscriptionId)
          }
        }

      case Some(token) =>
        logger.debug("Token found in cache")
        handle(token, localSubscriptionId)
    }

  }

  def handle(token: Token, localSubscriptionId: SubscriptionId): Route =
    (entity(as[ByteString]) & extractRequest) { (body, req) =>
      // Ignore initial empty connection request (e.g. from Microsoft WinRM client)...
      if (body.nonEmpty) {
        val optXmlRequestData =
          RequestConverterComponent.toXmlConverter(token).lift(RequestData(req, Nil, body))

        optXmlRequestData match {
          case Some(RequestData(_, _, document)) =>
            val messages = pushEventXmlParser.messagesFor(document, localSubscriptionId)

            if (messages.isEmpty)
              complete(StatusCodes.NoContent)
            else
              complete(forwardToSubscribers(messages, localSubscriptionId))
          case _ =>
            logger.warn(s"Unsupported request $optXmlRequestData")
            complete(StatusCodes.BadRequest)
        }
      } else {
        logger.debug("Received empty request")
        complete(StatusCodes.OK)
      }
    }

  private def forwardToSubscribers(
    messages: List[PushedMessage],
    localSubscriptionId: SubscriptionId
  ): Future[StatusCode] = {

    Source
      .single(messages)
      .watchTermination() {
        case (_, done) => done
      }
      .to(pushDeliveryRouter.inputSink)
      .run()
      .map(_ => StatusCodes.NoContent)
  }

  val eventServiceRoute: Route = pathPrefix("wsman" / "event_receiver" / "receive") {
    postEventKerberos
  }

}
