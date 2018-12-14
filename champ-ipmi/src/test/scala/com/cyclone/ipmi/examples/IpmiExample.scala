package com.cyclone.ipmi.examples

import akka.actor.ActorSystem
import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.protocol.sdr.{SensorType, ThresholdComparison}
import com.cyclone.ipmi.tool.command.{SdrFilter, SensorTool}
import com.cyclone.ipmi.{Ipmi, IpmiCredentials, IpmiTarget}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * This example enumerates the readings from temperatures sensors for a specific device along with
  * the corresponding upper critical values.
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
object IpmiExample extends App {

  val config = ConfigFactory.load()

  val host = config.getString("ipmi.host")
  val username = config.getString("ipmi.username")
  val password = config.getString("ipmi.password")

  implicit val actorSystem: ActorSystem = ActorSystem("exampleActorSystem")
  implicit val timeoutContext: TimeoutContext = TimeoutContext.default

  val ipmi = Ipmi.create

  val target = IpmiTarget.LAN.forHost(host = host, credentials = IpmiCredentials(username, password))

  val futureResult = ipmi.executeToolCommand(
    target,
    SensorTool.Command(SdrFilter.BySensorType(SensorType.Temperature))
  )

  futureResult.onComplete {
    case Success(result) =>
      for {
        sensorReading <- result.readings
        analogReading <- sensorReading.analogReading
      } {
        val thresholdMessage = analogReading.thresholdComparisons
          .get(ThresholdComparison.UpperCritical)
          .map { value =>
            s" (critical is ${value.message})"
          }
          .getOrElse("")

        println(s"Reading of ${sensorReading.sensorId.id} is ${analogReading.sensorValue.message}$thresholdMessage")
      }

      System.exit(0)

    case Failure(e) =>
      e.printStackTrace()
      System.exit(1)
  }

}
