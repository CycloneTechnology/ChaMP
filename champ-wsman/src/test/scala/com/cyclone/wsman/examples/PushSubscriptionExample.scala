package com.cyclone.wsman.examples

import java.net.InetAddress

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, KillSwitches}
import com.cyclone.util.kerberos.{KerberosArtifacts, KerberosDeployer}
import com.cyclone.util.net.{AuthenticationMethod, HostAndPort, HttpUrl, PasswordSecurityContext}
import com.cyclone.wsman.command.WSManPropertyValue
import com.cyclone.wsman.impl.subscription.SubscriptionItem
import com.cyclone.wsman.impl.subscription.push._
import com.cyclone.wsman.subscription.{PushDeliveryConfig, SubscribeByWQL}
import com.cyclone.wsman.{WSMan, WSManTarget}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * This example subscribes to the event log on a remote Windows PC.
  *
  * To run this need an application.conf in the classpath containing connection details:
  * {{{
  *wsman {
  *   # The target host.
  *   # Note that Kerberos requires fully qualified host names to authenticate.
  *   # If IPs are used we will attempt to resolve to a fully
  *   # qualified host name. To do this DNS needs to be configured - see documentation.
  *   host = someHost.someDomain
  *
  *   username = someUser
  *   password = somePassword
  * }
  * }}}
  *
  * If using Kerberos authentication, Kerberos can be configured using the following in the application.conf:
  * {{{
  * cyclone {
  *   kerberos {
  *     realm = domain.name # Or whatever is the domain name
  *     kdcHosts = [192.168.1.2] # IP of domain controller
  *     realmHosts = []
  *   }
  * }
  * }}}
  */
object PushSubscriptionExample extends App {
  self =>

  val config = ConfigFactory.load()

  val host = config.getString("wsman.host")
  val username = config.getString("wsman.username")
  val password = config.getString("wsman.password")

  implicit val actorSystem: ActorSystem = ActorSystem("exampleActorSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val port = 60001

  // If using Kerberos, this will create temporary krb5.conf and login.conf and configure system properties
  val deploymentResult = KerberosDeployer.create.deploy(KerberosArtifacts.simpleFromConfig)

  // Links the web service to the WSMan object so that subscribed events get passed to the appropriate
  // subscriber...
  val eventResource = "wsman/events"
  val pushDeliveryConfig = PushDeliveryConfig.create(eventResource)

  // Web service (with Akka Http route) to handle pushed events...
  val eventService = EventService.create(pushDeliveryConfig, deploymentResult)

  // Start a web server to handle the event handling resource...
  Http()
    .bindAndHandle(eventService.eventServiceRoute, "0.0.0.0", port)
    .onComplete {
      case Success(ok) => println(s"Test server bound to port $port")
      case Failure(e)  => e.printStackTrace()
    }

  val wsman = WSMan.create(pushDeliveryConfig)

  val futureSource =
    for {
      // Wait until Kerberos artifacts are deployed
      _ <- deploymentResult
    } yield {

      // To indicate that for this subscription we want push delivery
      // with events sent to the web service set up above...
      val deliveryHandler: PushDeliveryHandler = PushDeliveryHandler(
        HttpUrl.fromParts(
          hostAndPort = HostAndPort.fromParts(InetAddress.getLocalHost.getCanonicalHostName, port),
          resource = eventResource
        )
      )

      val source = wsman.subscribe(
        WSManTarget(
          WSMan.httpUrlFor(host, ssl = false),
          PasswordSecurityContext(username, password, AuthenticationMethod.Kerberos)
        ),
        SubscribeByWQL(
          "SELECT * FROM __InstanceCreationEvent WITHIN 1" +
          " WHERE TargetInstance ISA 'Win32_NTLogEvent'"
        ),
        deliveryHandler = deliveryHandler
      )

      source
    }

  val source = Source.fromFutureSource(futureSource)

  val killSwitch =
    source
      .viaMat(KillSwitches.single)(Keep.right)
      .to(Sink.foreach {
        case SubscriptionItem.Subscribed => println("Subscribed")
        case SubscriptionItem.Instance(instance) =>
          for {
            WSManPropertyValue.ForInstance(inst) <- instance.properties.get("TargetInstance")
            time                                 <- inst.stringProperty("TimeGenerated")
            message                              <- inst.stringProperty("Message")
          } println(s"""
            |Event occurred at $time:
            |$message
            | ---------------------------------------------
            |""".stripMargin)
      })
      .run()

  // Wait for events then shutdown...
  Thread.sleep(10000)
  killSwitch.shutdown()

  System.exit(0)
}
