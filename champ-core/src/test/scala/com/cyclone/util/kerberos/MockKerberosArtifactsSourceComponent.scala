package com.cyclone.util.kerberos

import com.cyclone.util.MockeryComponent
import org.jmock.AbstractExpectations.returnValue
import org.jmock.Expectations

import scala.concurrent.Future

trait MockKerberosArtifactsSourceComponent extends KerberosArtifactsSourceComponent {
  self: MockeryComponent =>

  lazy val kerberosArtifactsSource = mockery.mock(classOf[KerberosArtifactsSource])

  def willGetKerberosArtifacts(kerberosArtifacts: KerberosArtifacts): Unit =
    willGetKerberosArtifactsF(Future.successful(kerberosArtifacts))

  def willFailToGetKerberosArtifacts(exception: Exception): Unit =
    willGetKerberosArtifactsF(Future.failed(exception))

  def willGetKerberosArtifactsF(kerberosArtifacts: Future[KerberosArtifacts]): Unit =
    mockery.checking(new Expectations {
      oneOf(kerberosArtifactsSource).kerberosArtifacts
      will(returnValue(kerberosArtifacts))
    })
}
