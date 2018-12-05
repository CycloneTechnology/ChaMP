package com.cyclone.util.kerberos.settings

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.FiniteDuration

import com.cyclone.util.ConfigUtils._

case class KerberosDeploymentSettings(
  retryDelay: FiniteDuration
)

object KerberosDeploymentSettings {

  lazy val fromConfig: KerberosDeploymentSettings = {
    val config = ConfigFactory.load()

    KerberosDeploymentSettings(
      config.finiteDuration("cyclone.kerberos.deployment.retry.delay")
    )
  }
}

trait KerberosDeploymentSettingsComponent {
  def kerberosDeploymentSettings: KerberosDeploymentSettings
}

trait ConfigKerberosDeploymentSettingsComponent extends KerberosDeploymentSettingsComponent {
  lazy val kerberosDeploymentSettings: KerberosDeploymentSettings = KerberosDeploymentSettings.fromConfig
}

