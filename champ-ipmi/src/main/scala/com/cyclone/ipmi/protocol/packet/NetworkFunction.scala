package com.cyclone.ipmi.protocol.packet

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait NetworkFunction {
  def code: Byte
}

object NetworkFunction {

  implicit val decoder: Decoder[NetworkFunction] = new Decoder[NetworkFunction] {
    def decode(data: ByteString): NetworkFunction =
      fromCode(data(0).bits2To7)
  }

  case object ChassisRequest extends NetworkFunction {
    val code: Byte = 0.toByte
  }

  case object ChassisResponse extends NetworkFunction {
    val code: Byte = 1.toByte
  }

  case object BridgeRequest extends NetworkFunction {
    val code: Byte = 2.toByte
  }

  case object BridgeResponse extends NetworkFunction {
    val code: Byte = 3.toByte
  }

  case object SensorRequest extends NetworkFunction {
    val code: Byte = 4.toByte
  }

  case object SensorResponse extends NetworkFunction {
    val code: Byte = 5.toByte
  }

  case object ApplicationRequest extends NetworkFunction {
    val code: Byte = 6.toByte
  }

  case object ApplicationResponse extends NetworkFunction {
    val code: Byte = 7.toByte
  }

  case object FirmwareRequest extends NetworkFunction {
    val code: Byte = 8.toByte
  }

  case object FirmwareResponse extends NetworkFunction {
    val code: Byte = 9.toByte
  }

  case object StorageRequest extends NetworkFunction {
    val code: Byte = 0x0a.toByte
  }

  case object StorageResponse extends NetworkFunction {
    val code: Byte = 0x0b.toByte
  }

  case object MediaSpecificRequest extends NetworkFunction {
    val code: Byte = 0x0c.toByte
  }

  case object MediaSpecificResponse extends NetworkFunction {
    val code: Byte = 0x0d.toByte
  }

  case object Oem29Request extends NetworkFunction {
    val code: Byte = 0x29.toByte
  }

  case object Oem2aResponse extends NetworkFunction {
    val code: Byte = 0x2a.toByte
  }

  case object OemRequest extends NetworkFunction {
    val code: Byte = 0x2e.toByte
  }

  case object OemResponse extends NetworkFunction {
    val code: Byte = 0x2f.toByte
  }

  case object OemFree30hRequest extends NetworkFunction {
    val code: Byte = 0x30.toByte
  }

  case object OemFree30hResponse extends NetworkFunction {
    val code: Byte = 0x31.toByte
  }

  case object OemFree32hRequest extends NetworkFunction {
    val code: Byte = 0x32.toByte
  }

  case object OemFree32hResponse extends NetworkFunction {
    val code: Byte = 0x33.toByte
  }

  case object OemFree34hRequest extends NetworkFunction {
    val code: Byte = 0x34.toByte
  }

  case object OemFree34hResponse extends NetworkFunction {
    val code: Byte = 0x35.toByte
  }

  case object OemFree36hRequest extends NetworkFunction {
    val code: Byte = 0x36.toByte
  }

  case object OemFree36hResponse extends NetworkFunction {
    val code: Byte = 0x37.toByte
  }

  case object OemFree38hRequest extends NetworkFunction {
    val code: Byte = 0x38.toByte
  }

  case object OemFree38hResponse extends NetworkFunction {
    val code: Byte = 0x39.toByte
  }

  case object OemFree3ahRequest extends NetworkFunction {
    val code: Byte = 0x3a.toByte
  }

  case object OemFree3ahResponse extends NetworkFunction {
    val code: Byte = 0x3b.toByte
  }

  case object OemFree3chRequest extends NetworkFunction {
    val code: Byte = 0x3c.toByte
  }

  case object OemFree3chResponse extends NetworkFunction {
    val code: Byte = 0x3d.toByte
  }

  case object OemFree3e0hRequest extends NetworkFunction {
    val code: Byte = 0x3e.toByte
  }

  case object OemFree3ehResponse extends NetworkFunction {
    val code: Byte = 0x3f.toByte
  }

  // Fujitsu - Warning other OEM's may use these!

  case object FujitsuFirmwareRequest extends NetworkFunction {
    val code: Byte = 0x20.toByte
  }

  case object FujitsuFirmwareResponse extends NetworkFunction {
    val code: Byte = 0x24.toByte
  }

  case object FujitsuGroupRequest extends NetworkFunction {
    val code: Byte = 0xb8.toByte
  }

  case object FujitsuGroupResponse extends NetworkFunction {
    val code: Byte = 0xbc.toByte
  }

  case object FujitsuOemRequest extends NetworkFunction {
    val code: Byte = 0xc0.toByte
  }

  case object FujitsuOemResponse extends NetworkFunction {
    val code: Byte = 0xc4.toByte
  }

  //noinspection ScalaStyle
  def fromCode(code: Byte): NetworkFunction = code match {
    case ChassisRequest.code        => ChassisRequest
    case ChassisResponse.code       => ChassisResponse
    case BridgeRequest.code         => BridgeRequest
    case BridgeResponse.code        => BridgeResponse
    case SensorRequest.code         => SensorRequest
    case SensorResponse.code        => SensorResponse
    case ApplicationRequest.code    => ApplicationRequest
    case ApplicationResponse.code   => ApplicationResponse
    case FirmwareRequest.code       => FirmwareRequest
    case FirmwareResponse.code      => FirmwareResponse
    case StorageRequest.code        => StorageRequest
    case StorageResponse.code       => StorageResponse
    case MediaSpecificRequest.code  => MediaSpecificRequest
    case MediaSpecificResponse.code => MediaSpecificResponse

    case OemRequest.code  => OemRequest
    case OemResponse.code => OemResponse

    case OemFree30hRequest.code  => OemFree30hRequest
    case OemFree30hResponse.code => OemFree30hResponse
    case OemFree32hRequest.code  => OemFree32hRequest
    case OemFree32hResponse.code => OemFree32hResponse
    case OemFree34hRequest.code  => OemFree34hRequest
    case OemFree34hResponse.code => OemFree34hResponse
    case OemFree36hRequest.code  => OemFree36hRequest
    case OemFree36hResponse.code => OemFree36hResponse
    case OemFree38hRequest.code  => OemFree38hRequest
    case OemFree38hResponse.code => OemFree38hResponse
    case OemFree3ahRequest.code  => OemFree3ahRequest
    case OemFree3ahResponse.code => OemFree3ahResponse
    case OemFree3chRequest.code  => OemFree3chRequest
    case OemFree3chResponse.code => OemFree3chResponse
    case OemFree3e0hRequest.code => OemFree3e0hRequest
    case OemFree3ehResponse.code => OemFree3ehResponse

    case FujitsuFirmwareRequest.code  => FujitsuFirmwareRequest
    case FujitsuFirmwareResponse.code => FujitsuFirmwareResponse
    case FujitsuGroupRequest.code     => FujitsuGroupRequest
    case FujitsuGroupResponse.code    => FujitsuGroupResponse
    case FujitsuOemRequest.code       => FujitsuOemRequest
    case FujitsuOemResponse.code      => FujitsuOemResponse
  }
}
