package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.protocol.sdr.FieldPrefix.{EmptyField, FixedLengthField, NoMoreFields}

case class SensorId(id: String)

object SensorId {
  implicit val decoder: Decoder[SensorId] = new Decoder[SensorId] {

    def decode(data: ByteString): SensorId = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val deviceIdFieldPrefix = is.readByte.as[FieldPrefix]

      SensorId(deviceIdFieldPrefix match {
        case FixedLengthField(tpe, len) => tpe.decode(is.read(len)).stringValue
        case EmptyField                 => ""
        case NoMoreFields               => ""
      })
    }
  }
}
