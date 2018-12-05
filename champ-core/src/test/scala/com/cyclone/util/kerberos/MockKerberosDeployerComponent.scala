package com.cyclone.util.kerberos

import com.cyclone.util.MockeryComponent
import com.cyclone.util.kerberos.settings.ArtifactDeploymentResult
import org.jmock.AbstractExpectations._
import org.jmock.Expectations

import scala.concurrent.Future

trait MockKerberosDeployerComponent extends KerberosDeployerComponent {
  self: MockeryComponent =>

  lazy val kerberosDeployer = mockery.mock(classOf[KerberosDeployer])

  def willDeployKerberosArtifacts(
    kerberosArtifacts: KerberosArtifacts,
    locations: Option[ArtifactDeploymentInfo],
    result: ArtifactDeploymentResult): Unit =
    willDeployKerberosArtifactsF(kerberosArtifacts, locations, Future.successful(result))

  def willFailToDeployKerberosArtifacts(
    kerberosArtifacts: KerberosArtifacts,
    locations: Option[ArtifactDeploymentInfo],
    exception: Exception): Unit =
    willDeployKerberosArtifactsF(kerberosArtifacts, locations, Future.failed(exception))

  def willDeployKerberosArtifactsF(
    kerberosArtifacts: KerberosArtifacts,
    locations: Option[ArtifactDeploymentInfo],
    result: Future[ArtifactDeploymentResult]): Unit =
    mockery.checking(new Expectations {
      oneOf(kerberosDeployer).deploy(kerberosArtifacts, locations)
      will(returnValue(result))
    })
}