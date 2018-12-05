package com.cyclone.util.net

import com.cyclone.util.{FQNFormat, PasswordCredentials}

sealed trait SecurityContext {
  def description: String
}

case class PasswordSecurityContext(
  credentials: PasswordCredentials,
  authenticationMethod: AuthenticationMethod
) extends SecurityContext {

  def description: String = {
    val user = credentials.fullyQualifiedUsername(FQNFormat.UPNUpper)

    s"$user using $authenticationMethod authentication"
  }
}

object PasswordSecurityContext {

  def apply(
    fullyQualifiedUser: String,
    password: String,
    authenticationMethod: AuthenticationMethod
  ): PasswordSecurityContext =
    PasswordSecurityContext(
      PasswordCredentials.parseFullyQualifiedUser(fullyQualifiedUser, password),
      authenticationMethod
    )
}
