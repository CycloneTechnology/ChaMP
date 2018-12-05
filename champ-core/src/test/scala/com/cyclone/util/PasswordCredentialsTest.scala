package com.cyclone.util

import com.cyclone.util.FQNFormat.{DownLevel, UPN, UPNUpper}
import org.scalatest.{Matchers, WordSpec}

/**
  * Tests for [[PasswordCredentials]]
  */
class PasswordCredentialsTest extends WordSpec with Matchers {
  "credentials" when {
    "getting fully qualified user name" must {
      "return user name for non domain credentials" in {
        val credentials = PasswordCredentials.fromStrings("userName", "password")

        credentials.fullyQualifiedUsername(UPN) shouldBe "userName"
        credentials.fullyQualifiedUsername(UPNUpper) shouldBe "userName"
        credentials.fullyQualifiedUsername(DownLevel) shouldBe "userName"
      }

      "return user name for non domain credentials with empty domain" in {
        val credentials = PasswordCredentials.fromStrings("userName", "password", "  ")

        credentials.fullyQualifiedUsername(UPN) shouldBe "userName"
        credentials.fullyQualifiedUsername(UPNUpper) shouldBe "userName"

        credentials.fullyQualifiedUsername(DownLevel) shouldBe "userName"
      }

      "support UPN format" in {
        PasswordCredentials
          .fromStrings("userName", "password", "domain.com")
          .fullyQualifiedUsername(UPN) shouldBe "userName@domain.com"
      }

      "support UPN upper format" in {
        PasswordCredentials
          .fromStrings("userName", "password", "domain.com")
          .fullyQualifiedUsername(UPNUpper) shouldBe "userName@DOMAIN.COM"
      }

      "down level upper format takes only first part of domain name" in {
        PasswordCredentials
          .fromStrings("userName", "password", "domain.com")
          .fullyQualifiedUsername(DownLevel) shouldBe "domain\\userName"
      }
    }

    "parsing credentials" must {
      "parse simple user" in {
        PasswordCredentials.parseFullyQualifiedUser("user", "password") shouldBe
        PasswordCredentials("user", Password("password"))
      }

      "parse downlevel format" in {
        PasswordCredentials.parseFullyQualifiedUser("domain\\user", "password") shouldBe
        PasswordCredentials("user", Password("password"), Some("domain"))
      }

      "parse UPN formar" in {
        PasswordCredentials.parseFullyQualifiedUser("user@domain", "password") shouldBe
        PasswordCredentials("user", Password("password"), Some("domain"))
      }
    }
  }
}
