package com.cyclone.ipmi

import akka.util.ByteString
import com.cyclone.ipmi.codec.Codec

sealed trait PrivilegeLevel {
  val code: Byte
}

object PrivilegeLevel {
  implicit val codec: Codec[PrivilegeLevel] = new Codec[PrivilegeLevel] {

    def encode(a: PrivilegeLevel) =
      ByteString(a.code)

    def decode(data: ByteString): PrivilegeLevel =
      fromCode(data(0))
  }

  case object Callback extends PrivilegeLevel {
    val code: Byte = 1.toByte
  }

  case object User extends PrivilegeLevel {
    val code: Byte = 2.toByte
  }

  case object Operator extends PrivilegeLevel {
    val code: Byte = 3.toByte
  }

  case object Administrator extends PrivilegeLevel {
    val code: Byte = 4.toByte
  }

  def fromCode(code: Byte): PrivilegeLevel = code match {
    case Callback.code      => Callback
    case User.code          => User
    case Operator.code      => Operator
    case Administrator.code => Administrator
  }
}
