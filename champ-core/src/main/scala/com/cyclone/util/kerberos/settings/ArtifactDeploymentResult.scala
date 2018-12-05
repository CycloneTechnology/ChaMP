package com.cyclone.util.kerberos.settings

import com.cyclone.util.kerberos.ArtifactDeploymentInfo

/**
  * Result of [[com.cyclone.util.kerberos.KerberosArtifacts]] deployment
  */
case class ArtifactDeploymentResult(
  information: ArtifactDeploymentInfo
)
