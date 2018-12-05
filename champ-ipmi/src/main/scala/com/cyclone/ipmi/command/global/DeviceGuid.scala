package com.cyclone.ipmi.command.global

import akka.util.ByteString
import com.cyclone.ipmi.codec._

case class DeviceGuid(guid: String)

object DeviceGuid {
  implicit val decoder: Decoder[DeviceGuid] = new Decoder[DeviceGuid] {
    def decode(data: ByteString): DeviceGuid = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val node1 = is.readByte
      val node2 = is.readByte
      val node3 = is.readByte
      val node4 = is.readByte
      val node5 = is.readByte
      val node6 = is.readByte
      val clockSeqAndReserver = is.read(2).as[Short]
      val timeHighAndVersion = is.read(2).as[Short]
      val timeMid = is.read(2).as[Short]
      val timelow = is.read(4).as[Int]

      DeviceGuid(
        "%08x-%04x-%04x-%04x-%02x%02x%02x%02x%02x%02x"
          .format(
            timelow, timeMid, timeHighAndVersion, clockSeqAndReserver,
            node1, node2, node3, node4, node5, node6))
    }
  }
}