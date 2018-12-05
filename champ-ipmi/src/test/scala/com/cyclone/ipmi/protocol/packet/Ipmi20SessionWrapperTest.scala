package com.cyclone.ipmi.protocol.packet

import akka.util.ByteString
import com.cyclone.ipmi.IpmiCredentials
import com.cyclone.ipmi.protocol.packet.SessionId.ManagedSystemSessionId
import com.cyclone.ipmi.protocol.security._
import com.cyclone.ipmi.protocol.{Oem, SessionContext, V20SessionContext}
import org.scalatest.{Inside, Matchers, WordSpec}
import scalaz.\/-

/**
  * Tests for [[Ipmi20SessionWrapper]]
  */
class Ipmi20SessionWrapperTest extends WordSpec with Matchers with Inside {

  "a session wrapper codec" must {
    "encode and decode an unencrypted, unauthenticated session wrapper" in {
      val ctx = SessionContext.NoSession
      val w = Ipmi20SessionWrapper.Request(
        managedSystemSessionId = ManagedSystemSessionId(0),
        sessionSequenceNumber = SessionSequenceNumber(0),
        encryptor = ctx.encryptor,
        requestHashMaker = ctx.requestHashMaker,
        payload = ByteString(1, 2, 3, 4, 5, 6)
      )
      val bs = IpmiSessionWrapper.encode(w)

      val w2OrError = IpmiSessionWrapper.decode(bs, ctx)

      inside(w2OrError) {
        case \/-(w2) => w2.payload shouldBe w.payload
      }
    }

    "encode and decode oem payload" in {
      val ctx = SessionContext.NoSession
      val w = Ipmi20SessionWrapper.Request(
        managedSystemSessionId = ManagedSystemSessionId(0),
        sessionSequenceNumber = SessionSequenceNumber(0),
        encryptor = ctx.encryptor,
        requestHashMaker = ctx.requestHashMaker,
        payload = ByteString(1, 2, 3, 4, 5, 6),
        oem = Some(Oem(1, 2.toByte, 3.toByte)),
        payloadType = PayloadType.Oem
      )
      val bs = IpmiSessionWrapper.encode(w)

      val w2OrError = IpmiSessionWrapper.decode(bs, ctx)

      inside(w2OrError) {
        case \/-(w2) => w2.payload shouldBe w.payload
      }
    }

    "encode and decode an encrypted, authenticated session wrapper" in {
      val sik = AuthenticationAlgorithm.RakpHmacSha1
        .determineSik(Key.KG.fromCredentials(IpmiCredentials("root", "password")), ByteString(1, 2, 3))

      val sessionId = ManagedSystemSessionId(123)
      val ctx =
        V20SessionContext(
          sessionId,
          cipherSuite = CipherSuite(
            AuthenticationAlgorithm.NoAuth,
            ConfidentialityAlgorithm.AesCbc128,
            IntegrityAlgorithm.HmacSha1_96
          ),
          optSik = Some(sik)
        )

      val w = Ipmi20SessionWrapper.Request(
        managedSystemSessionId = sessionId,
        sessionSequenceNumber = SessionSequenceNumber(321),
        encryptor = ctx.encryptor,
        requestHashMaker = ctx.requestHashMaker,
        payload = ByteString(1, 2, 3, 4, 5, 6),
        payloadAuthenticated = true,
        payloadEncrypted = true
      )

      val bs = IpmiSessionWrapper.encode(w)

      val w2OrError = IpmiSessionWrapper.decode(bs, ctx)

      inside(w2OrError) {
        case \/-(w2) => w2.payload shouldBe w.payload
      }
    }
  }
}
