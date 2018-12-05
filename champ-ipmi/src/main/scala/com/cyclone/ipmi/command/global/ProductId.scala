package com.cyclone.ipmi.command.global

import akka.util.ByteString
import com.cyclone.ipmi.codec._

case class ProductId(value: Int)

object ProductId {
  implicit val decoder: Decoder[ProductId] = new Decoder[ProductId] {

    def decode(data: ByteString) =
      ProductId(data.take(2).as[Short].toInt)
  }
}
