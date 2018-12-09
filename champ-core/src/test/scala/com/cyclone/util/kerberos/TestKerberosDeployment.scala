package com.cyclone.util.kerberos

import akka.testkit.{TestKit, TestProbe}
import com.cyclone.akka.{ActorSystemComponent, MaterializerComponent}
import com.cyclone.util.kerberos.settings.{KerberosDeploymentSettings, KerberosDeploymentSettingsComponent}
import org.scalatest.Matchers

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Sets up Kerberos for tests
  */
trait TestKerberosDeployment
    extends DefaultKerberosDeploymentActorComponent
    with DefaultKerberosDeployerComponent
    with KerberosDeploymentSettingsComponent
    with KerberosArtifactsSourceComponent
    with DefaultKerberosDeploymentComponent {
  self: ActorSystemComponent with MaterializerComponent with Matchers =>

  def kerberosDeploymentSettings: KerberosDeploymentSettings =
    KerberosDeploymentSettings(retryDelay = 1.second)

  def kerberosArtifactsSource: KerberosArtifactsSource = new KerberosArtifactsSource {
    def kerberosArtifacts: Future[KerberosArtifacts] =
      Future.successful(KerberosArtifacts.simpleFromConfig)
  }

  new TestKit(actorSystem) {
    val probe = TestProbe()(actorSystem)

    kerberosDeployer.deploy(KerberosArtifacts.simpleFromConfig)

    kerberosDeploymentActor ! KerberosDeploymentActor.Deploy

    awaitAssert {
      kerberosDeploymentActor.tell(KerberosDeploymentActor.GetDeploymentInfo, probe.ref)
      probe.expectMsgType[ArtifactDeploymentInfo]
    }
  }
}
