package com.cyclone.util.spnego

import java.io.IOException
import java.nio.charset.StandardCharsets.UTF_8
import java.security.{PrivilegedAction, PrivilegedActionException, PrivilegedExceptionAction}
import javax.security.auth.Subject
import javax.security.auth.kerberos.KerberosPrincipal
import javax.security.auth.login.LoginContext

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.{HttpChallenge, RawHeader}
import akka.http.scaladsl.server.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import akka.http.scaladsl.server.directives.BasicDirectives.{extract, extractExecutionContext, extractLog, provide}
import akka.http.scaladsl.server.directives.FutureDirectives.onSuccess
import akka.http.scaladsl.server.directives.RouteDirectives.reject
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Directive1, Rejection, RequestContext}
import akka.util.Timeout
import com.cyclone.util.ConfigUtils._
import com.cyclone.util.kerberos.{ArtifactDeploymentInfo, KerberosDeployment}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.codec.binary.Base64
import org.ietf.jgss.{GSSCredential, GSSManager}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/**
  * Based on https://github.com/tresata/akka-http-spnego.
  *
  * With the following changes:
  * - removed cookie support
  * - allow Kerberos as well as Negotiate authorization header
  * - do not dispose of the gcc context to allow later decrypts 
  */
object SpnegoAuthenticator {
  private val Authorization = "authorization"
  private val negotiate = "Negotiate"
  private val kerberos = "Kerberos"
  private val wwwAuthenticate = "WWW-Authenticate"

  private[spnego] def challengeHeader(maybeServerToken: Option[Array[Byte]] = None): HttpHeader = RawHeader(
    wwwAuthenticate,
    negotiate + maybeServerToken.map(" " + new Base64(0).encodeToString(_)).getOrElse("")
  )

  def apply(
    config: Config = ConfigFactory.load(),
    artifactDeploymentInfo: ArtifactDeploymentInfo
  )(implicit ec: ExecutionContext, log: LoggingAdapter): SpnegoAuthenticator = {
    val principal = artifactDeploymentInfo.servicePrincipalName
    val keytab = artifactDeploymentInfo.keyTabPath.toFile.getAbsolutePath
    val debug = config.getBoolean("cyclone.spnego.kerberos.debug")
    val tokenValidity = config.getDuration("cyclone.spnego.token.validity")
    val signatureSecret = config.getString("cyclone.spnego.signature.secret")

    log.info("principal {}", principal)
    log.info("debug {}", debug)
    log.info("token validity {}", tokenValidity)

    val tokens = new Tokens(tokenValidity.toMillis, signatureSecret.getBytes(UTF_8))
    new SpnegoAuthenticator(principal, keytab, debug, tokens)
  }

  def spnegoAuthenticate(
    config: Config = ConfigFactory.load(),
    kerberosDeployment: KerberosDeployment): Directive1[Token] = {
    extractExecutionContext.flatMap { implicit ec =>
      extractLog.flatMap { implicit log =>
        extract { ctx =>
          log.debug("creating spnego authenticator")

          implicit val timeout: Timeout = Timeout(config.finiteDuration("cyclone.spnego.deploymentInfo.timeout"))

          for {
            deploymentResult <- kerberosDeployment.latestArtifactDeploymentInfo
            spnego = SpnegoAuthenticator(config, deploymentResult)
            applied <- Future(spnego.apply(ctx))
          } yield applied
        }.flatMap(onSuccess(_)).flatMap {
          case Left(rejection) => reject(rejection)
          case Right(token)    => provide(token)
        }
      }
    }
  }
}

class SpnegoAuthenticator(principal: String, keytab: String, debug: Boolean, tokens: Tokens)(implicit log: LoggingAdapter) {

  import SpnegoAuthenticator._

  private val subject = new Subject(false, Set(new KerberosPrincipal(principal)).asJava, Set.empty[AnyRef].asJava, Set.empty[AnyRef].asJava)
  private val kerberosConfiguration = KerberosConfiguration(keytab, principal, debug)

  private val loginContext = new LoginContext("", subject, null, kerberosConfiguration)
  loginContext.login()

  private val gssManager = Subject.doAs(loginContext.getSubject, new PrivilegedAction[GSSManager] {
    override def run: GSSManager = GSSManager.getInstance
  })

  private def clientToken(ctx: RequestContext): Option[Array[Byte]] =
    ctx.request.headers
      .collectFirst {
        case HttpHeader(Authorization, value) => value
      }
      .filter(value => value.startsWith(negotiate) || value.startsWith(kerberos))
      .map { authHeader =>
        log.debug("authorization header found")
        new Base64(0).decode(authHeader.substring(negotiate.length).trim)
      }

  private def kerberosCore(clientToken: Array[Byte]): Either[Rejection, Token] = {
    try {
      val (maybeServerToken, maybeToken) =
        Subject.doAs(loginContext.getSubject,
          new PrivilegedExceptionAction[(Option[Array[Byte]], Option[Token])] {
            override def run: (Option[Array[Byte]], Option[Token]) = {
              val gssContext = gssManager.createContext(null: GSSCredential)
              try {
                val maybeServerToken = Option(gssContext.acceptSecContext(clientToken, 0, clientToken.length))

                val maybeToken =
                  if (gssContext.isEstablished)
                    Some(tokens.create(gssContext.getSrcName.toString, maybeServerToken, gssContext))
                  else
                    None

                (maybeServerToken, maybeToken)
              }
              catch {
                case NonFatal(e) =>
                  log.error(e, "error in establishing security context")
                  throw e
              }
            }
          })

      if (log.isDebugEnabled)
        log.debug("maybeServerToken {} maybeToken {}",
          maybeServerToken.map(new Base64(0).encodeToString(_)), maybeToken)

      maybeToken
        .map { token =>
          log.debug("received new token")
          Right(token)
        }
        .getOrElse {
          log.debug("no token received but if there is a serverToken then negotiations are ongoing")
          Left(AuthenticationFailedRejection(
            CredentialsMissing,
            HttpChallenge(challengeHeader(maybeServerToken).value, None)))
        }
    }
    catch {
      case e: PrivilegedActionException =>
        e.getException match {
          case e: IOException => throw e // server error
          case NonFatal(t)    =>
            log.error(t, "negotiation failed")
            Left(AuthenticationFailedRejection(
              CredentialsRejected,
              HttpChallenge(challengeHeader().value, None))) // rejected
        }
    }
  }

  private def kerberosNegotiate(ctx: RequestContext): Option[Either[Rejection, Token]] =
    clientToken(ctx).map(kerberosCore)

  private def initiateNegotiations: Either[Rejection, Token] = {
    log.debug("no negotiation header found, initiating negotiations")
    Left(AuthenticationFailedRejection(
      CredentialsMissing,
      HttpChallenge(challengeHeader().value, None)))
  }

  def apply(ctx: RequestContext): Either[Rejection, Token] =
    kerberosNegotiate(ctx).getOrElse(initiateNegotiations)
}

