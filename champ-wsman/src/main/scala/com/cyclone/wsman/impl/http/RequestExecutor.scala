package com.cyclone.wsman.impl.http
import java.util.concurrent.ExecutionException

import com.cyclone.util.PasswordCredentials
import com.cyclone.util.net._
import com.cyclone.wsman.impl.http.WSManConnection.tlCredentials
import com.ning.http.client.Realm.AuthScheme
import com.ning.http.client._

import scala.concurrent.{blocking, ExecutionContext, Future}
import scala.util.control.NonFatal

/**
  * Instances of this object are able to execute a request.
  *
  * Typical implementations may ensure that the request happens in
  * a particular security context (i.e. with specified credentials and using a particular authentication scheme).
  */
private[http] trait RequestExecutor {

  def executeRequest(httpUrl: HttpUrl, requestBuilder: RequestBuilder)(
    implicit executionContext: ExecutionContext
  ): Future[Response]
}

private[http] object RequestExecutor {
  val blankCredentials: PasswordCredentials = PasswordCredentials.fromStrings("", "")

  abstract class BaseRequestExecutor(asyncHttpClient: AsyncHttpClient) extends RequestExecutor {
    protected def doExecuteRequest(
      request: Request
    )(implicit executionContext: ExecutionContext): Future[Response] = {
      val f: Future[Response] = asyncHttpClient.executeRequest(request)

      f.recoverWith {
        case NonFatal(e) => Future.failed(extractFromExecutionException(e))
      }
    }

    private def extractFromExecutionException(t: Throwable): Throwable =
      if (t.isInstanceOf[ExecutionException]) t.getCause else t

    protected def withThreadLocalCredentials[T](credentials: PasswordCredentials)(f: => T): T = {
      tlCredentials.set(credentials)
      try f
      finally tlCredentials.remove()
    }
  }

  class Unauthenticated(asyncHttpClient: AsyncHttpClient) extends BaseRequestExecutor(asyncHttpClient) {

    def executeRequest(httpUrl: HttpUrl, requestBuilder: RequestBuilder)(
      implicit executionContext: ExecutionContext
    ): Future[Response] = {
      requestBuilder.setUrl(httpUrl.urlString)
      doExecuteRequest(requestBuilder.build)
    }
  }

  class BasicAuth(asyncHttpClient: AsyncHttpClient, credentials: PasswordCredentials)
      extends BaseRequestExecutor(asyncHttpClient) {

    val realm: Realm = new Realm.RealmBuilder()
      .setUsePreemptiveAuth(true)
      .setScheme(AuthScheme.BASIC)
      .setPrincipal(credentials.username)
      .setPassword(credentials.plainPassword)
      .build()

    def executeRequest(httpUrl: HttpUrl, requestBuilder: RequestBuilder)(
      implicit executionContext: ExecutionContext
    ): Future[Response] = {
      requestBuilder.setUrl(httpUrl.urlString)
      requestBuilder.setRealm(realm)

      // See note in WSManConnection object re blank credentials...
      withThreadLocalCredentials(blankCredentials) {
        doExecuteRequest(requestBuilder.build)
      }
    }
  }

  class Kerberos(asyncHttpClient: AsyncHttpClient, dnsLookup: DnsLookup, credentials: PasswordCredentials)
      extends BaseRequestExecutor(asyncHttpClient) {

    val realm: Realm = new Realm.RealmBuilder()
      .setUsePreemptiveAuth(true)
      .setScheme(AuthScheme.SPNEGO)
      .build()

    def executeRequest(httpUrl: HttpUrl, requestBuilder: RequestBuilder)(
      implicit executionContext: ExecutionContext
    ): Future[Response] = {
      def doIt(fixedUrl: HttpUrl): Future[Response] = {
        requestBuilder.setUrl(fixedUrl.urlString)
        requestBuilder.setRealm(realm)

        withThreadLocalCredentials(credentials) {
          // SpnegoEngine may block in GSSManagerImpl with preemptive auth
          blocking {
            doExecuteRequest(requestBuilder.build)
          }
        }
      }

      // Need to lookup host name for Kerberos if we don't already have one
      for {
        urlWithHost <- httpUrl.withQualifiedHostNameIfInDomain(credentials.optDomain, dnsLookup)
        result      <- doIt(urlWithHost)
      } yield result
    }
  }

  def authenticated(
    asyncHttpClient: AsyncHttpClient,
    dnsLookup: DnsLookup,
    securityContext: SecurityContext
  ): RequestExecutor =
    securityContext match {
      case PasswordSecurityContext(creds, AuthenticationMethod.Kerberos) =>
        new Kerberos(asyncHttpClient, dnsLookup, creds)
      case PasswordSecurityContext(creds, AuthenticationMethod.Basic) => new BasicAuth(asyncHttpClient, creds)
    }
}
