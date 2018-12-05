package com.cyclone.ipmi.protocol.security

import akka.util.ByteString
import com.cyclone.ipmi.{BmcKey, IpmiCredentials, Password}

/**
  * Keys for HMAC hashing
  */
sealed trait Key {
  private[security] def byteArray: Array[Byte]
}

object Key {

  case class UID(password: Password) extends Key {
    private[security] def byteArray = password.byteArray
  }

  object UID {

    def fromCredentials(credentials: IpmiCredentials): UID =
      UID(credentials.password)
  }

  case class KG(password: Password, bmcKey: Option[BmcKey]) extends Key {
    private[security] def byteArray = bmcKey.map(_.byteArray).getOrElse(password.byteArray)
  }

  object KG {

    def fromCredentials(credentials: IpmiCredentials): KG =
      KG(credentials.password, credentials.bmcKey)
  }

  case class SIK private[security] (key: ByteString) extends Key {
    private[security] def byteArray = key.toArray
  }

}
