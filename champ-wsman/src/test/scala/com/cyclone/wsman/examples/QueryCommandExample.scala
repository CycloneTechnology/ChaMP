package com.cyclone.wsman.examples

import akka.actor.ActorSystem
import com.cyclone.command.TimeoutContext
import com.cyclone.util.kerberos.{KerberosArtifacts, KerberosDeployer}
import com.cyclone.util.net.{AuthenticationMethod, PasswordSecurityContext}
import com.cyclone.wsman.command.{EnumerateByWQL, WSManInstancesResult}
import com.cyclone.wsman.{WSMan, WSManTarget}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object QueryCommandExample extends App {

  implicit val actorSystem: ActorSystem = ActorSystem("exampleActorSystem")
  implicit val timeoutContext: TimeoutContext = TimeoutContext.default

  val config = ConfigFactory.load()

  // For Kerberos, need to either specify the fully qualified domain name or specify the
  // domain as part of fully qualified user name (in which case it tries to use DNS to
  // convert a host or IP to a fully qualified domain name with that domain under the covers)
  val host = config.getString("wsman.host")
  val username = config.getString("wsman.username") + "@" + config.getString("wsman.domain")
  val password = config.getString("wsman.password")

  val wsman = WSMan.create

  val futureResult: Future[WSManInstancesResult] =
    for {
      // If using Kerberos, this will create temporary krb5.conf and login.conf and configure system properties
      _ <- KerberosDeployer.create.deploy(KerberosArtifacts.simpleFromConfig)

      commandResult <- wsman.executeCommand(
        WSManTarget(WSMan.httpUrlFor(host, ssl = false),
          PasswordSecurityContext(username, password, AuthenticationMethod.Kerberos)),
        EnumerateByWQL("select * from Win32_Service"))
    } yield commandResult

  futureResult.onComplete {
    case Success(result) =>
      result.instances.foreach { instance =>
        for {
          caption <- instance.stringProperty("Caption")
          startMode <- instance.stringProperty("StartMode")
          state <- instance.stringProperty("State")
        } println(s"Service '$caption' with start mode $startMode state is $state")
      }
      System.exit(0)

    case Failure(e) =>
      e.printStackTrace()
      System.exit(1)
  }
}
