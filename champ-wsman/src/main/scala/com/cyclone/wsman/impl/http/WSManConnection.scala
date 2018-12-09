package com.cyclone.wsman.impl.http

import java.io.{IOException, StringWriter}
import java.net.ConnectException
import java.security.Security
import java.util.concurrent.{ExecutionException, TimeoutException}

import com.cyclone.util.Exceptions._
import com.cyclone.util.XmlUtils.prettyPrint
import com.cyclone.util.net._
import com.cyclone.util.{FQNFormat, PasswordCredentials}
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman._
import com.cyclone.wsman.impl.WSManAvailability
import com.cyclone.wsman.impl.http.settings.HttpSettings
import com.cyclone.wsman.impl.xml.RequestXML
import com.google.common.base.Charsets
import com.ning.http.client._
import com.typesafe.scalalogging.LazyLogging
import javax.security.auth.callback.{Callback, CallbackHandler, NameCallback, PasswordCallback}
import javax.security.auth.login.LoginException
import org.ietf.jgss.GSSException
import scalaz.Scalaz._

import scala.collection.JavaConverters._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML}

object WSManConnection {

  // NOTE: AsyncHttpClient honours Kerberos/Negotiate authenticate headers in the response
  // *regardless* of whether we specify kerberos in the realm for the request. Hence it will go
  // on to try to prompt us for credentials unless we always wire up the callback handler
  // and just set blank credentials when not using Kerberos...
  Security.setProperty(
    "auth.login.defaultCallbackHandler",
    classOf[ThreadLocalCredentialsCallbackHandler].getName
  )

  val tlCredentials: ThreadLocal[PasswordCredentials] = new ThreadLocal()

  class ThreadLocalCredentialsCallbackHandler extends CallbackHandler with LazyLogging {

    override def handle(callbacks: Array[Callback]): Unit = {
      val credentials = tlCredentials.get

      if (credentials != null) {
        for (callback <- callbacks) {
          callback match {
            case nameCallback: NameCallback =>
              nameCallback.setName(credentials.fullyQualifiedUsername(FQNFormat.UPNUpper))

            case passwordCallback: PasswordCallback =>
              passwordCallback.setPassword(credentials.passwordChars)

            case _ =>
          }
        }
      } else logger.debug("No credentials set - ignoring callback")
    }
  }

}

/**
  * Internal internal interface for sending requests
  */
trait WSManConnection {

  def executeSoapRequest(requestXML: RequestXML)(
    implicit executionContext: ExecutionContext
  ): Future[WSManErrorOr[Elem]]

  def determineAvailability(timeout: FiniteDuration)(
    implicit executionContext: ExecutionContext
  ): Future[WSManAvailability]
}

// TODO re-implement using akka http client
class DefaultWSManConnection(
  httpUrl: HttpUrl,
  securityContext: SecurityContext,
  asyncHttpClient: AsyncHttpClient,
  dnsLookup: DnsLookup,
  httpSettings: HttpSettings
) extends WSManConnection
    with LazyLogging {

  def executeSoapRequest(
    requestXML: RequestXML
  )(implicit executionContext: ExecutionContext): Future[WSManErrorOr[Elem]] = {

    executeSoapRequest(requestXML, RequestExecutor.authenticated(asyncHttpClient, dnsLookup, securityContext))
      .flatMap { response =>
        val statusCode = response.getStatusCode
        statusCode match {
          case 200 =>
            val xml = extractResponseXML(response)

            if ((xml \\ "Envelope" \\ "Body" \\ "Fault").headOption.isEmpty)
              xml.right.point[Future]
            else
              WSManQueryError(xml).left.point[Future]

          case 401 =>
            WSManAuthenticationError(Some("401 Unauthorized"), None).left.point[Future]

          case _ => queryError(response).left.point[Future]
        }
      }
      .recoverWith {
        case e: Exception =>
          wrapExceptionIfPossible(e) match {
            case Some(err) => err.left.point[Future]
            case None      => Future.failed(e)
          }
      }
  }

  def determineAvailability(
    timeout: FiniteDuration
  )(implicit executionContext: ExecutionContext): Future[WSManAvailability] = {
    val requestBuilder = new RequestBuilder()
      .setMethod("POST")
      .addHeader("Content-Length", "0")
      .addHeader("Content-Type", "application/soap+xml;charset=UTF-8")
      .setRequestTimeout(timeout.toMillis.toInt)

    lazy val challengeHeaders = securityContext match {
      case PasswordSecurityContext(_, method) => method.challengeHeaders
    }

    def authSchemeMatchesChallenge(response: Response): Boolean = {
      response.getHeaders("WWW-Authenticate").asScala.exists { header =>
        challengeHeaders.exists(header.startsWith)
      }
    }

    new RequestExecutor.Unauthenticated(asyncHttpClient)
      .executeRequest(httpUrl, requestBuilder)
      .map { response =>
        response.getStatusCode match {
          // Unless the url is not found there may be support...
          case 404                                          => WSManAvailability.PathNotFound
          case 401 if authSchemeMatchesChallenge(response)  => WSManAvailability.BadCredentials
          case 401 if !authSchemeMatchesChallenge(response) => WSManAvailability.NoAuthWrongScheme
          case statusCode =>
            WSManAvailability.OtherStatusCode(statusCode, possibilyAvailable = true)
        }
      }
      .recover {
        // Unless we cannot connect there may be support...
        case _: ConnectException => WSManAvailability.NotListening
        case _: TimeoutException => WSManAvailability.Timeout
        case e                   => WSManAvailability.OtherException(e, possibilyAvailable = true)
      }
  }

  private def executeSoapRequest(requestXML: RequestXML, requestExecutor: RequestExecutor)(
    implicit executionContext: ExecutionContext
  ): Future[Response] = {
    def xmlToString(xml: Elem) = {
      val sw = new StringWriter

      // Trim whitespace in elements to avoid complaints of 'invalid SOAP headers'
      // in case someone has reformatted the XML
      XML.write(sw, scala.xml.Utility.trim(xml), "UTF-8", xmlDecl = false, null)

      sw.toString
    }

    val xml = requestXML.xml(httpUrl.urlString)
    dumpXML("Sending request", xml)

    val requestBuilder = new RequestBuilder()
      .setMethod("POST")
      .setBody(xmlToString(xml))
      .setBodyEncoding(Charsets.UTF_8.name())
      .addHeader("Content-Type", "application/soap+xml;charset=UTF-8")
      .setRequestTimeout(requestTimeoutMillis(requestXML))

    requestExecutor.executeRequest(httpUrl, requestBuilder)
  }

  private def extractResponseXML(response: Response): Elem = {
    val xml = XML.load(response.getResponseBodyAsStream)

    dumpXML("Response received", xml)

    xml
  }

  private def queryError(response: Response): WSManError = {
    // NP-2414 - can get UnsupportedCharsetException...
    val responseBody = Try(response.getResponseBody()) match {
      case Success(body) => body
      case Failure(e)    => s"Unable to get response body: ${e.getMessage}"
    }

    // Response will typically be XML but allow for case when it is not...
    try {
      val xml = XML.load(scala.xml.Source.fromString(responseBody))

      dumpXML("Error response received", xml)

      WSManQueryError(xml)
    } catch {
      case e: Exception =>
        logger.debug(s"Non-XML error response received:\n$response", e)

        WSManRequestError(responseBody)
    }
  }

  private def wrapExceptionIfPossible(exception: Throwable): Option[WSManError] = {
    if (isAuthException(exception))
      Some(WSManAuthenticationError(firstMessage(exception), Some(exception)))
    else
      exception match {
        case e: ExecutionException => wrapExceptionIfPossible(e.getCause)
        case e: IOException        => Some(wrapIOException(e))
        case _: TimeoutException   => Some(RequestTimeout)
        case _                     => None
      }
  }

  private def wrapIOException(exception: IOException): WSManError =
    if (isAuthException(exception))
      WSManAuthenticationError(firstMessage(exception), Some(exception))
    else
      WSManIOError(firstMessage(exception), Some(exception))

  private def isAuthException(exception: Throwable) =
    causalChainContains(exception) { e =>
      e.isInstanceOf[LoginException] || e.isInstanceOf[GSSException]
    }

  private def requestTimeoutMillis(requestXML: RequestXML) = {
    val duration = requestXML.optionalTimeRemaining
      .getOrElse(httpSettings.defaultRequestTimeout) max httpSettings.minimumRequestTimeout

    duration.toMillis.toInt
  }

  private def dumpXML(msg: String, xml: Elem): Unit =
    logger.debug(s"$msg:\n${prettyPrint(xml)}")
}
