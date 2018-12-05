package com.cyclone.util.spnego

import akka.http.scaladsl.server.Directive1
import com.cyclone.util.kerberos.KerberosDeploymentComponent
import com.typesafe.config.{Config, ConfigFactory}

trait SpnegoDirectives {
  self: KerberosDeploymentComponent =>

  def spnegoAuthenticate(config: Config = ConfigFactory.load()): Directive1[Token] =
    SpnegoAuthenticator.spnegoAuthenticate(config, kerberosDeployment)
}
