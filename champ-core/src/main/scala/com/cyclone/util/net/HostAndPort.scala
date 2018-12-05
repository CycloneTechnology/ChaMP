package com.cyclone.util.net

import com.google.common.net.{HostAndPort => GuavaHostAndPort}

import scala.language.implicitConversions
import com.google.common.base.Preconditions._

/**
  * Simple Scala class inspired by the Guava [[com.google.common.net.HostAndPort]]
  *
  * @param host the host
  * @param port the port if set
  */
case class HostAndPort(host: String, port: Option[Int]) {

  def hasPort: Boolean = port.isDefined

  def getPortOrDefault(defaultPort: Int): Int =
    port.getOrElse(defaultPort)


  def withDefaultPort(defaultPort: Int): HostAndPort = {
    checkArgument(isValidPort(defaultPort), "Port out of range: %s", defaultPort)
    if (port.isDefined) this else copy(port = Some(defaultPort))
  }

  /** Return true for valid port numbers. */
  private def isValidPort(port: Int) = port >= 0 && port <= 65535

  override def toString: String =
    port match {
      case Some(p) => s"$host:$p"
      case None    => host
    }
}

object HostAndPort {
  private def portIfSet(guavaHostAndPort: GuavaHostAndPort): Option[Int] =
    if (guavaHostAndPort.hasPort) Some(guavaHostAndPort.getPort) else None

  def fromHost(host: String): HostAndPort = {
    val w = GuavaHostAndPort.fromHost(host)

    HostAndPort(w.getHost, portIfSet(w))
  }

  def fromParts(host: String, port: Int): HostAndPort = {
    val w = GuavaHostAndPort.fromParts(host, port)

    HostAndPort(w.getHost, portIfSet(w))
  }

  def fromString(hostAndPortString: String): HostAndPort = {
    val w = GuavaHostAndPort.fromString(hostAndPortString)

    HostAndPort(w.getHost, portIfSet(w))
  }
}

