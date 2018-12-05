package com.cyclone.ipmi.protocol.security

import akka.util.ByteString
import com.cyclone.ipmi.IpmiCredentials
import org.scalatest.{Matchers, WordSpec}

/**
  * Tests for [[ConfidentialityAlgorithm]]
  */
class ConfidentialityAlgorithmTest extends WordSpec with Matchers {

  val credentials = IpmiCredentials("root", "password")

  def sikFrom(base: ByteString) =
    AuthenticationAlgorithm.RakpHmacSha1.determineSik(Key.KG.fromCredentials(credentials), base)

  "a MACBasedConfidentialityAlgorithm" must {
    "encrypt and decrypt" in {
      val sik = sikFrom(ByteString(1, 2, 3, 4))
      val data = ByteString("hello world")

      val enc = ConfidentialityAlgorithm.AesCbc128.encrypt(sik, data)

      val dec = ConfidentialityAlgorithm.AesCbc128.decrypt(sik, enc)

      dec shouldBe data
    }

    "fail to decrypt if wrong key" in {
      val sik1 = sikFrom(ByteString(1, 2, 3, 4))
      val sik2 = sikFrom(ByteString(4, 3, 2, 1))
      val data = ByteString("hello world")

      val enc = ConfidentialityAlgorithm.AesCbc128.encrypt(sik1, data)

      val dec = ConfidentialityAlgorithm.AesCbc128.decrypt(sik2, enc)

      dec should not be data
    }
  }
}
