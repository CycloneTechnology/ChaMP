package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec.{Decoder, _}

/**
  * From Product Info Area data
  */
case class ProductInfo(
  manufacturerName: String,
  productName: String,
  partModelNumber: String,
  version: String,
  serialNumber: String,
  assetTag: String,
  fruFileId: Option[String],
  customFields: Seq[FruField]
)

object ProductInfo {
  implicit val decoder: Decoder[ProductInfo] = new Decoder[ProductInfo] {

    def decode(data: ByteString): ProductInfo = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val formatVersionNumber = is.readByte.bits0To3.toUnsignedInt
      require(formatVersionNumber == 1, "Unsupported version number")

      val areaLength = is.readByte.toUnsignedInt * 8

      implicit val languageCode: LanguageCode = is.readByte.as[LanguageCode]

      val fieldsIterator = FieldsIterator.from(iterator.toByteString)

      val manufacturerName = fieldsIterator.next()
      val productName = fieldsIterator.next()
      val partModelNumber = fieldsIterator.next()
      val version = fieldsIterator.next()
      val serialNumber = fieldsIterator.next(true)
      val assetTag = fieldsIterator.next()
      val fruFileId = fieldsIterator.nextOpt()

      val customFields =
        if (fruFileId.isDefined) fieldsIterator.toSeq else Seq.empty

      require(data(areaLength - 1) == checksum(data.take(areaLength - 1)))

      ProductInfo(
        manufacturerName.stringValue,
        productName.stringValue,
        partModelNumber.stringValue,
        version.stringValue,
        serialNumber.stringValue,
        assetTag.stringValue,
        fruFileId.map(_.stringValue),
        customFields
      )
    }
  }
}
