package com.cyclone.ipmi.examples

import akka.actor.ActorSystem
import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.command.chassis.{GetChassisStatus, GetPohCounter}
import com.cyclone.ipmi.{Ipmi, IpmiCredentials, IpmiTarget}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * This example shows how multiple tool commands and standard commands
  * can be executed within a single authenticated session
  * to get a number of different data related to chassis power.
  *
  * To run this need an application.conf in the classpath containing connection details:
  * {{{
  *ipmi {
  *   # The target host
  *   host = 192.168.1.123
  *
  *   # The credentials (defaults ADMIN/ADMIN for Supermicro)
  *   username = someUser
  *   password = somePassword
  * }
  * }}}
  */
object IpmiAdvancedExample extends App {

  val config = ConfigFactory.load()

  val host = config.getString("ipmi.host")
  val username = config.getString("ipmi.username")
  val password = config.getString("ipmi.password")

  implicit val actorSystem: ActorSystem = ActorSystem("exampleActorSystem")
  implicit val timeoutContext: TimeoutContext = TimeoutContext.default

  val ipmi = Ipmi.create

  val target = IpmiTarget.LAN.forHost(host = host, credentials = IpmiCredentials(username, password))

  // This allows running multiple against a target within an authenticated session...
  val futureResult = ipmi.withContext(target) { implicit ctx =>
    for {
      status       <- ipmi.executeCommand(GetChassisStatus.Command)
      powerOnHours <- ipmi.executeCommand(GetPohCounter.Command)
    } yield (status, powerOnHours)
  }

  futureResult.onComplete {
    case Success((status, powerOnHours)) =>
      println(s"""Power on=${status.currentPowerState.on}, fault=${status.currentPowerState.fault}.
           |Total POH=${powerOnHours.powerOnTime}
         """.stripMargin)

      System.exit(0)

    case Failure(e) =>
      e.printStackTrace()
      System.exit(1)
  }

}
