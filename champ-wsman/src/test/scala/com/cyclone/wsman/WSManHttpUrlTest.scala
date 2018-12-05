package com.cyclone.wsman

import com.cyclone.util.PasswordCredentials
import com.cyclone.util.net.{AuthenticationMethod, HostAndPort, HttpUrl, PasswordSecurityContext}
import org.scalatest.{FunSuite, Matchers}

class WSManHttpUrlTest extends FunSuite with Matchers {

  val securityContext =
    PasswordSecurityContext(PasswordCredentials.fromStrings("user", "password"), AuthenticationMethod.Basic)

  test("connectUrlFor does not override specified port") {
    assert(
      WSMan.httpUrlFor(HostAndPort.fromString("10.0.0.4:123"), ssl = false) ===
        HttpUrl.fromString("http://10.0.0.4:123/wsman")
    )
  }

  test("connectUrlFor applies default wsman http port") {
    assert(
      WSMan.httpUrlFor(HostAndPort.fromString("10.0.0.4"), ssl = false) ===
        HttpUrl.fromString("http://10.0.0.4:5985/wsman")
    )
  }

  test("connectUrlFor applies default wsman https port") {
    assert(
      WSMan.httpUrlFor(HostAndPort.fromString("10.0.0.4"), ssl = true) ===
        HttpUrl.fromString("https://10.0.0.4:5986/wsman")
    )
  }
}
