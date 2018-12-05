package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.typesafe.scalalogging.LazyLogging
import scalaz.Scalaz._

/**
  * Holds FRU information for standard FRUs according to the FRU specification.
  */
case class StandardFru(
  chassisInfo: Option[ChassisInfo],
  boardInfo: Option[BoardInfo],
  productInfo: Option[ProductInfo],
  multiRecords: Seq[MultiRecord]
) extends Fru

object StandardFru {
  implicit val decoder: Decoder[StandardFru] with LazyLogging = new Decoder[StandardFru] with LazyLogging {
    def decode(data: ByteString): StandardFru = {
      logger.debug(s"Decoding standard FRU from ${data.toHexString()}")

      val iterator = data.iterator
      val is = iterator.asInputStream

      val formatVersionNumber = is.readByte.bits0To3
      require(formatVersionNumber == 1, "Unsupported version number")

      /* val internalUseAreaOffset = */ is.readByte.toUnsignedInt * 8
      val chassisInfoAreaOffset = is.readByte.toUnsignedInt * 8
      val boardInfoAreaOffset = is.readByte.toUnsignedInt * 8
      val productInfoAreaOffset = is.readByte.toUnsignedInt * 8

      /* val multiRecordAreaOffset = */ is.readByte.toUnsignedInt * 8

      // PAD
      is.skip(1)

      require(is.readByte == checksum(data.take(6)))

      StandardFru(
        chassisInfo = (chassisInfoAreaOffset != 0).option(data.drop(chassisInfoAreaOffset).as[ChassisInfo]),
        boardInfo = (boardInfoAreaOffset != 0).option(data.drop(boardInfoAreaOffset).as[BoardInfo]),
        productInfo = (productInfoAreaOffset != 0).option(data.drop(productInfoAreaOffset).as[ProductInfo]),
        Seq.empty // TODO support multi records
      )
    }
  }
}