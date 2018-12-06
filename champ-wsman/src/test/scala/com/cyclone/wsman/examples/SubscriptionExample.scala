package com.cyclone.wsman.examples
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, KillSwitches}
import com.cyclone.util.kerberos.{KerberosArtifacts, KerberosDeployer}
import com.cyclone.util.net.{AuthenticationMethod, PasswordSecurityContext}
import com.cyclone.wsman.command.WSManPropertyValue
import com.cyclone.wsman.impl.subscription.SubscriptionItem
import com.cyclone.wsman.subscription.SubscribeByWQL
import com.cyclone.wsman.{WSMan, WSManTarget}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global

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
object SubscriptionExample extends App {
  val config = ConfigFactory.load()

  val host = config.getString("wsman.host")
  val username = config.getString("wsman.username")
  val password = config.getString("wsman.password")

  implicit val actorSystem: ActorSystem = ActorSystem("exampleActorSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val wsman = WSMan.create

  val futureSource =
    for {
      // If using Kerberos, this will create temporary krb5.conf and login.conf and configure system properties
      _ <- KerberosDeployer.create.deploy(KerberosArtifacts.simpleFromConfig)

    } yield {
      val source = wsman.subscribe(
        WSManTarget(
          WSMan.httpUrlFor(host, ssl = false),
          PasswordSecurityContext(username, password, AuthenticationMethod.Kerberos)
        ),
        SubscribeByWQL(
          "SELECT * FROM __InstanceCreationEvent WITHIN 1" +
          " WHERE TargetInstance ISA 'Win32_NTLogEvent'"
        )
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
