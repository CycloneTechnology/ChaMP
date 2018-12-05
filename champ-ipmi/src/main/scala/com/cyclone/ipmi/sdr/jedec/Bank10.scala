package com.cyclone.ipmi.sdr.jedec

import akka.util.ByteString
import com.cyclone.ipmi.codec._

// JEDEC Manufacturer lookup
// See STANDARD MANUFACTURERS IDENTIFICATION CODE at https://www.jedec.org/
// This code is based on document JEP106AT published Spe 2016

object Bank10 {

  val bank: PartialFunction[Byte, ManufacturerIdentificationCode] = {

    case WeltronicsCoLTD.code               => WeltronicsCoLTD
    case VmwareInc.code                     => VmwareInc
    case HewlettPackardEnterprise.code      => HewlettPackardEnterprise
    case Intenso.code                       => Intenso
    case PuyaSemiconductor.code             => PuyaSemiconductor
    case Memorfi.code                       => Memorfi
    case MscTechnologiesGmbh.code           => MscTechnologiesGmbh
    case Txrui.code                         => Txrui
    case SiFiveinc.code                     => SiFiveinc
    case SpreadtrumCommunications.code      => SpreadtrumCommunications
    case ParagonTechnologySchenzhenLtd.code => ParagonTechnologySchenzhenLtd
    case UmaxTechnology.code                => UmaxTechnology
    case ShenzhenYongShengTechnology.code   => ShenzhenYongShengTechnology
    case SnoamooShenzhenKaiZhuoYue.code     => SnoamooShenzhenKaiZhuoYue
    case DatenTecnologiaLtda.code           => DatenTecnologiaLtda
    case ShenzhenXinRuiYanElectronics.code  => ShenzhenXinRuiYanElectronics
    case EtaCompute.code                    => EtaCompute
    case Energous.code                      => Energous
    case RaspberryPiTradingLtd.code         => RaspberryPiTradingLtd
    case ShenzhenChixingzheTechCoLtd.code   => ShenzhenChixingzheTechCoLtd
  }

  case object WeltronicsCoLTD extends ManufacturerIdentificationCode {
    val code: Byte = 0x01.toByte
    val name = "Weltronics Co. LTD"
  }

  case object VmwareInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x02.toByte
    val name = "VMware, Inc."
  }

  case object HewlettPackardEnterprise extends ManufacturerIdentificationCode {
    val code: Byte = 0x83.toByte
    val name = "Hewlett Packard Enterprise"
  }

  case object Intenso extends ManufacturerIdentificationCode {
    val code: Byte = 0x04.toByte
    val name = "INTENSO"
  }

  case object PuyaSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x85.toByte
    val name = "Puya Semiconductor"
  }

  case object Memorfi extends ManufacturerIdentificationCode {
    val code: Byte = 0x86.toByte
    val name = "MEMORFI"
  }

  case object MscTechnologiesGmbh extends ManufacturerIdentificationCode {
    val code: Byte = 0x07.toByte
    val name = "MSC Technologies GmbH"
  }

  case object Txrui extends ManufacturerIdentificationCode {
    val code: Byte = 0x08.toByte
    val name = "Txrui"
  }

  case object SiFiveinc extends ManufacturerIdentificationCode {
    val code: Byte = 0x89.toByte
    val name = "SiFive, Inc."
  }

  case object SpreadtrumCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x8a.toByte
    val name = "Spreadtrum Communications"
  }

  case object ParagonTechnologySchenzhenLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x0b.toByte
    val name = "Paragon Technology (Shenzhen) Ltd."
  }

  case object UmaxTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x8c.toByte
    val name = "UMAX Technology"
  }

  case object ShenzhenYongShengTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x0d.toByte
    val name = "Shenzhen Yong Sheng Technology"
  }

  case object SnoamooShenzhenKaiZhuoYue extends ManufacturerIdentificationCode {
    val code: Byte = 0x0e.toByte
    val name = "SNOAMOO (Shenzhen Kai Zhuo Yue)"
  }

  case object DatenTecnologiaLtda extends ManufacturerIdentificationCode {
    val code: Byte = 0x8f.toByte
    val name = "Daten Tecnologia LTDA"
  }

  case object ShenzhenXinRuiYanElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x10.toByte
    val name = "Shenzhen XinRuiYan Electronics"
  }

  case object EtaCompute extends ManufacturerIdentificationCode {
    val code: Byte = 0x91.toByte
    val name = "Eta Compute"
  }

  case object Energous extends ManufacturerIdentificationCode {
    val code: Byte = 0x92.toByte
    val name = "Energous"
  }

  case object RaspberryPiTradingLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x13.toByte
    val name = "Raspberry Pi Trading Ltd."
  }

  case object ShenzhenChixingzheTechCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x94.toByte
    val name = "Shenzhen Chixingzhe Tech Co. Ltd."
  }

}