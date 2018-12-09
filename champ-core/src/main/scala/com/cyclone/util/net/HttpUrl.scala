package com.cyclone.util.net

import java.net.{URL, UnknownHostException}

import com.google.common.net.InetAddresses.isInetAddress

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Represents an HttpUrl
  *
  * @param hostAndPort the host and port
  * @param scheme      the scheme (default http)
  * @param resource    optional resource path
  */
case class HttpUrl(
  hostAndPort: HostAndPort,
  scheme: String = "http",
  resource: Option[String] = None
) {

  /**
    * This will substitute the fully qualified host name for an IP address or host name.
    * If a domain is specified, the first host name that matches the domain is used.
    *
    * For requests involving Kerberos authentication, we need the host name in the
    * url.
    */
  def withQualifiedHostNameIfInDomain(
    domain: Option[String],
    dnsLookup: DnsLookup
  ): Future[HttpUrl] = {
    def isFqdn(host: String) = !isInetAddress(host) && host.contains(".")

    def isSameDomain(host: String): Boolean =
      domain.map(_.toUpperCase).forall { ucDomain =>
        host.toUpperCase().endsWith(s".$ucDomain")
      }

    def domainHostName(host: String): Future[Option[String]] = {
      val result = for {
        ptrs <- dnsLookup.lookupAddressAndPTRs(host)
      } yield ptrs.collectFirst { case DnsRecord.PTR(h) if isSameDomain(h) => h }

      result.recover {
        case _: UnknownHostException => None
      }
    }

    if (isFqdn(hostAndPort.host))
      Future.successful(this)
    else
      domainHostName(hostAndPort.host).map {
        case Some(host) => withHost(host)
        case None       => this
      }
  }

  def withHost(host: String): HttpUrl =
    HttpUrl(hostAndPort.copy(host = host), scheme, resource)

  def urlString: String =
    scheme + "://" + hostAndPort + "/" + resource.map(HttpUrl.trimLeadingSlash).getOrElse("")

  override def toString: String = urlString
}

object HttpUrl {

  def fromStrings(scheme: String = "http", hostAndPortString: String, resource: String): HttpUrl =
    fromParts(scheme, HostAndPort.fromString(hostAndPortString), resource)

  def fromParts(scheme: String = "http", hostAndPort: HostAndPort, resource: String): HttpUrl =
    HttpUrl(hostAndPort, scheme, optionalPathFrom(resource))

  def fromParts(hostAndPort: HostAndPort, resource: String, ssl: Boolean): HttpUrl =
    fromParts(scheme(ssl), hostAndPort, resource)

  private def optionalPathFrom(resource: String): Option[String] = {
    val trimmed = trimLeadingSlash(resource)
    if (trimmed.length() > 0) Some(trimmed) else None
  }

  private def trimLeadingSlash(resource: String): String = {
    val trimmed = resource.trim

    if (trimmed.startsWith("/")) trimmed.substring(1) else trimmed
  }

  private def scheme(ssl: Boolean): String = if (ssl) "https" else "http"

  def fromString(urlString: String): HttpUrl = {
    def fromURL(url: URL): HttpUrl = {
      def hostAndPort =
        if (url.getPort == -1)
          HostAndPort.fromHost(url.getHost)
        else
          HostAndPort.fromParts(url.getHost, url.getPort)

      HttpUrl(hostAndPort, url.getProtocol, optionalPathFrom(url.getPath))
    }

    fromURL(new URL(urlString))
  }
}
