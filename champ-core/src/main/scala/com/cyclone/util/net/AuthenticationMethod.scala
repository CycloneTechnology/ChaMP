package com.cyclone.util.net

sealed trait AuthenticationMethod {
  def challengeHeaders: Seq[String]
}

case object AuthenticationMethod {

  case object Basic extends AuthenticationMethod {
    def challengeHeaders: Seq[String] = Seq("Basic")
  }

  case object Kerberos extends AuthenticationMethod {
    def challengeHeaders: Seq[String] = List("Kerberos", "Negotiate")
  }

}
