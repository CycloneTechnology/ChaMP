package com.cyclone.util

import scala.concurrent.duration.FiniteDuration
import com.typesafe.config.Config
import java.util.concurrent.TimeUnit._

/**
  * Typesafe config utilities.
  */
object ConfigUtils {

  implicit class ConfigEnrichment(config: Config) {

    /**
      * Get a config duration item at the specified path as a FiniteDuration
      */
    def finiteDuration(path: String): FiniteDuration = {
      val i = config.getDuration(path, MILLISECONDS)

      FiniteDuration(i, MILLISECONDS).toCoarsest
    }
  }

}
