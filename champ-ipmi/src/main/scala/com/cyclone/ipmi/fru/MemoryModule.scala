package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.sdr.jedec.ManufacturerIdentificationCode
import com.google.common.base.Charsets

sealed trait MemoryType {
  def decodeModule(data: ByteString): MemoryModule
}

object MemoryType {
  implicit val decoder: Decoder[MemoryType] = new Decoder[MemoryType] {

    def decode(data: ByteString): MemoryType =
      data(0).toUnsignedInt match {
        case 0x06      => `DDR SGRAM`
        case 0x07      => `DDR SDRAM`
        case 0x08      => `DDR2 SDRAM`
        case 0x09      => `DDR2 SDRAM FB-DIMM`
        case 0x0A      => `DDR2 SDRAM FB-DIMM PROBE`
        case 0x0B      => `DDR3 SDRAM`
        case 0x0C      => `DDR4 SDRAM`
        case code: Int => Other(code)
      }
  }

  case object `DDR SGRAM` extends MemoryType with Type1MemoryDecoder

  case object `DDR SDRAM` extends MemoryType with Type1MemoryDecoder

  case object `DDR2 SDRAM` extends MemoryType with Type1MemoryDecoder

  case object `DDR2 SDRAM FB-DIMM` extends MemoryType with Type2MemoryDecoder

  case object `DDR2 SDRAM FB-DIMM PROBE` extends MemoryType with Type2MemoryDecoder

  case object `DDR3 SDRAM` extends MemoryType with Type2MemoryDecoder

  case object `DDR4 SDRAM` extends MemoryType with Type3MemoryDecoder

  case class Other(code: Int) extends MemoryType {

    def decodeModule(data: ByteString) = MemoryModule(
      memoryType = this,
      manufacturer = None,
      partNumber = None,
      serialNumber = None
    )
  }

}

trait StandardMemoryDecoder {
  self: MemoryType =>

  protected def decodeManufacturerUsingContinuationFlag(data: ByteString): Option[String] =
    ManufacturerIdentificationCode.decodeUsingContinuationFlag(data).map(_.name)

  protected def decodeManufacturerUsingContinuationCount(data: ByteString): Option[String] =
    ManufacturerIdentificationCode.decodeUsingContinuationCount(data).map(_.name)

  protected def decodeSerialNumber(data: ByteString) =
    Some(data.toHexString(""))

  protected def decodePartNumber(data: ByteString) =
    Some(data.decodeString(Charsets.US_ASCII.name).trim)
}

trait Type1MemoryDecoder extends StandardMemoryDecoder {
  self: MemoryType =>

  def decodeModule(data: ByteString) =
    MemoryModule(
      memoryType = data(2).as[MemoryType],
      manufacturer = decodeManufacturerUsingContinuationFlag(data.take(64 to 71)),
      serialNumber = decodeSerialNumber(data.take(95 to 98)),
      partNumber = decodePartNumber(data.take(73 to 90))
    )
}

trait Type2MemoryDecoder extends StandardMemoryDecoder {
  self: MemoryType =>

  def decodeModule(data: ByteString) =
    MemoryModule(
      memoryType = data(2).as[MemoryType],
      manufacturer = decodeManufacturerUsingContinuationCount(data.take(117 to 118)),
      serialNumber = decodeSerialNumber(data.take(122 to 125)),
      partNumber = decodePartNumber(data.take(128 to 145))
    )
}

trait Type3MemoryDecoder extends StandardMemoryDecoder {
  self: MemoryType =>

  def decodeModule(data: ByteString) =
    MemoryModule(
      memoryType = data(2).as[MemoryType],
      manufacturer = decodeManufacturerUsingContinuationCount(data.take(320 to 321)),
      serialNumber = decodeSerialNumber(data.take(325 to 328)),
      partNumber = decodePartNumber(data.take(329 to 348))
    )
}

case class MemoryModule(
  memoryType: MemoryType,
  manufacturer: Option[String],
  partNumber: Option[String],
  serialNumber: Option[String]
) extends Fru

object MemoryModule {
  implicit val decoder: Decoder[MemoryModule] = new Decoder[MemoryModule] {

    def decode(data: ByteString): MemoryModule =
      data(2).as[MemoryType].decodeModule(data)
  }
}
