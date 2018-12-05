package com.cyclone.ipmi.sdr.jedec

// JEDEC Manufacturer lookup
// See STANDARD MANUFACTURERS IDENTIFICATION CODE at https://www.jedec.org/
// This code is based on document JEP106AT published Spe 2016

object Bank08 {

  val bank: PartialFunction[Byte, ManufacturerIdentificationCode] = {

    case SikluCommunicationLtd.code                   => SikluCommunicationLtd
    case AForceManufacturingLtd.code                  => AForceManufacturingLtd
    case Strontium.code                               => Strontium
    case ALiCorpAbilisSystems.code                    => ALiCorpAbilisSystems
    case SigleadInc.code                              => SigleadInc
    case UbicomInc.code                               => UbicomInc
    case UnifosaCorporation.code                      => UnifosaCorporation
    case StretchInc.code                              => StretchInc
    case LantiqDeutschlandGmbH.code                   => LantiqDeutschlandGmbH
    case Visipro.code                                 => Visipro
    case EkMemory.code                                => EkMemory
    case MicroelectronicsInstituteZte.code            => MicroelectronicsInstituteZte
    case UbloxAg.code                                 => UbloxAg
    case CarryTechnologyCoLtd.code                    => CarryTechnologyCoLtd
    case Nokia.code                                   => Nokia
    case KingTigerTechnology.code                     => KingTigerTechnology
    case SierraWireless.code                          => SierraWireless
    case HtMicron.code                                => HtMicron
    case AlbatronTechnologyCoLtd.code                 => AlbatronTechnologyCoLtd
    case LeicaGeosystemsAg.code                       => LeicaGeosystemsAg
    case BroadLight.code                              => BroadLight
    case Aexea.code                                   => Aexea
    case ClariPhyCommunicationsInc.code               => ClariPhyCommunicationsInc
    case GreenPlug.code                               => GreenPlug
    case DesignArtNetworks.code                       => DesignArtNetworks
    case MachXtremeTechnologyLtd.code                 => MachXtremeTechnologyLtd
    case AtoSolutionsCoLtd.code                       => AtoSolutionsCoLtd
    case Ramsta.code                                  => Ramsta
    case GreenliantSystemsLtd.code                    => GreenliantSystemsLtd
    case Teikon.code                                  => Teikon
    case AntecHadron.code                             => AntecHadron
    case NavComTechnologyInc.code                     => NavComTechnologyInc
    case ShanghaiFudanMicroelectronics.code           => ShanghaiFudanMicroelectronics
    case CalxedaInc.code                              => CalxedaInc
    case JscEdcElectronics.code                       => JscEdcElectronics
    case KanditTechnologyCoLtd.code                   => KanditTechnologyCoLtd
    case RamosTechnology.code                         => RamosTechnology
    case GoldenmarsTechnology.code                    => GoldenmarsTechnology
    case XeLTechnologyInc.code                        => XeLTechnologyInc
    case NewzoneCorporation.code                      => NewzoneCorporation
    case ShenZhenMercyPowerTech.code                  => ShenZhenMercyPowerTech
    case NanjingYihuoTechnology.code                  => NanjingYihuoTechnology
    case NethraImagingInc.code                        => NethraImagingInc
    case SiTelSemiconductorBv.code                    => SiTelSemiconductorBv
    case SolidGearCorporation.code                    => SolidGearCorporation
    case TopowerComputerIndCoLtd.code                 => TopowerComputerIndCoLtd
    case Wilocity.code                                => Wilocity
    case ProfichipGmbH.code                           => ProfichipGmbH
    case GeradTechnologies.code                       => GeradTechnologies
    case RitekCorporation.code                        => RitekCorporation
    case GomosTechnologyLimited.code                  => GomosTechnologyLimited
    case MemorightCorporation.code                    => MemorightCorporation
    case DbroadInc.code                               => DbroadInc
    case HiSiliconTechnologies.code                   => HiSiliconTechnologies
    case SyndiantInc.code                             => SyndiantInc
    case EnvervInc.code                               => EnvervInc
    case Cognex.code                                  => Cognex
    case XinnovaTechnologyInc.code                    => XinnovaTechnologyInc
    case UltronAg.code                                => UltronAg
    case ConcordIdeaCorporation.code                  => ConcordIdeaCorporation
    case AimCorporation.code                          => AimCorporation
    case LifetimeMemoryProducts.code                  => LifetimeMemoryProducts
    case Ramsway.code                                 => Ramsway
    case RecoreSystemsBv.code                         => RecoreSystemsBv
    case HaotianJinshiboScienceTech.code              => HaotianJinshiboScienceTech
    case BeingAdvancedMemory.code                     => BeingAdvancedMemory
    case AdestoTechnologies.code                      => AdestoTechnologies
    case GiantecSemiconductorInc.code                 => GiantecSemiconductorInc
    case HmdElectronicsAg.code                        => HmdElectronicsAg
    case GlowayInternationalHk.code                   => GlowayInternationalHk
    case Kingcore.code                                => Kingcore
    case AnucellTechnologyHolding.code                => AnucellTechnologyHolding
    case AccordSoftwareAndSystemsPvtLtd.code          => AccordSoftwareAndSystemsPvtLtd
    case ActiveSemiInc.code                           => ActiveSemiInc
    case DensoCorporation.code                        => DensoCorporation
    case TlsiInc.code                                 => TlsiInc
    case Qidan.code                                   => Qidan
    case Mustang.code                                 => Mustang
    case OrcaSystems.code                             => OrcaSystems
    case PassifSemiconductor.code                     => PassifSemiconductor
    case GigaDeviceSemiconductorBeijingInc.code       => GigaDeviceSemiconductorBeijingInc
    case MemphisElectronic.code                       => MemphisElectronic
    case BeckhoffAutomationGmbH.code                  => BeckhoffAutomationGmbH
    case HarmonySemiconductorCorp.code                => HarmonySemiconductorCorp
    case AirComputersSrl.code                         => AirComputersSrl
    case TmtMemory.code                               => TmtMemory
    case EorexCorporation.code                        => EorexCorporation
    case Xingtera.code                                => Xingtera
    case Netsol.code                                  => Netsol
    case BestdonTechnologyCoLtd.code                  => BestdonTechnologyCoLtd
    case BaysandInc.code                              => BaysandInc
    case UroadTechnologyCoLtd.code                    => UroadTechnologyCoLtd
    case WilkElektronikSa.code                        => WilkElektronikSa
    case Aai.code                                     => Aai
    case Harman.code                                  => Harman
    case BergMicroelectronicsInc.code                 => BergMicroelectronicsInc
    case AssiaInc.code                                => AssiaInc
    case VisiontekProductsLlc.code                    => VisiontekProductsLlc
    case OcMemory.code                                => OcMemory
    case WelinkSolutionInc.code                       => WelinkSolutionInc
    case SharkGaming.code                             => SharkGaming
    case AvalancheTechnology.code                     => AvalancheTechnology
    case RandDCenterElveesOjsc.code                   => RandDCenterElveesOjsc
    case KingboMarsTechnologyCoLtd.code               => KingboMarsTechnologyCoLtd
    case HighBridgeSolutionsIndustriaElectronica.code => HighBridgeSolutionsIndustriaElectronica
    case TranscendTechnologyCoLtd.code                => TranscendTechnologyCoLtd
    case EverspinTechnologies.code                    => EverspinTechnologies
    case HonHaiPrecision.code                         => HonHaiPrecision
    case SmartStorageSystems.code                     => SmartStorageSystems
    case ToumazGroup.code                             => ToumazGroup
    case ZentelElectronicsCorporation.code            => ZentelElectronicsCorporation
    case PanramInternationalCorporation.code          => PanramInternationalCorporation
    case SiliconSpaceTechnology.code                  => SiliconSpaceTechnology
    case LiteOnItCorporation.code                     => LiteOnItCorporation
    case Inuitive.code                                => Inuitive
    case HMicro.code                                  => HMicro
    case BittWareInc.code                             => BittWareInc
    case GlobalFoundries.code                         => GlobalFoundries
    case AcpiDigitalCoLtd.code                        => AcpiDigitalCoLtd
    case AnnapurnaLabs.code                           => AnnapurnaLabs
    case AcSiPTechnologyCorporation.code              => AcSiPTechnologyCorporation
    case IdeaElectronicSystems.code                   => IdeaElectronicSystems
    case GoweTechnologyCoLtd.code                     => GoweTechnologyCoLtd
    case HermesTestingSolutionsInc.code               => HermesTestingSolutionsInc
    case PositivoBgh.code                             => PositivoBgh
    case IntelligenceSiliconTechnology.code           => IntelligenceSiliconTechnology
  }

  case object SikluCommunicationLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x01.toByte
    val name = "Siklu Communication Ltd."
  }

  case object AForceManufacturingLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x02.toByte
    val name = "A Force Manufacturing Ltd."
  }

  case object Strontium extends ManufacturerIdentificationCode {
    val code: Byte = 0x83.toByte
    val name = "Strontium"
  }

  case object ALiCorpAbilisSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x04.toByte
    val name = "ALi Corp (Abilis Systems)"
  }

  case object SigleadInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x85.toByte
    val name = "Siglead, Inc."
  }

  case object UbicomInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x86.toByte
    val name = "Ubicom, Inc."
  }

  case object UnifosaCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x07.toByte
    val name = "Unifosa Corporation"
  }

  case object StretchInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x08.toByte
    val name = "Stretch, Inc."
  }

  case object LantiqDeutschlandGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0x89.toByte
    val name = "Lantiq Deutschland GmbH"
  }

  case object Visipro extends ManufacturerIdentificationCode {
    val code: Byte = 0x8A.toByte
    val name = "Visipro."
  }

  case object EkMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0x0B.toByte
    val name = "EKMemory"
  }

  case object MicroelectronicsInstituteZte extends ManufacturerIdentificationCode {
    val code: Byte = 0x8C.toByte
    val name = "Microelectronics Institute ZTE"
  }

  case object UbloxAg extends ManufacturerIdentificationCode {
    val code: Byte = 0x0D.toByte
    val name = "u-blox AG"
  }

  case object CarryTechnologyCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x0E.toByte
    val name = "Carry Technology Co. Ltd."
  }

  case object Nokia extends ManufacturerIdentificationCode {
    val code: Byte = 0x8F.toByte
    val name = "Nokia"
  }

  case object KingTigerTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x10.toByte
    val name = "King Tiger Technology"
  }

  case object SierraWireless extends ManufacturerIdentificationCode {
    val code: Byte = 0x91.toByte
    val name = "Sierra Wireless"
  }

  case object HtMicron extends ManufacturerIdentificationCode {
    val code: Byte = 0x92.toByte
    val name = "HT Micron"
  }

  case object AlbatronTechnologyCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x13.toByte
    val name = "Albatron Technology Co. Ltd."
  }

  case object LeicaGeosystemsAg extends ManufacturerIdentificationCode {
    val code: Byte = 0x94.toByte
    val name = "Leica Geosystems AG"
  }

  case object BroadLight extends ManufacturerIdentificationCode {
    val code: Byte = 0x15.toByte
    val name = "BroadLight"
  }

  case object Aexea extends ManufacturerIdentificationCode {
    val code: Byte = 0x16.toByte
    val name = "AEXEA"
  }

  case object ClariPhyCommunicationsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x97.toByte
    val name = "ClariPhy Communications, Inc."
  }

  case object GreenPlug extends ManufacturerIdentificationCode {
    val code: Byte = 0x98.toByte
    val name = "Green Plug"
  }

  case object DesignArtNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x19.toByte
    val name = "Design Art Networks"
  }

  case object MachXtremeTechnologyLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x1A.toByte
    val name = "Mach Xtreme Technology Ltd."
  }

  case object AtoSolutionsCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x9B.toByte
    val name = "ATO Solutions Co. Ltd."
  }

  case object Ramsta extends ManufacturerIdentificationCode {
    val code: Byte = 0x1C.toByte
    val name = "Ramsta"
  }

  case object GreenliantSystemsLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x9D.toByte
    val name = "Greenliant Systems, Ltd."
  }

  case object Teikon extends ManufacturerIdentificationCode {
    val code: Byte = 0x9E.toByte
    val name = "Teikon"
  }

  case object AntecHadron extends ManufacturerIdentificationCode {
    val code: Byte = 0x1F.toByte
    val name = "Antec Hadron"
  }

  case object NavComTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x20.toByte
    val name = "NavCom Technology, Inc."
  }

  case object ShanghaiFudanMicroelectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0xA1.toByte
    val name = "Shanghai Fudan Microelectronics"
  }

  case object CalxedaInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xA2.toByte
    val name = "Calxeda, Inc."
  }

  case object JscEdcElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x23.toByte
    val name = "JSC EDC Electronics"
  }

  case object KanditTechnologyCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xA4.toByte
    val name = "Kandit Technology Co. Ltd."
  }

  case object RamosTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x25.toByte
    val name = "Ramos Technology"
  }

  case object GoldenmarsTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x26.toByte
    val name = "Goldenmars Technology"
  }

  case object XeLTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xA7.toByte
    val name = "XeL Technology Inc."
  }

  case object NewzoneCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xA8.toByte
    val name = "Newzone Corporation"
  }

  case object ShenZhenMercyPowerTech extends ManufacturerIdentificationCode {
    val code: Byte = 0x29.toByte
    val name = "ShenZhen MercyPower Tech"
  }

  case object NanjingYihuoTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x2A.toByte
    val name = "Nanjing Yihuo Technology"
  }

  case object NethraImagingInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xAB.toByte
    val name = "Nethra Imaging Inc."
  }

  case object SiTelSemiconductorBv extends ManufacturerIdentificationCode {
    val code: Byte = 0x2C.toByte
    val name = "SiTel Semiconductor BV"
  }

  case object SolidGearCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xAD.toByte
    val name = "SolidGear Corporation"
  }

  case object TopowerComputerIndCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xAE.toByte
    val name = "Topower Computer Ind Co Ltd."
  }

  case object Wilocity extends ManufacturerIdentificationCode {
    val code: Byte = 0x2F.toByte
    val name = "Wilocity"
  }

  case object ProfichipGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0xB0.toByte
    val name = "Profichip GmbH"
  }

  case object GeradTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x31.toByte
    val name = "Gerad Technologies"
  }

  case object RitekCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x32.toByte
    val name = "Ritek Corporation"
  }

  case object GomosTechnologyLimited extends ManufacturerIdentificationCode {
    val code: Byte = 0xB3.toByte
    val name = "Gomos Technology Limited"
  }

  case object MemorightCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x34.toByte
    val name = "Memoright Corporation"
  }

  case object DbroadInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xB5.toByte
    val name = "D-Broad, Inc."
  }

  case object HiSiliconTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xB6.toByte
    val name = "HiSilicon Technologies"
  }

  case object SyndiantInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x37.toByte
    val name = "Syndiant Inc."
  }

  case object EnvervInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x38.toByte
    val name = "Enverv Inc."
  }

  case object Cognex extends ManufacturerIdentificationCode {
    val code: Byte = 0xB9.toByte
    val name = "Cognex"
  }

  case object XinnovaTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xBA.toByte
    val name = "Xinnova Technology Inc."
  }

  case object UltronAg extends ManufacturerIdentificationCode {
    val code: Byte = 0x3B.toByte
    val name = "Ultron AG"
  }

  case object ConcordIdeaCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xBC.toByte
    val name = "Concord Idea Corporation"
  }

  case object AimCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x3D.toByte
    val name = "AIM Corporation"
  }

  case object LifetimeMemoryProducts extends ManufacturerIdentificationCode {
    val code: Byte = 0x3E.toByte
    val name = "Lifetime Memory Products"
  }

  case object Ramsway extends ManufacturerIdentificationCode {
    val code: Byte = 0xBF.toByte
    val name = "Ramsway"
  }

  case object RecoreSystemsBv extends ManufacturerIdentificationCode {
    val code: Byte = 0x40.toByte
    val name = "Recore Systems B.V."
  }

  case object HaotianJinshiboScienceTech extends ManufacturerIdentificationCode {
    val code: Byte = 0xC1.toByte
    val name = "Haotian Jinshibo Science Tech"
  }

  case object BeingAdvancedMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0xC2.toByte
    val name = "Being Advanced Memory"
  }

  case object AdestoTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x43.toByte
    val name = "Adesto Technologies"
  }

  case object GiantecSemiconductorInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xC4.toByte
    val name = "Giantec Semiconductor, Inc."
  }

  case object HmdElectronicsAg extends ManufacturerIdentificationCode {
    val code: Byte = 0x45.toByte
    val name = "HMD Electronics AG"
  }

  case object GlowayInternationalHk extends ManufacturerIdentificationCode {
    val code: Byte = 0x46.toByte
    val name = "Gloway International (HK)"
  }

  case object Kingcore extends ManufacturerIdentificationCode {
    val code: Byte = 0xC7.toByte
    val name = "Kingcore"
  }

  case object AnucellTechnologyHolding extends ManufacturerIdentificationCode {
    val code: Byte = 0xC8.toByte
    val name = "Anucell Technology Holding"
  }

  case object AccordSoftwareAndSystemsPvtLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x49.toByte
    val name = "Accord Software & Systems Pvt. Ltd."
  }

  case object ActiveSemiInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x4A.toByte
    val name = "Active-Semi Inc."
  }

  case object DensoCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xCB.toByte
    val name = "Denso Corporation"
  }

  case object TlsiInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x4C.toByte
    val name = "TLSI Inc."
  }

  case object Qidan extends ManufacturerIdentificationCode {
    val code: Byte = 0xCD.toByte
    val name = "Qidan"
  }

  case object Mustang extends ManufacturerIdentificationCode {
    val code: Byte = 0xCE.toByte
    val name = "Mustang"
  }

  case object OrcaSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x4F.toByte
    val name = "Orca Systems"
  }

  case object PassifSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xD0.toByte
    val name = "Passif Semiconductor"
  }

  case object GigaDeviceSemiconductorBeijingInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x51.toByte
    val name = "GigaDevice Semiconductor (Beijing) Inc."
  }

  case object MemphisElectronic extends ManufacturerIdentificationCode {
    val code: Byte = 0x52.toByte
    val name = "Memphis Electronic"
  }

  case object BeckhoffAutomationGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0xD3.toByte
    val name = "Beckhoff Automation GmbH"
  }

  case object HarmonySemiconductorCorp extends ManufacturerIdentificationCode {
    val code: Byte = 0x54.toByte
    val name = "Harmony Semiconductor Corp"
  }

  case object AirComputersSrl extends ManufacturerIdentificationCode {
    val code: Byte = 0xD5.toByte
    val name = "Air Computers SRL"
  }

  case object TmtMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0xD6.toByte
    val name = "TMT Memory"
  }

  case object EorexCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x57.toByte
    val name = "Eorex Corporation"
  }

  case object Xingtera extends ManufacturerIdentificationCode {
    val code: Byte = 0x58.toByte
    val name = "Xingtera"
  }

  case object Netsol extends ManufacturerIdentificationCode {
    val code: Byte = 0xD9.toByte
    val name = "Netsol"
  }

  case object BestdonTechnologyCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xDA.toByte
    val name = "Bestdon Technology Co. Ltd."
  }

  case object BaysandInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x5B.toByte
    val name = "Baysand Inc."
  }

  case object UroadTechnologyCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xDC.toByte
    val name = "Uroad Technology Co. Ltd."
  }

  case object WilkElektronikSa extends ManufacturerIdentificationCode {
    val code: Byte = 0x5D.toByte
    val name = "Wilk Elektronik S.A."
  }

  case object Aai extends ManufacturerIdentificationCode {
    val code: Byte = 0x5E.toByte
    val name = "AAI"
  }

  case object Harman extends ManufacturerIdentificationCode {
    val code: Byte = 0xDF.toByte
    val name = "Harman"
  }

  case object BergMicroelectronicsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xE0.toByte
    val name = "Berg Microelectronics Inc."
  }

  case object AssiaInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x61.toByte
    val name = "ASSIA, Inc."
  }

  case object VisiontekProductsLlc extends ManufacturerIdentificationCode {
    val code: Byte = 0x62.toByte
    val name = "Visiontek Products LLC"
  }

  case object OcMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0xE3.toByte
    val name = "OCMEMORY"
  }

  case object WelinkSolutionInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x64.toByte
    val name = "Welink Solution Inc."
  }

  case object SharkGaming extends ManufacturerIdentificationCode {
    val code: Byte = 0xE5.toByte
    val name = "Shark Gaming"
  }

  case object AvalancheTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xE6.toByte
    val name = "Avalanche Technology"
  }

  case object RandDCenterElveesOjsc extends ManufacturerIdentificationCode {
    val code: Byte = 0x67.toByte
    val name = "R&D Center ELVEES OJSC"
  }

  case object KingboMarsTechnologyCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x68.toByte
    val name = "KingboMars Technology Co. Ltd."
  }

  case object HighBridgeSolutionsIndustriaElectronica extends ManufacturerIdentificationCode {
    val code: Byte = 0xE9.toByte
    val name = "High Bridge Solutions Industria Electronica"
  }

  case object TranscendTechnologyCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xEA.toByte
    val name = "Transcend Technology Co. Ltd."
  }

  case object EverspinTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x6B.toByte
    val name = "Everspin Technologies"
  }

  case object HonHaiPrecision extends ManufacturerIdentificationCode {
    val code: Byte = 0xEC.toByte
    val name = "Hon-Hai Precision"
  }

  case object SmartStorageSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x6D.toByte
    val name = "Smart Storage Systems"
  }

  case object ToumazGroup extends ManufacturerIdentificationCode {
    val code: Byte = 0x6E.toByte
    val name = "Toumaz Group"
  }

  case object ZentelElectronicsCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xEF.toByte
    val name = "Zentel Electronics Corporation"
  }

  case object PanramInternationalCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x70.toByte
    val name = "Panram International Corporation"
  }

  case object SiliconSpaceTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xF1.toByte
    val name = "Silicon Space Technology"
  }

  case object LiteOnItCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xF2.toByte
    val name = "LITE-ON IT Corporation"
  }

  case object Inuitive extends ManufacturerIdentificationCode {
    val code: Byte = 0x73.toByte
    val name = "Inuitive"
  }

  case object HMicro extends ManufacturerIdentificationCode {
    val code: Byte = 0xF4.toByte
    val name = "HMicro"
  }

  case object BittWareInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x75.toByte
    val name = "BittWare, Inc."
  }

  case object GlobalFoundries extends ManufacturerIdentificationCode {
    val code: Byte = 0x76.toByte
    val name = "GLOBALFOUNDRIES"
  }

  case object AcpiDigitalCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xF7.toByte
    val name = "ACPI Digital Co. Ltd."
  }

  case object AnnapurnaLabs extends ManufacturerIdentificationCode {
    val code: Byte = 0xF8.toByte
    val name = "Annapurna Labs"
  }

  case object AcSiPTechnologyCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x79.toByte
    val name = "AcSiP Technology Corporation"
  }

  case object IdeaElectronicSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x7A.toByte
    val name = "Idea! Electronic Systems"
  }

  case object GoweTechnologyCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xFB.toByte
    val name = "Gowe Technology Co. Ltd."
  }

  case object HermesTestingSolutionsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x7C.toByte
    val name = "Hermes Testing Solutions, Inc."
  }

  case object PositivoBgh extends ManufacturerIdentificationCode {
    val code: Byte = 0xFD.toByte
    val name = "Positivo BGH"
  }

  case object IntelligenceSiliconTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xFE.toByte
    val name = "Intelligence  Silicon Technology"
  }

}