package com.cyclone.ipmi.sdr.jedec

// JEDEC Manufacturer lookup
// See STANDARD MANUFACTURERS IDENTIFICATION CODE at https://www.jedec.org/
// This code is based on document JEP106AT published Spe 2016

object Bank03 {

  val bank: PartialFunction[Byte, ManufacturerIdentificationCode] = {

    case CamintonnCorporation.code         => CamintonnCorporation
    case IsoaIncorporated.code             => IsoaIncorporated
    case AgateSemiconductor.code           => AgateSemiconductor
    case AdmtekIncorporated.code           => AdmtekIncorporated
    case Hypertec.code                     => Hypertec
    case AdhocTechnologies.code            => AdhocTechnologies
    case MosaidTechnologies.code           => MosaidTechnologies
    case ArdentTechnologies.code           => ArdentTechnologies
    case Switchcore.code                   => Switchcore
    case CiscoSystemsInc.code              => CiscoSystemsInc
    case AllayerTechnologies.code          => AllayerTechnologies
    case WorkxAgWichman.code               => WorkxAgWichman
    case OasisSemiconductor.code           => OasisSemiconductor
    case NovanetSemiconductor.code         => NovanetSemiconductor
    case EmSolutions.code                  => EmSolutions
    case PowerGeneral.code                 => PowerGeneral
    case AdvancedHardwareArch.code         => AdvancedHardwareArch
    case InovaSemiconductorsGmbH.code      => InovaSemiconductorsGmbH
    case Telocity.code                     => Telocity
    case DelkinDevices.code                => DelkinDevices
    case SymageryMicrosystems.code         => SymageryMicrosystems
    case CportCorporation.code             => CportCorporation
    case SiberCoreTechnologies.code        => SiberCoreTechnologies
    case SouthlandMicrosystems.code        => SouthlandMicrosystems
    case MalleableTechnologies.code        => MalleableTechnologies
    case KendinCommunications.code         => KendinCommunications
    case GreatTechnologyMicrocomputer.code => GreatTechnologyMicrocomputer
    case SanminaCorporation.code           => SanminaCorporation
    case HadcoCorporation.code             => HadcoCorporation
    case Corsair.code                      => Corsair
    case ActransSystemInc.code             => ActransSystemInc
    case AlphaTechnologies.code            => AlphaTechnologies
    case SiliconLaboratoriesIncCygnal.code => SiliconLaboratoriesIncCygnal
    case ArtesynTechnologies.code          => ArtesynTechnologies
    case AlignManufacturing.code           => AlignManufacturing
    case PeregrineSemiconductor.code       => PeregrineSemiconductor
    case ChameleonSystems.code             => ChameleonSystems
    case AplusFlashTechnology.code         => AplusFlashTechnology
    case MipsTechnologies.code             => MipsTechnologies
    case ChrysalisIts.code                 => ChrysalisIts
    case AdtecCorporation.code             => AdtecCorporation
    case KentronTechnologies.code          => KentronTechnologies
    case WinTechnologies.code              => WinTechnologies
    case TezzaronSemiconductor.code        => TezzaronSemiconductor
    case ExtremePacketDevices.code         => ExtremePacketDevices
    case RfMicroDevices.code               => RfMicroDevices
    case SiemensAg.code                    => SiemensAg
    case SarnoffCorporation.code           => SarnoffCorporation
    case ItautecSa.code                    => ItautecSa
    case RadiataInc.code                   => RadiataInc
    case BenchmarkElectAvex.code           => BenchmarkElectAvex
    case Legend.code                       => Legend
    case SpecTekIncorporated.code          => SpecTekIncorporated
    case HiFn.code                         => HiFn
    case EnikiaIncorporated.code           => EnikiaIncorporated
    case SwitchOnNetworks.code             => SwitchOnNetworks
    case AaNetcomIncorporated.code         => AaNetcomIncorporated
    case MicroMemoryBank.code              => MicroMemoryBank
    case EssTechnology.code                => EssTechnology
    case VirataCorporation.code            => VirataCorporation
    case ExcessBandwidth.code              => ExcessBandwidth
    case WestBaySemiconductor.code         => WestBaySemiconductor
    case DspGroup.code                     => DspGroup
    case NewportCommunications.code        => NewportCommunications
    case Chip2ChipIncorporated.code        => Chip2ChipIncorporated
    case PhobosCorporation.code            => PhobosCorporation
    case IntellitechCorporation.code       => IntellitechCorporation
    case NordicVlsiAsa.code                => NordicVlsiAsa
    case IshoniNetworks.code               => IshoniNetworks
    case SiliconSpice.code                 => SiliconSpice
    case AlchemySemiconductor.code         => AlchemySemiconductor
    case AgilentTechnologies.code          => AgilentTechnologies
    case CentilliumCommunications.code     => CentilliumCommunications
    case WlGore.code                       => WlGore
    case HanBitElectronics.code            => HanBitElectronics
    case GlobeSpan.code                    => GlobeSpan
    case Element14.code                    => Element14
    case Pycon.code                        => Pycon
    case SaifunSemiconductors.code         => SaifunSemiconductors
    case SibyteIncorporated.code           => SibyteIncorporated
    case MetaLinkTechnologies.code         => MetaLinkTechnologies
    case FeiyaTechnology.code              => FeiyaTechnology
    case IAndCTechnology.code              => IAndCTechnology
    case Shikatronics.code                 => Shikatronics
    case Elektrobit.code                   => Elektrobit
    case Megic.code                        => Megic
    case ComTier.code                      => ComTier
    case MalaysiaMicroSolutions.code       => MalaysiaMicroSolutions
    case Hyperchip.code                    => Hyperchip
    case GemstoneCommunications.code       => GemstoneCommunications
    case AnadigmAnadyne.code               => AnadigmAnadyne
    case ThreeParData.code                 => ThreeParData
    case MellanoxTechnologies.code         => MellanoxTechnologies
    case TenxTechnologies.code             => TenxTechnologies
    case HelixAg.code                      => HelixAg
    case Domosys.code                      => Domosys
    case SkyupTechnology.code              => SkyupTechnology
    case HiNtCorporation.code              => HiNtCorporation
    case Chiaro.code                       => Chiaro
    case MdtTechnologiesGmbH.code          => MdtTechnologiesGmbH
    case ExbitTechnologyAs.code            => ExbitTechnologyAs
    case IntegratedTechnologyExpress.code  => IntegratedTechnologyExpress
    case AvedMemory.code                   => AvedMemory
    case Legerity.code                     => Legerity
    case JasmineNetworks.code              => JasmineNetworks
    case CaspianNetworks.code              => CaspianNetworks
    case Ncube.code                        => Ncube
    case SiliconAccessNetworks.code        => SiliconAccessNetworks
    case FdkCorporation.code               => FdkCorporation
    case HighBandwidthAccess.code          => HighBandwidthAccess
    case MultiLinkTechnology.code          => MultiLinkTechnology
    case Brecis.code                       => Brecis
    case WorldWidePackets.code             => WorldWidePackets
    case Apw.code                          => Apw
    case ChicorySystems.code               => ChicorySystems
    case XstreamLogic.code                 => XstreamLogic
    case FastChip.code                     => FastChip
    case ZucottoWireless.code              => ZucottoWireless
    case Realchip.code                     => Realchip
    case GalaxyPower.code                  => GalaxyPower
    case Esilicon.code                     => Esilicon
    case MorphicsTechnology.code           => MorphicsTechnology
    case AccelerantNetworks.code           => AccelerantNetworks
    case SiliconWave.code                  => SiliconWave
    case SandCraft.code                    => SandCraft
    case Elpida.code                       => Elpida
  }

  case object CamintonnCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x01.toByte
    val name = "Camintonn Corporation"
  }

  case object IsoaIncorporated extends ManufacturerIdentificationCode {
    val code: Byte = 0x02.toByte
    val name = "ISOA Incorporated"
  }

  case object AgateSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x83.toByte
    val name = "Agate Semiconductor"
  }

  case object AdmtekIncorporated extends ManufacturerIdentificationCode {
    val code: Byte = 0x04.toByte
    val name = "ADMtek Incorporated"
  }

  case object Hypertec extends ManufacturerIdentificationCode {
    val code: Byte = 0x85.toByte
    val name = "HYPERTEC"
  }

  case object AdhocTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x86.toByte
    val name = "Adhoc Technologies"
  }

  case object MosaidTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x07.toByte
    val name = "MOSAID Technologies"
  }

  case object ArdentTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x08.toByte
    val name = "Ardent Technologies"
  }

  case object Switchcore extends ManufacturerIdentificationCode {
    val code: Byte = 0x89.toByte
    val name = "Switchcore"
  }

  case object CiscoSystemsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x8A.toByte
    val name = "Cisco Systems, Inc."
  }

  case object AllayerTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x0B.toByte
    val name = "Allayer Technologies"
  }

  case object WorkxAgWichman extends ManufacturerIdentificationCode {
    val code: Byte = 0x8C.toByte
    val name = "WorkX AG (Wichman)"
  }

  case object OasisSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x0D.toByte
    val name = "Oasis Semiconductor"
  }

  case object NovanetSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x0E.toByte
    val name = "Novanet Semiconductor"
  }

  case object EmSolutions extends ManufacturerIdentificationCode {
    val code: Byte = 0x8F.toByte
    val name = "E-M Solutions"
  }

  case object PowerGeneral extends ManufacturerIdentificationCode {
    val code: Byte = 0x10.toByte
    val name = "Power General"
  }

  case object AdvancedHardwareArch extends ManufacturerIdentificationCode {
    val code: Byte = 0x91.toByte
    val name = "Advanced Hardware Arch."
  }

  case object InovaSemiconductorsGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0x92.toByte
    val name = "Inova Semiconductors GmbH"
  }

  case object Telocity extends ManufacturerIdentificationCode {
    val code: Byte = 0x13.toByte
    val name = "Telocity"
  }

  case object DelkinDevices extends ManufacturerIdentificationCode {
    val code: Byte = 0x94.toByte
    val name = "Delkin Devices"
  }

  case object SymageryMicrosystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x15.toByte
    val name = "Symagery Microsystems"
  }

  case object CportCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x16.toByte
    val name = "C-Port Corporation"
  }

  case object SiberCoreTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x97.toByte
    val name = "SiberCore Technologies"
  }

  case object SouthlandMicrosystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x98.toByte
    val name = "Southland Microsystems"
  }

  case object MalleableTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x19.toByte
    val name = "Malleable Technologies"
  }

  case object KendinCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x1A.toByte
    val name = "Kendin Communications"
  }

  case object GreatTechnologyMicrocomputer extends ManufacturerIdentificationCode {
    val code: Byte = 0x9B.toByte
    val name = "Great Technology Microcomputer"
  }

  case object SanminaCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x1C.toByte
    val name = "Sanmina Corporation"
  }

  case object HadcoCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x9D.toByte
    val name = "HADCO Corporation"
  }

  case object Corsair extends ManufacturerIdentificationCode {
    val code: Byte = 0x9E.toByte
    val name = "Corsair"
  }

  case object ActransSystemInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x1F.toByte
    val name = "Actrans System Inc."
  }

  case object AlphaTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x20.toByte
    val name = "ALPHA Technologies"
  }

  case object SiliconLaboratoriesIncCygnal extends ManufacturerIdentificationCode {
    val code: Byte = 0xA1.toByte
    val name = "Silicon Laboratories, Inc. (Cygnal)"
  }

  case object ArtesynTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xA2.toByte
    val name = "Artesyn Technologies"
  }

  case object AlignManufacturing extends ManufacturerIdentificationCode {
    val code: Byte = 0x23.toByte
    val name = "Align Manufacturing"
  }

  case object PeregrineSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xA4.toByte
    val name = "Peregrine Semiconductor"
  }

  case object ChameleonSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x25.toByte
    val name = "Chameleon Systems"
  }

  case object AplusFlashTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x26.toByte
    val name = "Aplus Flash Technology"
  }

  case object MipsTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xA7.toByte
    val name = "MIPS Technologies"
  }

  case object ChrysalisIts extends ManufacturerIdentificationCode {
    val code: Byte = 0xA8.toByte
    val name = "Chrysalis ITS"
  }

  case object AdtecCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x29.toByte
    val name = "ADTEC Corporation"
  }

  case object KentronTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x2A.toByte
    val name = "Kentron Technologies"
  }

  case object WinTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xAB.toByte
    val name = "Win Technologies"
  }

  case object TezzaronSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x2C.toByte
    val name = "Tezzaron Semiconductor"
  }

  case object ExtremePacketDevices extends ManufacturerIdentificationCode {
    val code: Byte = 0xAD.toByte
    val name = "Extreme Packet Devices"
  }

  case object RfMicroDevices extends ManufacturerIdentificationCode {
    val code: Byte = 0xAE.toByte
    val name = "RF Micro Devices"
  }

  case object SiemensAg extends ManufacturerIdentificationCode {
    val code: Byte = 0x2F.toByte
    val name = "Siemens AG"
  }

  case object SarnoffCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xB0.toByte
    val name = "Sarnoff Corporation"
  }

  case object ItautecSa extends ManufacturerIdentificationCode {
    val code: Byte = 0x31.toByte
    val name = "Itautec SA"
  }

  case object RadiataInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x32.toByte
    val name = "Radiata Inc."
  }

  case object BenchmarkElectAvex extends ManufacturerIdentificationCode {
    val code: Byte = 0xB3.toByte
    val name = "Benchmark Elect. (AVEX)"
  }

  case object Legend extends ManufacturerIdentificationCode {
    val code: Byte = 0x34.toByte
    val name = "Legend"
  }

  case object SpecTekIncorporated extends ManufacturerIdentificationCode {
    val code: Byte = 0xB5.toByte
    val name = "SpecTek Incorporated"
  }

  case object HiFn extends ManufacturerIdentificationCode {
    val code: Byte = 0xB6.toByte
    val name = "Hi/fn"
  }

  case object EnikiaIncorporated extends ManufacturerIdentificationCode {
    val code: Byte = 0x37.toByte
    val name = "Enikia Incorporated"
  }

  case object SwitchOnNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x38.toByte
    val name = "SwitchOn Networks"
  }

  case object AaNetcomIncorporated extends ManufacturerIdentificationCode {
    val code: Byte = 0xB9.toByte
    val name = "AANetcom Incorporated"
  }

  case object MicroMemoryBank extends ManufacturerIdentificationCode {
    val code: Byte = 0xBA.toByte
    val name = "Micro Memory Bank"
  }

  case object EssTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x3B.toByte
    val name = "ESS Technology"
  }

  case object VirataCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xBC.toByte
    val name = "Virata Corporation"
  }

  case object ExcessBandwidth extends ManufacturerIdentificationCode {
    val code: Byte = 0x3D.toByte
    val name = "Excess Bandwidth"
  }

  case object WestBaySemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x3E.toByte
    val name = "West Bay Semiconductor"
  }

  case object DspGroup extends ManufacturerIdentificationCode {
    val code: Byte = 0xBF.toByte
    val name = "DSP Group"
  }

  case object NewportCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x40.toByte
    val name = "Newport Communications"
  }

  case object Chip2ChipIncorporated extends ManufacturerIdentificationCode {
    val code: Byte = 0xC1.toByte
    val name = "Chip2Chip Incorporated"
  }

  case object PhobosCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xC2.toByte
    val name = "Phobos Corporation"
  }

  case object IntellitechCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x43.toByte
    val name = "Intellitech Corporation"
  }

  case object NordicVlsiAsa extends ManufacturerIdentificationCode {
    val code: Byte = 0xC4.toByte
    val name = "Nordic VLSI ASA"
  }

  case object IshoniNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x45.toByte
    val name = "Ishoni Networks"
  }

  case object SiliconSpice extends ManufacturerIdentificationCode {
    val code: Byte = 0x46.toByte
    val name = "Silicon Spice"
  }

  case object AlchemySemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xC7.toByte
    val name = "Alchemy Semiconductor"
  }

  case object AgilentTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xC8.toByte
    val name = "Agilent Technologies"
  }

  case object CentilliumCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x49.toByte
    val name = "Centillium Communications"
  }

  case object WlGore extends ManufacturerIdentificationCode {
    val code: Byte = 0x4A.toByte
    val name = "W.L. Gore"
  }

  case object HanBitElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0xCB.toByte
    val name = "HanBit Electronics"
  }

  case object GlobeSpan extends ManufacturerIdentificationCode {
    val code: Byte = 0x4C.toByte
    val name = "GlobeSpan"
  }

  case object Element14 extends ManufacturerIdentificationCode {
    val code: Byte = 0xCD.toByte
    val name = "Element 14"
  }

  case object Pycon extends ManufacturerIdentificationCode {
    val code: Byte = 0xCE.toByte
    val name = "Pycon"
  }

  case object SaifunSemiconductors extends ManufacturerIdentificationCode {
    val code: Byte = 0x4F.toByte
    val name = "Saifun Semiconductors"
  }

  case object SibyteIncorporated extends ManufacturerIdentificationCode {
    val code: Byte = 0xD0.toByte
    val name = "Sibyte, Incorporated"
  }

  case object MetaLinkTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x51.toByte
    val name = "MetaLink Technologies"
  }

  case object FeiyaTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x52.toByte
    val name = "Feiya Technology"
  }

  case object IAndCTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xD3.toByte
    val name = "I & C Technology"
  }

  case object Shikatronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x54.toByte
    val name = "Shikatronics"
  }

  case object Elektrobit extends ManufacturerIdentificationCode {
    val code: Byte = 0xD5.toByte
    val name = "Elektrobit"
  }

  case object Megic extends ManufacturerIdentificationCode {
    val code: Byte = 0xD6.toByte
    val name = "Megic"
  }

  case object ComTier extends ManufacturerIdentificationCode {
    val code: Byte = 0x57.toByte
    val name = "Com-Tier"
  }

  case object MalaysiaMicroSolutions extends ManufacturerIdentificationCode {
    val code: Byte = 0x58.toByte
    val name = "Malaysia Micro Solutions"
  }

  case object Hyperchip extends ManufacturerIdentificationCode {
    val code: Byte = 0xD9.toByte
    val name = "Hyperchip"
  }

  case object GemstoneCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0xDA.toByte
    val name = "Gemstone Communications"
  }

  case object AnadigmAnadyne extends ManufacturerIdentificationCode {
    val code: Byte = 0x5B.toByte
    val name = "Anadigm (Anadyne)"
  }

  case object ThreeParData extends ManufacturerIdentificationCode {
    val code: Byte = 0xDC.toByte
    val name = "3ParData"
  }

  case object MellanoxTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x5D.toByte
    val name = "Mellanox Technologies"
  }

  case object TenxTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x5E.toByte
    val name = "Tenx Technologies"
  }

  case object HelixAg extends ManufacturerIdentificationCode {
    val code: Byte = 0xDF.toByte
    val name = "Helix AG"
  }

  case object Domosys extends ManufacturerIdentificationCode {
    val code: Byte = 0xE0.toByte
    val name = "Domosys"
  }

  case object SkyupTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x61.toByte
    val name = "Skyup Technology"
  }

  case object HiNtCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x62.toByte
    val name = "HiNT Corporation"
  }

  case object Chiaro extends ManufacturerIdentificationCode {
    val code: Byte = 0xE3.toByte
    val name = "Chiaro"
  }

  case object MdtTechnologiesGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0x64.toByte
    val name = "MDT Technologies GmbH"
  }

  case object ExbitTechnologyAs extends ManufacturerIdentificationCode {
    val code: Byte = 0xE5.toByte
    val name = "Exbit Technology A/S"
  }

  case object IntegratedTechnologyExpress extends ManufacturerIdentificationCode {
    val code: Byte = 0xE6.toByte
    val name = "Integrated Technology Express"
  }

  case object AvedMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0x67.toByte
    val name = "AVED Memory"
  }

  case object Legerity extends ManufacturerIdentificationCode {
    val code: Byte = 0x68.toByte
    val name = "Legerity"
  }

  case object JasmineNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xE9.toByte
    val name = "Jasmine Networks"
  }

  case object CaspianNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xEA.toByte
    val name = "Caspian Networks"
  }

  case object Ncube extends ManufacturerIdentificationCode {
    val code: Byte = 0x6B.toByte
    val name = "nCUBE"
  }

  case object SiliconAccessNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xEC.toByte
    val name = "Silicon Access Networks"
  }

  case object FdkCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x6D.toByte
    val name = "FDK Corporation"
  }

  case object HighBandwidthAccess extends ManufacturerIdentificationCode {
    val code: Byte = 0x6E.toByte
    val name = "High Bandwidth Access"
  }

  case object MultiLinkTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xEF.toByte
    val name = "MultiLink Technology"
  }

  case object Brecis extends ManufacturerIdentificationCode {
    val code: Byte = 0x70.toByte
    val name = "BRECIS"
  }

  case object WorldWidePackets extends ManufacturerIdentificationCode {
    val code: Byte = 0xF1.toByte
    val name = "World Wide Packets"
  }

  case object Apw extends ManufacturerIdentificationCode {
    val code: Byte = 0xF2.toByte
    val name = "APW"
  }

  case object ChicorySystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x73.toByte
    val name = "Chicory Systems"
  }

  case object XstreamLogic extends ManufacturerIdentificationCode {
    val code: Byte = 0xF4.toByte
    val name = "Xstream Logic"
  }

  case object FastChip extends ManufacturerIdentificationCode {
    val code: Byte = 0x75.toByte
    val name = "Fast-Chip"
  }

  case object ZucottoWireless extends ManufacturerIdentificationCode {
    val code: Byte = 0x76.toByte
    val name = "Zucotto Wireless"
  }

  case object Realchip extends ManufacturerIdentificationCode {
    val code: Byte = 0xF7.toByte
    val name = "Realchip"
  }

  case object GalaxyPower extends ManufacturerIdentificationCode {
    val code: Byte = 0xF8.toByte
    val name = "Galaxy Power"
  }

  case object Esilicon extends ManufacturerIdentificationCode {
    val code: Byte = 0x79.toByte
    val name = "eSilicon"
  }

  case object MorphicsTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x7A.toByte
    val name = "MorphicsTechnology"
  }

  case object AccelerantNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xFB.toByte
    val name = "Accelerant Networks"
  }

  case object SiliconWave extends ManufacturerIdentificationCode {
    val code: Byte = 0x7C.toByte
    val name = "Silicon Wave"
  }

  case object SandCraft extends ManufacturerIdentificationCode {
    val code: Byte = 0xFD.toByte
    val name = "SandCraft"
  }

  case object Elpida extends ManufacturerIdentificationCode {
    val code: Byte = 0xFE.toByte
    val name = "Elpida"
  }

}