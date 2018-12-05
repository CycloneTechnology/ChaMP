package com.cyclone.wsman.impl

sealed trait WSManAvailability {
  def message: String

  def possibilyAvailable: Boolean
}

object WSManAvailability {

  case object NotListening extends WSManAvailability {
    val possibilyAvailable = false

    def message: String = "NotListening"
  }

  case object Timeout extends WSManAvailability {
    // At least at the moment not available - likely that
    // an identify would also timeout
    val possibilyAvailable = false

    def message: String = "Timeout"
  }

  case object PathNotFound extends WSManAvailability {
    val possibilyAvailable = false

    def message: String = "PathNotFound"
  }

  case object NoAuthWrongScheme extends WSManAvailability {
    val possibilyAvailable = false

    def message: String = "NoAuthWrongScheme"
  }

  case object BadCredentials extends WSManAvailability {
    val possibilyAvailable = true

    def message: String = "BadCredentials"
  }

  case class OtherException(throwable: Throwable, possibilyAvailable: Boolean) extends WSManAvailability {
    def message: String = s"Exception thrown: ${throwable.getMessage}"
  }

  case class OtherStatusCode(responseCode: Int, possibilyAvailable: Boolean) extends WSManAvailability {
    def message: String = s"Response status code: $responseCode"
  }

}
