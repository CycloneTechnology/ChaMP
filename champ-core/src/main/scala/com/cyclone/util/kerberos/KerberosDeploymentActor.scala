package com.cyclone.util.kerberos

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorLogging, ActorRef, Props, Scheduler, Timers}
import akka.event.LoggingReceive
import akka.pattern.pipe
import com.cyclone.akka.ActorSystemComponent
import com.cyclone.util.kerberos.KerberosDeploymentActor._
import com.cyclone.util.kerberos.settings.{
  ArtifactDeploymentResult,
  KerberosDeploymentSettings,
  KerberosDeploymentSettingsComponent
}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Manages deployment of Kerberos artifacts that are
  * taken from a [[KerberosArtifactsSource]] and deployed using a [[KerberosDeployer]].
  *
  * Automatically performs retries after failed deployment.
  *
  * The need for re-deployment (e.g. following config change) must be detected externally.
  */
trait KerberosDeploymentActorComponent {
  def kerberosDeploymentActor: ActorRef
}

trait DefaultKerberosDeploymentActorComponent extends KerberosDeploymentActorComponent {
  self: KerberosArtifactsSourceComponent
    with KerberosDeployerComponent
    with ActorSystemComponent
    with KerberosDeploymentSettingsComponent =>

  lazy val kerberosDeploymentActor: ActorRef = actorSystem.actorOf(
    KerberosDeploymentActor
      .props(kerberosArtifactsSource, kerberosDeployer, kerberosDeploymentSettings)
  )
}

object KerberosDeploymentActor {

  def props(
    kerberosArtifactsSource: KerberosArtifactsSource,
    kerberosDeployer: KerberosDeployer,
    settings: KerberosDeploymentSettings
  ): Props =
    Props(new KerberosDeploymentActor(kerberosArtifactsSource, kerberosDeployer, settings))

  case object Deploy

  case object GetState

  case class State(
    numDeployments: Int,
    deployInProgress: Boolean,
    lastDeploymentInfo: Option[ArtifactDeploymentInfo]
  )

  object GetDeploymentInfo

}

private[kerberos] class KerberosDeploymentActor(
  kerberosArtifactsSource: KerberosArtifactsSource,
  kerberosDeployer: KerberosDeployer,
  settings: KerberosDeploymentSettings
) extends Actor
    with Timers
    with ActorLogging {
  implicit val sched: Scheduler = context.system.scheduler

  private val retryKey = "Retry"

  private var clientsRequiringInfo = List.empty[ActorRef]

  def receive: Receive = idle(0, None)

  def idle(numDeployments: Int, lastInfo: Option[ArtifactDeploymentInfo]): Receive =
    LoggingReceive.withLabel("idle") {

      case Deploy =>
        deploy(lastInfo).pipeTo(self)
        context become deploying(numDeployments, lastInfo)

      case GetDeploymentInfo =>
        lastInfo match {
          case Some(info) => sender() ! info
          case None       => clientsRequiringInfo = sender() :: clientsRequiringInfo
        }

      case GetState => sender() ! State(numDeployments, deployInProgress = false, lastInfo)
    }

  def deploying(numDeployments: Int, lastInfo: Option[ArtifactDeploymentInfo]): Receive =
    LoggingReceive.withLabel("deploying") {

      case GetDeploymentInfo =>
        lastInfo match {
          case Some(info) => sender() ! info
          case None       => clientsRequiringInfo = sender() :: clientsRequiringInfo
        }

      case GetState => sender() ! State(numDeployments, deployInProgress = true, lastInfo)

      case Deploy =>
        timers.cancel(retryKey)
        deploy(lastInfo).pipeTo(self)

      case result: ArtifactDeploymentResult =>
        clientsRequiringInfo.reverse.foreach(_ ! result.information)
        clientsRequiringInfo = Nil
        context become idle(numDeployments + 1, Some(result.information))

      case Failure(e) =>
        log.warning("Failed to deploy Kerberos artifacts - will retry: {}", e.getMessage)
        timers.startSingleTimer(retryKey, Deploy, settings.retryDelay)
    }

  private def deploy(deploymentLocations: Option[ArtifactDeploymentInfo]) = {
    for {
      kerberosArtifacts <- kerberosArtifactsSource.kerberosArtifacts
      result            <- kerberosDeployer.deploy(kerberosArtifacts, deploymentLocations)
    } yield result
  }
}
