package com.cyclone.util.kerberos

import akka.pattern.ask
import akka.util.Timeout
import com.cyclone.util.kerberos.settings.ArtifactDeploymentResult

import scala.concurrent.Future

trait KerberosDeployment {
  /**
    * Gets the latest [[ArtifactDeploymentResult]] or, if a deployment is underway
    * and there is no deployment result, waits (up to the specified timeout)
    * until the deployment completes.
    */
  def latestArtifactDeploymentInfo(implicit timeout: Timeout): Future[ArtifactDeploymentInfo]
}

trait KerberosDeploymentComponent {
  def kerberosDeployment: KerberosDeployment
}

trait DefaultKerberosDeploymentComponent extends KerberosDeploymentComponent {
  self: KerberosDeploymentActorComponent =>

  lazy val kerberosDeployment: KerberosDeployment = new KerberosDeployment {
    def latestArtifactDeploymentInfo(implicit timeout: Timeout): Future[ArtifactDeploymentInfo] =
      (kerberosDeploymentActor ? KerberosDeploymentActor.GetDeploymentInfo).mapTo[ArtifactDeploymentInfo]
  }
}