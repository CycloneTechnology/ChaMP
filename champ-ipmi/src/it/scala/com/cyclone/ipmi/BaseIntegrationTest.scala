package com.cyclone.ipmi

import com.cyclone.akka.{ActorSystemShutdown, TestKitSupport}
import com.cyclone.command.{OperationDeadline, TimeoutContext}
import com.google.common.net.InetAddresses
import com.typesafe.config.ConfigFactory
import org.scalatest.Suite
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

import scala.concurrent.duration._

/**
  * Base for integration tests
  */
abstract class BaseIntegrationTest
  extends TestKitSupport
    with ScalaFutures
    with IntegrationPatience {
  this: Suite with ActorSystemShutdown =>

  private val config = ConfigFactory.load

  val host = InetAddresses.forString(config.getString("ipmi.host"))
  val port = config.getInt("ipmi.port")
  val credentials =
    IpmiCredentials(
      config.getString("ipmi.username"),
      config.getString("ipmi.password"))

  val vReq = IpmiVersionRequirement.V20IfSupported
  val priv = PrivilegeLevel.Administrator

  val target = IpmiTarget.LAN(host, port, credentials, priv, vReq)

  implicit val timeoutContext: TimeoutContext = TimeoutContext(OperationDeadline.fromNow(5.seconds))
}
