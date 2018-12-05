package com.cyclone.ipmi.command.oem.dell

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetSystemInfoParameters
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetSystemInfoParameters.ParameterSelector.{BlockData, LongString, ShortString}
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetSystemInfoParameters.{BlockDecoder, ParameterCodec, ParameterSelector}
import com.cyclone.ipmi.command.oem.dell.GetSystemInfoParameterSelectors.IdracInfo.AddressFormat.{IPv4, IPv6}

/**
  * Dell OEM [[GetSystemInfoParameters]] for getting an asset tag
  */
object GetSystemInfoParameterSelectors {


  case object Guid extends ParameterSelector {
    val code = 0xc3

    case class Data(value: String)

    implicit val decoder: Decoder[Data] = new Decoder[Data] {
      def decode(data: ByteString): Data = {
        val len = data(0).toUnsignedInt
        val guid = data.drop(1).take(len).map(_.toUnsignedInt)

        val guidString =
          "%02X%02X%02X%02X-%02X%02X-%02X%02X-%02X%02X-%02X%02X%02X%02X%02X%02X".format(
            guid(15), /* time low */
            guid(14),
            guid(13),
            guid(12),
            guid(11), /* time mid */
            guid(10),
            guid(9), /* time high and version */
            guid(8),
            guid(6), /* clock seq high and reserved - comes before clock seq low */
            guid(7), /* clock seq low */
            guid(5), /* node */
            guid(4),
            guid(3),
            guid(2),
            guid(1),
            guid(0)
          )

        Data(guidString)
      }
    }

    implicit val parameterCodec: ParameterCodec[Guid.type, Data, Nothing] = ParameterCodec.parameterCodecFor[Guid.type, Data]
  }

  case object AssetTag extends ParameterSelector {
    val code = 0xc4

    implicit val parameterCodec: ParameterCodec[AssetTag.type, ShortString, Nothing] = ParameterCodec.parameterCodecFor[AssetTag.type, ShortString]
  }

  case object ServiceTag extends ParameterSelector {
    val code = 0xc5

    implicit val parameterCodec: ParameterCodec[ServiceTag.type, ShortString, Nothing] = ParameterCodec.parameterCodecFor[ServiceTag.type, ShortString]
  }

  case object ChassisServiceTag extends ParameterSelector {
    val code = 0xc6

    implicit val parameterCodec: ParameterCodec[ChassisServiceTag.type, ShortString, Nothing] = ParameterCodec.parameterCodecFor[ChassisServiceTag.type, ShortString]
  }

  case object ChassisRelatedServiceTag extends ParameterSelector {
    val code = 0xc7

    implicit val parameterCodec: ParameterCodec[ChassisRelatedServiceTag.type, ShortString, Nothing] = ParameterCodec.parameterCodecFor[ChassisRelatedServiceTag.type, ShortString]
  }

  case object BoardRevision extends ParameterSelector {
    val code = 0xc8

    implicit val parameterCodec: ParameterCodec[BoardRevision.type, ShortString, Nothing] = ParameterCodec.parameterCodecFor[BoardRevision.type, ShortString]
  }

  case object PlatformModelName extends ParameterSelector {
    val code = 0xd1

    implicit val parameterCodec: ParameterCodec[PlatformModelName.type, BlockData, LongString] = ParameterCodec.blockParameterCodecFor[PlatformModelName.type, BlockData, LongString]
  }


  case object BladeSlotInfo extends ParameterSelector {
    val code = 0xdc

    implicit val parameterCodec: ParameterCodec[BladeSlotInfo.type, BlockData, LongString] = ParameterCodec.blockParameterCodecFor[BladeSlotInfo.type, BlockData, LongString]
  }

  case object iDracIpV4Url extends ParameterSelector {
    val code = 0xde

    implicit val parameterCodec: ParameterCodec[iDracIpV4Url.type, BlockData, LongString] = ParameterCodec.blockParameterCodecFor[iDracIpV4Url.type, BlockData, LongString]
  }

  case object CmcIpV4Url extends ParameterSelector {
    val code = 0xe0

    implicit val parameterCodec: ParameterCodec[CmcIpV4Url.type, BlockData, LongString] = ParameterCodec.blockParameterCodecFor[CmcIpV4Url.type, BlockData, LongString]
  }

  case object CmcIpV6Url extends ParameterSelector {
    val code = 0xf3

    implicit val parameterCodec: ParameterCodec[CmcIpV6Url.type, BlockData, LongString] = ParameterCodec.blockParameterCodecFor[CmcIpV6Url.type, BlockData, LongString]
  }

  case object IdracInfo extends ParameterSelector {
    val code = 0xdd

    sealed trait AddressFormat

    object AddressFormat {

      case object IPv4 extends AddressFormat

      case object IPv6 extends AddressFormat

      implicit val decoder: Decoder[AddressFormat] = new Decoder[AddressFormat] {
        def decode(data: ByteString): AddressFormat = data(0).toUnsignedInt match {
          case 0x00 => IPv4
          case 0x01 => IPv6
        }
      }
    }

    sealed trait IdracType

    object IdracType {

      case object `10G` extends IdracType

      case object `CMC` extends IdracType

      case object `11G Monolithic` extends IdracType

      case object `11G Modular` extends IdracType

      case object `Maser Lite` extends IdracType

      case object `12G Monolithic` extends IdracType

      case object `12G Modular` extends IdracType

      implicit val decoder: Decoder[IdracType] = new Decoder[IdracType] {
        def decode(data: ByteString): IdracType = data(0).toUnsignedInt match {
          case 0x08 => `10G`
          case 0x09 => `CMC`
          case 0x0A => `11G Monolithic`
          case 0x0B => `11G Modular`
          case 0x0D => `Maser Lite`
          case 0x10 => `12G Monolithic`
          case 0x11 => `12G Modular`
        }
      }
    }

    case class Data(
      ipAddressFormat: AddressFormat,
      static: Boolean,
      address: String,
      firmwareVersion: String,
      `type`: IdracType
    )

    implicit val decoder: BlockDecoder[Data] = new BlockDecoder[Data] {
      def decode(encoding: StringDecoder, data: ByteString): Data = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val ipAddressFormat = is.readByte.as[AddressFormat]
        val static = is.readByte.bit0

        val address = ipAddressFormat match {
          case IPv4 =>
            val bytes = is.read(4)
            is.skip(12)
            bytes.map(_.toUnsignedInt).mkString(".")
          case IPv6 => is.read(16).toHexString()
        }

        val firmwareVersion = is.read(20).as(StringDecoder.AsciiLatin)

        val tpe = is.readByte.as[IdracType]

        Data(
          ipAddressFormat = ipAddressFormat,
          static = static,
          address = address,
          firmwareVersion = firmwareVersion,
          `type` = tpe
        )

      }
    }

    implicit val parameterCodec: ParameterCodec[IdracInfo.type, BlockData, Data] = ParameterCodec.blockParameterCodecFor[IdracInfo.type, BlockData, Data]
  }

  case class MacAddresses(macAddresses: Seq[String])

  case object MacAddressesFor10G extends ParameterSelector {
    val code = 0xcb

    implicit val decoder: Decoder[MacAddresses] = new Decoder[MacAddresses] {
      def decode(data: ByteString): MacAddresses = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        /*val num = */is.readByte.toUnsignedInt

        val macAddresses = iterator.toByteString.grouped(6).map(bs => bs.toHexString()).toSeq

        MacAddresses(macAddresses = macAddresses)
      }
    }

    implicit val parameterCodec: ParameterCodec[MacAddressesFor10G.type, MacAddresses, Nothing] = ParameterCodec.parameterCodecFor[MacAddressesFor10G.type, MacAddresses]

  }

  // Note: not used with standard GetSystemInfoParameters command,
  // but a hacked dell version - see GetSystemInfoParametersEx
  case object MacAddressesFor11Or12G extends ParameterSelector {
    val code = 0xda
  }

}



