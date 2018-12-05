package com.cyclone.ipmi

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.google.common.base.Charsets

/**
  * A user name
  */
case class Username(name: String) extends AnyVal

object Username {
  implicit val coder: Coder[Username] = new Coder[Username] {

    def encode(a: Username): ByteString =
      MoreCodecs.shortStringLengthPrefixedStringCodec.encode(a.name)
  }
}

case class UsernameV15(name: String) extends AnyVal

object UsernameV15 {
  implicit val coder: Coder[UsernameV15] = new Coder[UsernameV15] {

    def encode(a: UsernameV15): ByteString =
      MoreCodecs.defaultTerminatedStringCodec.encode(a.name)
  }
}

/**
  * A password
  */
case class Password(value: String) extends AnyVal {
  def byteArray: Array[Byte] = value.getBytes(Charsets.US_ASCII)

  override def toString: String = "Password(**********)"
}

object Password {
  implicit val coder: Coder[Password] = new Coder[Password] {

    def encode(a: Password): ByteString =
      MoreCodecs.defaultTerminatedStringCodec.encode(a.value)
  }
}

/**
  * A BMC key. Used in addition to password if required by the managed system.
  */
case class BmcKey(bytes: ByteString) extends AnyVal {
  def byteArray: Array[Byte] = bytes.toArray
}

/**
  * Credentials for IPMI
  */
case class IpmiCredentials(username: Username, password: Password, bmcKey: Option[BmcKey] = None) {
  def usernameV15: UsernameV15 = UsernameV15(username.name)
}

object IpmiCredentials {

  def apply(username: String, password: String): IpmiCredentials =
    IpmiCredentials(Username(username), Password(password))
}
