package com.cyclone.ipmi.protocol.packet

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * A session id
  */
sealed trait SessionId extends Any

object SessionId {

  case class ManagedSystemSessionId(id: Int) extends SessionId {
    def isSession: Boolean = id != 0
  }

  object ManagedSystemSessionId {
    implicit val codec: Codec[ManagedSystemSessionId] = new Codec[ManagedSystemSessionId] {
      def encode(a: ManagedSystemSessionId): ByteString = a.id.toBin

      def decode(data: ByteString) =
        ManagedSystemSessionId(data.as[Int])
    }
  }

  case class RemoteConsoleSessionId(id: Int) extends SessionId

  object RemoteConsoleSessionId {
    implicit val codec: Codec[RemoteConsoleSessionId] = new Codec[RemoteConsoleSessionId] {
      def encode(a: RemoteConsoleSessionId): ByteString = a.id.toBin

      def decode(data: ByteString) =
        RemoteConsoleSessionId(data.as[Int])
    }
  }

}
