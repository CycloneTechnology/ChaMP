package com.cyclone.ipmi.protocol.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec.{Decoder, _}

/**
  * From Chassis Info Area data
  */
case class ChassisInfo(
  chassisType: ChassisType,
  chassisPartNumber: String,
  chassisSerialNumber: String,
  customFields: Seq[FruField]
)

object ChassisInfo {
  implicit val decoder: Decoder[ChassisInfo] = new Decoder[ChassisInfo] {

    def decode(data: ByteString): ChassisInfo = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val formatVersionNumber = is.readByte.bits0To3.toUnsignedInt
      require(formatVersionNumber == 1, "Unsupported version number")

      val areaLength = is.readByte.toUnsignedInt * 8
      val chassisType = is.readByte.as[ChassisType]

      // Assume english - no field in spec to specify
      implicit val languageCode: LanguageCode = LanguageCode(0)

      val fieldsIterator = FieldsIterator.from(iterator.toByteString)

      val chassisPartNumber = fieldsIterator.next()
      val chassisSerialNumber = fieldsIterator.next(true)

      val customFields = fieldsIterator.toSeq

      require(data(areaLength - 1) == checksum(data.take(areaLength - 1)))

      ChassisInfo(
        chassisType,
        chassisPartNumber.stringValue,
        chassisSerialNumber.stringValue,
        customFields
      )
    }
  }
}
