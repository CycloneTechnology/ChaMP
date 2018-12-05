package com.cyclone.ipmi.examples

import akka.actor.ActorSystem
import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.sdr.SensorType
import com.cyclone.ipmi.tool.api.IpmiTool
import com.cyclone.ipmi.tool.command.{SdrFilter, SdrTool}
import com.cyclone.ipmi.{IpmiCredentials, IpmiTarget}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object IpmiToolExample extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("exampleActorSystem")
  implicit val timeoutContext: TimeoutContext = TimeoutContext.default

  val config = ConfigFactory.load()

  val host = config.getString("ipmi.host")
  val port = config.getInt("ipmi.port")
  val username = config.getString("ipmi.username")
  val password = config.getString("ipmi.password")

  val ipmiTool = IpmiTool.create

  val target = IpmiTarget.LAN.forHost(host, port, IpmiCredentials(username, password))

  val futureResult = ipmiTool.executeCommand(
    target,
    SdrTool.Command(SdrFilter.BySensorType(SensorType.Temperature))
  )

  futureResult.onComplete {
    case Success(result) =>
      println(result)
      System.exit(0)

    case Failure(e) =>
      e.printStackTrace()
      System.exit(1)
  }

}
