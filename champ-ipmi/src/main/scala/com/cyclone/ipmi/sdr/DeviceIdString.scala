package com.cyclone.ipmi.sdr


import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.sdr.FieldPrefix.{EmptyField, FixedLengthField, NoMoreFields}

case class DeviceIdString(id: String)

object DeviceIdString {
  implicit val decoder: Decoder[DeviceIdString] = new Decoder[DeviceIdString] {
    def decode(data: ByteString): DeviceIdString = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val deviceIdFieldPrefix = is.readByte.as[FieldPrefix]

      DeviceIdString(deviceIdFieldPrefix match {
        case FixedLengthField(tpe, len) => tpe.decode(is.read(len)).stringValue
        case EmptyField                 => ""
        case NoMoreFields               => ""
      })
    }
  }
}

