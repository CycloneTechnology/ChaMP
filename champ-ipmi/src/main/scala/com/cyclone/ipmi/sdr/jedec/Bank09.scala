package com.cyclone.ipmi.sdr.jedec

// JEDEC Manufacturer lookup
// See STANDARD MANUFACTURERS IDENTIFICATION CODE at https://www.jedec.org/
// This code is based on document JEP106AT published Spe 2016

object Bank09 {

  val bank: PartialFunction[Byte, ManufacturerIdentificationCode] = {

    case ThreeDPlus.code                          => ThreeDPlus
    case DiehlAerospace.code                      => DiehlAerospace
    case Fairchild.code                           => Fairchild
    case MercurySystems.code                      => MercurySystems
    case SonicsInc.code                           => SonicsInc
    case GeIntelligentPlatformsGmbHandCo.code     => GeIntelligentPlatformsGmbHandCo
    case ShenzhenJingeInformationCoLtd.code       => ShenzhenJingeInformationCoLtd
    case Scww.code                                => Scww
    case SiliconMotionInc.code                    => SiliconMotionInc
    case Anurag.code                              => Anurag
    case KingKong.code                            => KingKong
    case From30CoLtd.code                         => From30CoLtd
    case GowinSemiconductorCorp.code              => GowinSemiconductorCorp
    case FremontMicroDevicesLtd.code              => FremontMicroDevicesLtd
    case EricssonModems.code                      => EricssonModems
    case Exelis.code                              => Exelis
    case SatixfyLtd.code                          => SatixfyLtd
    case GalaxyMicrosystemsLtd.code               => GalaxyMicrosystemsLtd
    case GlowayInternationalCoLtd.code            => GlowayInternationalCoLtd
    case Lab.code                                 => Lab
    case SmartEnergyInstruments.code              => SmartEnergyInstruments
    case ApprovedMemoryCorporation.code           => ApprovedMemoryCorporation
    case AxellCorporation.code                    => AxellCorporation
    case EssencoreLimited.code                    => EssencoreLimited
    case Phytium.code                             => Phytium
    case XianSinoChipSemiconductor.code           => XianSinoChipSemiconductor
    case AmbiqMicro.code                          => AmbiqMicro
    case EveRamTechnologyInc.code                 => EveRamTechnologyInc
    case Infomax.code                             => Infomax
    case ButterflyNetworkInc.code                 => ButterflyNetworkInc
    case ShenzhenCityGcaiElectronics.code         => ShenzhenCityGcaiElectronics
    case StackDevicesCorporation.code             => StackDevicesCorporation
    case AdkMediaGroup.code                       => AdkMediaGroup
    case TspGlobalCoLtd.code                      => TspGlobalCoLtd
    case HighX.code                               => HighX
    case ShenzhenElicksTechnology.code            => ShenzhenElicksTechnology
    case IssiChingis.code                         => IssiChingis
    case GoogleInc.code                           => GoogleInc
    case DasimaInternationalDevelopment.code      => DasimaInternationalDevelopment
    case LeahkinnTechnologyLimited.code           => LeahkinnTechnologyLimited
    case HimaPaulHildebrandtGmbHCoKG.code         => HimaPaulHildebrandtGmbHCoKG
    case KeysightTechnologies.code                => KeysightTechnologies
    case TechcompInternationalFastable.code       => TechcompInternationalFastable
    case AncoreTechnologyCorporation.code         => AncoreTechnologyCorporation
    case Nuvoton.code                             => Nuvoton
    case KoreaUhbeleInternationalGroupLtd.code    => KoreaUhbeleInternationalGroupLtd
    case IkegamiTsushinkiCoLtd.code               => IkegamiTsushinkiCoLtd
    case RelChipInc.code                          => RelChipInc
    case BaikalElectronics.code                   => BaikalElectronics
    case NemostechInc.code                        => NemostechInc
    case MemorysolutionGmbH.code                  => MemorysolutionGmbH
    case SiliconIntegratedSystemsCorporation.code => SiliconIntegratedSystemsCorporation
    case Xiede.code                               => Xiede
    case MultilaserComponents.code                => MultilaserComponents
    case FlashChi.code                            => FlashChi
    case Jone.code                                => Jone
    case GctSemiconductorInc.code                 => GctSemiconductorInc
    case HongKongZettaDeviceTechnology.code       => HongKongZettaDeviceTechnology
    case UnimemoryTechnologysPteLtd.code          => UnimemoryTechnologysPteLtd
    case Cuso.code                                => Cuso
    case Kuso.code                                => Kuso
    case UniquifyInc.code                         => UniquifyInc
    case SkymediCorporation.code                  => SkymediCorporation
    case CoreChanceCoLtd.code                     => CoreChanceCoLtd
    case TekismCoLtd.code                         => TekismCoLtd
    case SeagateTechnologyPlc.code                => SeagateTechnologyPlc
    case HongKongGaiaGroupCoLimited.code          => HongKongGaiaGroupCoLimited
    case GigacomSemiconductorLlc.code             => GigacomSemiconductorLlc
    case V2Technologies.code                      => V2Technologies
    case Tli.code                                 => Tli
    case Neotion.code                             => Neotion
    case Lenovo.code                              => Lenovo
    case ShenzhenZhongtengElectronicCorpLtd.code  => ShenzhenZhongtengElectronicCorpLtd
    case CompoundPhotonics.code                   => CompoundPhotonics
    case In2H2inc.code                            => In2H2inc
    case ShenzhenPangoMicrosystemsCoLtd.code      => ShenzhenPangoMicrosystemsCoLtd
    case Vasekey.code                             => Vasekey
    case CalCompIndustriaDeSemicondutores.code    => CalCompIndustriaDeSemicondutores
    case EyenixCoLtd.code                         => EyenixCoLtd
    case Heoriady.code                            => Heoriady
    case AcceleratedMemoryProductionInc.code      => AcceleratedMemoryProductionInc
    case InvecasInc.code                          => InvecasInc
    case ApMemory.code                            => ApMemory
    case DouqiTechnology.code                     => DouqiTechnology
    case EtronTechnologyInc.code                  => EtronTechnologyInc
    case IndieSemiconductor.code                  => IndieSemiconductor
    case SocionextInc.code                        => SocionextInc
    case Hgst.code                                => Hgst
    case Evga.code                                => Evga
    case AudienceInc.code                         => AudienceInc
    case EpicGear.code                            => EpicGear
    case VitesseEnterpriseCo.code                 => VitesseEnterpriseCo
    case FoxtronnInternationalCorporation.code    => FoxtronnInternationalCorporation
    case BretelonInc.code                         => BretelonInc
    case Graphcore.code                           => Graphcore
    case EoplexInc.code                           => EoplexInc
    case MaxLinearInc.code                        => MaxLinearInc
    case EtaDevices.code                          => EtaDevices
    case Loki.code                                => Loki
    case ImsElectronicsCoLtd.code                 => ImsElectronicsCoLtd
    case DosiliconCoLtd.code                      => DosiliconCoLtd
    case DolphinIntegration.code                  => DolphinIntegration
    case ShenzhenMicElectronicsTechnology.code    => ShenzhenMicElectronicsTechnology
    case BoyaMicroelectronicsInc.code             => BoyaMicroelectronicsInc
    case GeniachipRoche.code                      => GeniachipRoche
    case Axign.code                               => Axign
    case KingredElectronicTechnologyLtd.code      => KingredElectronicTechnologyLtd
    case ChaoYueZhuoComputerBusinessDept.code     => ChaoYueZhuoComputerBusinessDept
    case GuangzhouSiNuoElectronicTechnology.code  => GuangzhouSiNuoElectronicTechnology
    case CrocusTechnologyInc.code                 => CrocusTechnologyInc
    case CreativeChipsGmbH.code                   => CreativeChipsGmbH
    case GeAviationSystemsLlc.code                => GeAviationSystemsLlc
    case Asgard.code                              => Asgard
    case GoodWealthTechnologyLtd.code             => GoodWealthTechnologyLtd
    case TriCorTechnologies.code                  => TriCorTechnologies
    case NovaSystemsGmbH.code                     => NovaSystemsGmbH
    case Juhor.code                               => Juhor
    case ZhuhaiDoukeCommerceCoLtd.code            => ZhuhaiDoukeCommerceCoLtd
    case DSLMemory.code                           => DSLMemory
    case AnvoSystemsDresdenGmbH.code              => AnvoSystemsDresdenGmbH
    case Realtek.code                             => Realtek
    case AltoBeam.code                            => AltoBeam
    case WaveComputing.code                       => WaveComputing
    case BeijingTrustNetTechnologyCoLtd.code      => BeijingTrustNetTechnologyCoLtd
    case InnoviumInc.code                         => InnoviumInc
    case StarswayTechnologyLimited.code           => StarswayTechnologyLimited
  }

  case object ThreeDPlus extends ManufacturerIdentificationCode {
    val code: Byte = 0x01.toByte
    val name = "3D PLUS"
  }

  case object DiehlAerospace extends ManufacturerIdentificationCode {
    val code: Byte = 0x02.toByte
    val name = "Diehl Aerospace"
  }

  case object Fairchild extends ManufacturerIdentificationCode {
    val code: Byte = 0x83.toByte
    val name = "Fairchild"
  }

  case object MercurySystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x04.toByte
    val name = "Mercury Systems"
  }

  case object SonicsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x85.toByte
    val name = "Sonics, Inc."
  }

  case object GeIntelligentPlatformsGmbHandCo extends ManufacturerIdentificationCode {
    val code: Byte = 0x86.toByte
    val name = "GE Intelligent Platforms GmbH & Co."
  }

  case object ShenzhenJingeInformationCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x07.toByte
    val name = "Shenzhen Jinge Information Co. Ltd."
  }

  case object Scww extends ManufacturerIdentificationCode {
    val code: Byte = 0x08.toByte
    val name = "SCWW"
  }

  case object SiliconMotionInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x89.toByte
    val name = "Silicon Motion Inc."
  }

  case object Anurag extends ManufacturerIdentificationCode {
    val code: Byte = 0x8A.toByte
    val name = "Anurag"
  }

  case object KingKong extends ManufacturerIdentificationCode {
    val code: Byte = 0x0B.toByte
    val name = "King Kong"
  }

  case object From30CoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x8C.toByte
    val name = "FROM30 Co. Ltd."
  }

  case object GowinSemiconductorCorp extends ManufacturerIdentificationCode {
    val code: Byte = 0x0D.toByte
    val name = "Gowin Semiconductor Corp"
  }

  case object FremontMicroDevicesLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x0E.toByte
    val name = "Fremont Micro Devices Ltd."
  }

  case object EricssonModems extends ManufacturerIdentificationCode {
    val code: Byte = 0x8F.toByte
    val name = "Ericsson Modems"
  }

  case object Exelis extends ManufacturerIdentificationCode {
    val code: Byte = 0x10.toByte
    val name = "Exelis"
  }

  case object SatixfyLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x91.toByte
    val name = "Satixfy Ltd."
  }

  case object GalaxyMicrosystemsLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x92.toByte
    val name = "Galaxy Microsystems Ltd."
  }

  case object GlowayInternationalCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x13.toByte
    val name = "Gloway International Co. Ltd."
  }

  case object Lab extends ManufacturerIdentificationCode {
    val code: Byte = 0x94.toByte
    val name = "Lab"
  }

  case object SmartEnergyInstruments extends ManufacturerIdentificationCode {
    val code: Byte = 0x15.toByte
    val name = "Smart Energy Instruments"
  }

  case object ApprovedMemoryCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x16.toByte
    val name = "Approved Memory Corporation"
  }

  case object AxellCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x97.toByte
    val name = "Axell Corporation"
  }

  case object EssencoreLimited extends ManufacturerIdentificationCode {
    val code: Byte = 0x98.toByte
    val name = "Essencore Limited"
  }

  case object Phytium extends ManufacturerIdentificationCode {
    val code: Byte = 0x19.toByte
    val name = "Phytium"
  }

  case object XianSinoChipSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x1A.toByte
    val name = "Xi’an SinoChip Semiconductor"
  }

  case object AmbiqMicro extends ManufacturerIdentificationCode {
    val code: Byte = 0x9B.toByte
    val name = "Ambiq Micro"
  }

  case object EveRamTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x1C.toByte
    val name = "eveRAM Technology, Inc."
  }

  case object Infomax extends ManufacturerIdentificationCode {
    val code: Byte = 0x9D.toByte
    val name = "Infomax"
  }

  case object ButterflyNetworkInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x9E.toByte
    val name = "Butterfly Network, Inc."
  }

  case object ShenzhenCityGcaiElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x1F.toByte
    val name = "Shenzhen City Gcai Electronics"
  }

  case object StackDevicesCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x20.toByte
    val name = "Stack Devices Corporation"
  }

  case object AdkMediaGroup extends ManufacturerIdentificationCode {
    val code: Byte = 0xA1.toByte
    val name = ""
  }

  case object TspGlobalCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xA2.toByte
    val name = "TSP Global Co., Ltd."
  }

  case object HighX extends ManufacturerIdentificationCode {
    val code: Byte = 0x23.toByte
    val name = "HighX"
  }

  case object ShenzhenElicksTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xA4.toByte
    val name = "Shenzhen Elicks Technology"
  }

  case object IssiChingis extends ManufacturerIdentificationCode {
    val code: Byte = 0x25.toByte
    val name = "ISSI/Chingis"
  }

  case object GoogleInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x26.toByte
    val name = "Google, Inc."
  }

  case object DasimaInternationalDevelopment extends ManufacturerIdentificationCode {
    val code: Byte = 0xA7.toByte
    val name = "Dasima International Development"
  }

  case object LeahkinnTechnologyLimited extends ManufacturerIdentificationCode {
    val code: Byte = 0xA8.toByte
    val name = "Leahkinn Technology Limited"
  }

  case object HimaPaulHildebrandtGmbHCoKG extends ManufacturerIdentificationCode {
    val code: Byte = 0x29.toByte
    val name = "HIMA Paul Hildebrandt GmbH Co KG"
  }

  case object KeysightTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x2A.toByte
    val name = "Keysight Technologies"
  }

  case object TechcompInternationalFastable extends ManufacturerIdentificationCode {
    val code: Byte = 0xAB.toByte
    val name = "Techcomp International (Fastable)"
  }

  case object AncoreTechnologyCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x2C.toByte
    val name = "Ancore Technology Corporation"
  }

  case object Nuvoton extends ManufacturerIdentificationCode {
    val code: Byte = 0xAD.toByte
    val name = "Nuvoton"
  }

  case object KoreaUhbeleInternationalGroupLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xAE.toByte
    val name = "Korea Uhbele International Group Ltd."
  }

  case object IkegamiTsushinkiCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x2F.toByte
    val name = "Ikegami Tsushinki Co Ltd."
  }

  case object RelChipInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xB0.toByte
    val name = "RelChip, Inc."
  }

  case object BaikalElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x31.toByte
    val name = "Baikal Electronics"
  }

  case object NemostechInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x32.toByte
    val name = "Nemostech Inc."
  }

  case object MemorysolutionGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0xB3.toByte
    val name = "Memorysolution GmbH"
  }

  case object SiliconIntegratedSystemsCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x34.toByte
    val name = "Silicon Integrated Systems Corporation"
  }

  case object Xiede extends ManufacturerIdentificationCode {
    val code: Byte = 0xB5.toByte
    val name = "Xiede"
  }

  case object MultilaserComponents extends ManufacturerIdentificationCode {
    val code: Byte = 0xB6.toByte
    val name = "Multilaser Components"
  }

  case object FlashChi extends ManufacturerIdentificationCode {
    val code: Byte = 0x37.toByte
    val name = "Flash Chi"
  }

  case object Jone extends ManufacturerIdentificationCode {
    val code: Byte = 0x38.toByte
    val name = "Jone"
  }

  case object GctSemiconductorInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xB9.toByte
    val name = "GCT Semiconductor Inc."
  }

  case object HongKongZettaDeviceTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xBA.toByte
    val name = "Hong Kong Zetta Device Technology"
  }

  case object UnimemoryTechnologysPteLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x3B.toByte
    val name = "Unimemory Technology(s) Pte Ltd."
  }

  case object Cuso extends ManufacturerIdentificationCode {
    val code: Byte = 0xBC.toByte
    val name = "Cuso"
  }

  case object Kuso extends ManufacturerIdentificationCode {
    val code: Byte = 0x3D.toByte
    val name = "Kuso"
  }

  case object UniquifyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x3E.toByte
    val name = "Uniquify Inc."
  }

  case object SkymediCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xBF.toByte
    val name = "Skymedi Corporation"
  }

  case object CoreChanceCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x40.toByte
    val name = "Core Chance Co. Ltd."
  }

  case object TekismCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xC1.toByte
    val name = "Tekism Co. Ltd."
  }

  case object SeagateTechnologyPlc extends ManufacturerIdentificationCode {
    val code: Byte = 0xC2.toByte
    val name = "Seagate Technology PLC"
  }

  case object HongKongGaiaGroupCoLimited extends ManufacturerIdentificationCode {
    val code: Byte = 0x43.toByte
    val name = "Hong Kong Gaia Group Co. Limited"
  }

  case object GigacomSemiconductorLlc extends ManufacturerIdentificationCode {
    val code: Byte = 0xC4.toByte
    val name = "Gigacom Semiconductor LLC"
  }

  case object V2Technologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x45.toByte
    val name = "V2 Technologies"
  }

  case object Tli extends ManufacturerIdentificationCode {
    val code: Byte = 0x46.toByte
    val name = "TLi"
  }

  case object Neotion extends ManufacturerIdentificationCode {
    val code: Byte = 0xC7.toByte
    val name = "Neotion"
  }

  case object Lenovo extends ManufacturerIdentificationCode {
    val code: Byte = 0xC8.toByte
    val name = "Lenovo"
  }

  case object ShenzhenZhongtengElectronicCorpLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x49.toByte
    val name = "Shenzhen Zhongteng Electronic Corp. Ltd."
  }

  case object CompoundPhotonics extends ManufacturerIdentificationCode {
    val code: Byte = 0x4A.toByte
    val name = "Compound Photonics"
  }

  case object In2H2inc extends ManufacturerIdentificationCode {
    val code: Byte = 0xCB.toByte
    val name = "in2H2 inc"
  }

  case object ShenzhenPangoMicrosystemsCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x4C.toByte
    val name = "Shenzhen Pango Microsystems Co. Ltd"
  }

  case object Vasekey extends ManufacturerIdentificationCode {
    val code: Byte = 0xCD.toByte
    val name = "Vasekey"
  }

  case object CalCompIndustriaDeSemicondutores extends ManufacturerIdentificationCode {
    val code: Byte = 0xCE.toByte
    val name = "Cal-Comp Industria de Semicondutores"
  }

  case object EyenixCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x4F.toByte
    val name = "Eyenix Co., Ltd."
  }

  case object Heoriady extends ManufacturerIdentificationCode {
    val code: Byte = 0xD0.toByte
    val name = "Heoriady"
  }

  case object AcceleratedMemoryProductionInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x51.toByte
    val name = "Accelerated Memory Production Inc."
  }

  case object InvecasInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x52.toByte
    val name = "INVECAS, Inc."
  }

  case object ApMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0xD3.toByte
    val name = "AP Memory"
  }

  case object DouqiTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x54.toByte
    val name = "Douqi Technology"
  }

  case object EtronTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xD5.toByte
    val name = "Etron Technology, Inc."
  }

  case object IndieSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xD6.toByte
    val name = "Indie Semiconductor"
  }

  case object SocionextInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x57.toByte
    val name = "Socionext Inc."
  }

  case object Hgst extends ManufacturerIdentificationCode {
    val code: Byte = 0x58.toByte
    val name = "HGST"
  }

  case object Evga extends ManufacturerIdentificationCode {
    val code: Byte = 0xD9.toByte
    val name = "EVGA"
  }

  case object AudienceInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xDA.toByte
    val name = "Audience Inc."
  }

  case object EpicGear extends ManufacturerIdentificationCode {
    val code: Byte = 0x5B.toByte
    val name = "EpicGear"
  }

  case object VitesseEnterpriseCo extends ManufacturerIdentificationCode {
    val code: Byte = 0xDC.toByte
    val name = "Vitesse Enterprise Co."
  }

  case object FoxtronnInternationalCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x5D.toByte
    val name = "Foxtronn International Corporation"
  }

  case object BretelonInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x5E.toByte
    val name = "Bretelon Inc."
  }

  case object Graphcore extends ManufacturerIdentificationCode {
    val code: Byte = 0xDF.toByte
    val name = "Graphcore"
  }

  case object EoplexInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xE0.toByte
    val name = "Eoplex Inc"
  }

  case object MaxLinearInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x61.toByte
    val name = "MaxLinear, Inc."
  }

  case object EtaDevices extends ManufacturerIdentificationCode {
    val code: Byte = 0x62.toByte
    val name = "ETA Devices"
  }

  case object Loki extends ManufacturerIdentificationCode {
    val code: Byte = 0xE3.toByte
    val name = "LOKI"
  }

  case object ImsElectronicsCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x64.toByte
    val name = "IMS Electronics Co., Ltd."
  }

  case object DosiliconCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xE5.toByte
    val name = "Dosilicon Co., Ltd."
  }

  case object DolphinIntegration extends ManufacturerIdentificationCode {
    val code: Byte = 0xE6.toByte
    val name = "Dolphin Integration"
  }

  case object ShenzhenMicElectronicsTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x67.toByte
    val name = "Shenzhen Mic Electronics Technology"
  }

  case object BoyaMicroelectronicsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x68.toByte
    val name = "Boya Microelectronics Inc."
  }

  case object GeniachipRoche extends ManufacturerIdentificationCode {
    val code: Byte = 0xE9.toByte
    val name = "Geniachip (Roche)"
  }

  case object Axign extends ManufacturerIdentificationCode {
    val code: Byte = 0xEA.toByte
    val name = "Axign"
  }

  case object KingredElectronicTechnologyLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x6B.toByte
    val name = "Kingred Electronic Technology Ltd."
  }

  case object ChaoYueZhuoComputerBusinessDept extends ManufacturerIdentificationCode {
    val code: Byte = 0xEC.toByte
    val name = "Chao Yue Zhuo Computer Business Dept."
  }

  case object GuangzhouSiNuoElectronicTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x6D.toByte
    val name = "Guangzhou Si Nuo Electronic Technology."
  }

  case object CrocusTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x6E.toByte
    val name = ""
  }

  case object CreativeChipsGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0xEF.toByte
    val name = "Creative Chips GmbH"
  }

  case object GeAviationSystemsLlc extends ManufacturerIdentificationCode {
    val code: Byte = 0x70.toByte
    val name = "GE Aviation Systems LLC."
  }

  case object Asgard extends ManufacturerIdentificationCode {
    val code: Byte = 0xF1.toByte
    val name = "Asgard"
  }

  case object GoodWealthTechnologyLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xF2.toByte
    val name = "Good Wealth Technology Ltd."
  }

  case object TriCorTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x73.toByte
    val name = "TriCor Technologies"
  }

  case object NovaSystemsGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0xF4.toByte
    val name = "Nova-Systems GmbH"
  }

  case object Juhor extends ManufacturerIdentificationCode {
    val code: Byte = 0x75.toByte
    val name = "JUHOR"
  }

  case object ZhuhaiDoukeCommerceCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x76.toByte
    val name = "Zhuhai Douke Commerce Co. Ltd."
  }

  case object DSLMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0xF7.toByte
    val name = "DSL Memory"
  }

  case object AnvoSystemsDresdenGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0xF8.toByte
    val name = "Anvo-Systems Dresden GmbH"
  }

  case object Realtek extends ManufacturerIdentificationCode {
    val code: Byte = 0x79.toByte
    val name = "Realtek"
  }

  case object AltoBeam extends ManufacturerIdentificationCode {
    val code: Byte = 0x7A.toByte
    val name = "AltoBeam"
  }

  case object WaveComputing extends ManufacturerIdentificationCode {
    val code: Byte = 0xFB.toByte
    val name = "Wave Computing"
  }

  case object BeijingTrustNetTechnologyCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x7C.toByte
    val name = "Beijing TrustNet Technology Co. Ltd."
  }

  case object InnoviumInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xFD.toByte
    val name = "Innovium, Inc."
  }

  case object StarswayTechnologyLimited extends ManufacturerIdentificationCode {
    val code: Byte = 0xFE.toByte
    val name = "Starsway Technology Limited"
  }

}
