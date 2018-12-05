package com.cyclone.wsman

import com.cyclone.util.net.{HttpUrl, SecurityContext}


/**
  * Represents a target for a WSMan command
  */
case class WSManTarget(
  httpUrl: HttpUrl,
  securityContext: SecurityContext
)

object WSManTarget{
  val defaultSslPort = 5986
  val defaultNonSslPort = 5985
}