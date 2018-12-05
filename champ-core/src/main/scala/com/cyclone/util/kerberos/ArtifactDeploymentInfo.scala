package com.cyclone.util.kerberos

import java.nio.file.Path

/**
  * Kerberos deployment information.
  */
case class ArtifactDeploymentInfo(
  kerb5ConfPath: Path,
  loginConfPath: Path,
  servicePrincipalName: String,
  keyTabPath: Path
)
