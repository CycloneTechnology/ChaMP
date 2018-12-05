package com.cyclone.util

case class Password(pwd: String) extends AnyVal {
  override def toString: String = "Password(**********)"
}

/**
  * Represents password-based credentials with an optional domain.
  */
case class PasswordCredentials(username: String, password: Password, optDomain: Option[String] = None) {
  def fullyQualifiedUsername(format: FQNFormat): String = {
    optDomain match {
      case Some(domain) => format.doFormatUsername(username, domain)
      case None         => username
    }
  }

  def plainPassword: String = password.pwd

  def passwordChars: Array[Char] = password.pwd.toCharArray

  def domainOrBlank: String = optDomain.getOrElse("")

  def upperCaseDomain: Option[String] = optDomain.map(_.toUpperCase())
}

object PasswordCredentials {
  /**
    * Java friendly factory for [[PasswordCredentials]] that allows a null or empty domain
    */
  def fromStrings(username: String, password: String, domainOrNull: String): PasswordCredentials =
    PasswordCredentials(username, Password(password), StringUtils.toOption(domainOrNull))

  /**
    * Java friendly factory for [[PasswordCredentials]] with no domain
    */
  def fromStrings(username: String, password: String): PasswordCredentials =
    PasswordCredentials(username, Password(password), None)

  /**
    * Parse a user name to determine whether it has a domain and then wraps
    * as an appropriate [[PasswordCredentials]] along with the specified password.
    */
  def parseFullyQualifiedUser(fullyQualifiedUsername: String, password: String): PasswordCredentials = {
    val (user, optDomain) = {
      val atIdx = fullyQualifiedUsername.indexOf('@')

      if (atIdx >= 0) {
        val (u, d) = fullyQualifiedUsername.splitAt(atIdx)
        (u, d.drop(1))
      } else {
        val slashIdx = fullyQualifiedUsername.indexOf('\\')

        if (slashIdx >= 0) {
          val (d, u) = fullyQualifiedUsername.splitAt(slashIdx)
          (u.drop(1), d)
        } else
          (fullyQualifiedUsername, "")
      }
    }

    PasswordCredentials.fromStrings(user, password, optDomain)
  }

}

sealed trait FQNFormat {
  protected[util] def doFormatUsername(username: String, domain: String): String
}

object FQNFormat {

  case object UPN extends FQNFormat {
    protected[util] def doFormatUsername(username: String, domain: String): String =
      username + "@" + domain
  }

  case object UPNUpper extends FQNFormat {
    protected[util] def doFormatUsername(username: String, domain: String): String =
      username + "@" + domain.toUpperCase
  }

  case object DownLevel extends FQNFormat {
    protected[util] def doFormatUsername(username: String, domain: String): String = {
      domain.split('.').headOption match {
        case None             => username
        case Some(domainPart) => domainPart + "\\" + username
      }
    }

  }

}