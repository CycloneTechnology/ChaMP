package com.cyclone.util.kerberos

import scala.concurrent.Future

/**
  * Source of [[KerberosArtifacts]]
  */
trait KerberosArtifactsSource {
  def kerberosArtifacts: Future[KerberosArtifacts]
}

trait KerberosArtifactsSourceComponent {
  def kerberosArtifactsSource: KerberosArtifactsSource
}
