package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec.{Decoder, _}
import org.joda.time.DateTime

/**
  * From Board Info Area data
  */
case class BoardInfo(
  manufactureDate: DateTime,
  manufacturer: String,
  productName: String,
  serialNumber: String,
  partNumber: String,
  fruFileId: Option[String],
  customFields: Seq[FruField]
)

object BoardInfo {
  implicit val decoder: Decoder[BoardInfo] = new Decoder[BoardInfo] {

    def decode(data: ByteString): BoardInfo = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val formatVersionNumber = is.readByte.bits0To3.toUnsignedInt
      require(formatVersionNumber == 1, "Unsupported version number")

      val areaLength = is.readByte.toUnsignedInt * 8

      implicit val languageCode: LanguageCode = is.readByte.as[LanguageCode]

      val manufactureDate = is.read(3).as(MoreCodecs.datetimeCodecFrom1996InMinutes)

      val fieldsIterator = FieldsIterator.from(iterator.toByteString)

      val manufacturer = fieldsIterator.next()
      val productName = fieldsIterator.next()
      val serialNumber = fieldsIterator.next(true)
      val partNumber = fieldsIterator.next()
      val fruFileId = fieldsIterator.nextOpt(true)

      val customFields =
        if (fruFileId.isDefined) fieldsIterator.toSeq else Seq.empty

      require(data(areaLength - 1) == checksum(data.take(areaLength - 1)))

      BoardInfo(
        manufactureDate,
        manufacturer.stringValue,
        productName.stringValue,
        serialNumber.stringValue,
        partNumber.stringValue,
        fruFileId.map(_.stringValue),
        customFields
      )
    }
  }
}
