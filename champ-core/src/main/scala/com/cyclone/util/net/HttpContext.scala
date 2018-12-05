package com.cyclone.util.net

import com.cyclone.util.net.HttpContext._

import scala.concurrent.duration._

object HttpContext {
  val defaultPort = 80

  /**
    * Creates an [[HttpContext]] from a host and a port
    *
    * @param hostAndPort    the host and port
    * @param secured        whether to use https
    * @param connectTimeout timeout for connecting
    * @return the HttpContext
    */
  def fromHostAndPort(
    hostAndPort: HostAndPort,
    secured: Boolean,
    connectTimeout: FiniteDuration
  ): HttpContext = {
    val defaultPort = if (secured) 443 else 80

    HttpContext(
      hostAndPort.host,
      hostAndPort.getPortOrDefault(defaultPort),
      secured,
      connectTimeout
    )
  }
}

/**
  * Context for an http request
  *
  * @param hostName       the host name or address
  * @param port           the port
  * @param secured        whether the request is secure
  * @param connectTimeout the timeout for establishing a TCP connection
  */
case class HttpContext(
  hostName: String,
  port: Int = defaultPort,
  secured: Boolean = false,
  connectTimeout: FiniteDuration = 10.seconds
)
