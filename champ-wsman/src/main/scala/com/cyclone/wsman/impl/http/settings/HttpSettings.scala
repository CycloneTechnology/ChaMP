package com.cyclone.wsman.impl.http.settings
import com.cyclone.util.ConfigUtils._
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

case class HttpSettings(
  connectTimeout: FiniteDuration = 10.seconds,
  defaultRequestTimeout: FiniteDuration = 10.seconds,
  minimumRequestTimeout: FiniteDuration = 2.seconds
) {
  assert(defaultRequestTimeout >= minimumRequestTimeout)
}

object HttpSettings {

  lazy val fromConfig: HttpSettings = {
    val config = ConfigFactory.load()

    HttpSettings(
      config.finiteDuration("cyclone.wsman.http.connectTimeout"),
      config.finiteDuration("cyclone.wsman.http.defaultRequestTimeout"),
      config.finiteDuration("cyclone.wsman.http.minimumRequestTimeout")
    )
  }
}

trait HttpSettingsComponent {
  def httpSettings: HttpSettings
}

trait ConfigHttpSettingsComponent extends HttpSettingsComponent {
  lazy val httpSettings: HttpSettings =
    HttpSettings.fromConfig
}
