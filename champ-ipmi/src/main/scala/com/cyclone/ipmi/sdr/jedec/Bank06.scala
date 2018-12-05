package com.cyclone.ipmi.sdr.jedec

// JEDEC Manufacturer lookup
// See STANDARD MANUFACTURERS IDENTIFICATION CODE at https://www.jedec.org/
// This code is based on document JEP106AT published Spe 2016

object Bank06 {

  val bank: PartialFunction[Byte, ManufacturerIdentificationCode] = {

    case SpecularNetworks.code              => SpecularNetworks
    case PatriotMemoryPdpSystems.code       => PatriotMemoryPdpSystems
    case UchipTechnologyCorp.code           => UchipTechnologyCorp
    case SiliconOptix.code                  => SiliconOptix
    case GreenfieldNetworks.code            => GreenfieldNetworks
    case CompuRamGmbH.code                  => CompuRamGmbH
    case StargenInc.code                    => StargenInc
    case NetCellCorporation.code            => NetCellCorporation
    case ExcalibrusTechnologiesLtd.code     => ExcalibrusTechnologiesLtd
    case ScmMicrosystems.code               => ScmMicrosystems
    case XsigoSystemsInc.code               => XsigoSystemsInc
    case ChipsAndSystemsInc.code            => ChipsAndSystemsInc
    case Tier1MultichipSolutions.code       => Tier1MultichipSolutions
    case CwrlLabs.code                      => CwrlLabs
    case Teradici.code                      => Teradici
    case GigaramInc.code                    => GigaramInc
    case G2Microsystems.code                => G2Microsystems
    case PowerFlashSemiconductor.code       => PowerFlashSemiconductor
    case PaSemiInc.code                     => PaSemiInc
    case NovaTechSolutionsSa.code           => NovaTechSolutionsSa
    case C2MicrosystemsInc.code             => C2MicrosystemsInc
    case Level5Networks.code                => Level5Networks
    case CosMemoryAg.code                   => CosMemoryAg
    case InnovasicSemiconductor.code        => InnovasicSemiconductor
    case Zero2IcCoLtd.code                  => Zero2IcCoLtd
    case TabulaInc.code                     => TabulaInc
    case CrucialTechnology.code             => CrucialTechnology
    case ChelsioCommunications.code         => ChelsioCommunications
    case SolarflareCommunications.code      => SolarflareCommunications
    case XambalaInc.code                    => XambalaInc
    case EadsAstrium.code                   => EadsAstrium
    case TerraSemiconductorInc.code         => TerraSemiconductorInc
    case ImagingWorksInc.code               => ImagingWorksInc
    case AstuteNetworksInc.code             => AstuteNetworksInc
    case Tzero.code                         => Tzero
    case Emulex.code                        => Emulex
    case PowerOne.code                      => PowerOne
    case PulseLinkInc.code                  => PulseLinkInc
    case HonHaiPrecisionIndustry.code       => HonHaiPrecisionIndustry
    case WhiteRockNetworksInc.code          => WhiteRockNetworksInc
    case TelegentSystemsUSAInc.code         => TelegentSystemsUSAInc
    case AtruaTechnologiesInc.code          => AtruaTechnologiesInc
    case AcbelPolytechInc.code              => AcbelPolytechInc
    case ErideInc.code                      => ErideInc
    case ULiElectronicsInc.code             => ULiElectronicsInc
    case MagnumSemiconductorInc.code        => MagnumSemiconductorInc
    case NeoOneTechnologyInc.code           => NeoOneTechnologyInc
    case ConnexTechnologyInc.code           => ConnexTechnologyInc
    case StreamProcessorsInc.code           => StreamProcessorsInc
    case FocusEnhancements.code             => FocusEnhancements
    case TelecisWirelessInc.code            => TelecisWirelessInc
    case UnavMicroelectronics.code          => UnavMicroelectronics
    case TarariInc.code                     => TarariInc
    case AmbricInc.code                     => AmbricInc
    case NewportMediaInc.code               => NewportMediaInc
    case Vmts.code                          => Vmts
    case EnucliaSemiconductorInc.code       => EnucliaSemiconductorInc
    case VirtiumTechnologyInc.code          => VirtiumTechnologyInc
    case SolidStateSystemCoLtd.code         => SolidStateSystemCoLtd
    case KianTechLlc.code                   => KianTechLlc
    case Artimi.code                        => Artimi
    case PowerQuotientInternational.code    => PowerQuotientInternational
    case AvagoTechnologies.code             => AvagoTechnologies
    case AdTechnology.code                  => AdTechnology
    case SigmaDesigns.code                  => SigmaDesigns
    case SiCortexInc.code                   => SiCortexInc
    case VenturaTechnologyGroup.code        => VenturaTechnologyGroup
    case Easic.code                         => Easic
    case MhsSas.code                        => MhsSas
    case MicroStarInternational.code        => MicroStarInternational
    case RapportInc.code                    => RapportInc
    case MakwayInternational.code           => MakwayInternational
    case BroadReachEngineeringCo.code       => BroadReachEngineeringCo
    case SemiconductorMfgIntlCorp.code      => SemiconductorMfgIntlCorp
    case SiConnect.code                     => SiConnect
    case FciUsaInc.code                     => FciUsaInc
    case ValiditySensors.code               => ValiditySensors
    case ConeyTechnologyCoLtd.code          => ConeyTechnologyCoLtd
    case SpansLogic.code                    => SpansLogic
    case NeterionInc.code                   => NeterionInc
    case Qimonda.code                       => Qimonda
    case NewJapanRadioCoLtd.code            => NewJapanRadioCoLtd
    case Velogix.code                       => Velogix
    case MontalvoSystems.code               => MontalvoSystems
    case iVivityInc.code                    => iVivityInc
    case WaltonChaintech.code               => WaltonChaintech
    case Aeneon.code                        => Aeneon
    case LoromIndustrialCoLtd.code          => LoromIndustrialCoLtd
    case RadiospireNetworks.code            => RadiospireNetworks
    case SensioTechnologiesInc.code         => SensioTechnologiesInc
    case NethraImaging.code                 => NethraImaging
    case HexonTechnologyPteLtd.code         => HexonTechnologyPteLtd
    case CompuStocxCsx.code                 => CompuStocxCsx
    case MethodeElectronicsInc.code         => MethodeElectronicsInc
    case ConnectOneLtd.code                 => ConnectOneLtd
    case OpulanTechnologies.code            => OpulanTechnologies
    case SeptentrioNv.code                  => SeptentrioNv
    case GoldenmarsTechnologyInc.code       => GoldenmarsTechnologyInc
    case KretonCorporation.code             => KretonCorporation
    case CochlearLtd.code                   => CochlearLtd
    case AltairSemiconductor.code           => AltairSemiconductor
    case NetEffectInc.code                  => NetEffectInc
    case SpansionInc.code                   => SpansionInc
    case TaiwanSemiconductorMfg.code        => TaiwanSemiconductorMfg
    case EmphanySystemsInc.code             => EmphanySystemsInc
    case ApaceWaveTechnologies.code         => ApaceWaveTechnologies
    case MobilygenCorporation.code          => MobilygenCorporation
    case Tego.code                          => Tego
    case CswitchCorporation.code            => CswitchCorporation
    case HaierBeijingIcDesignCo.code        => HaierBeijingIcDesignCo
    case MetaRam.code                       => MetaRam
    case AxelElectronicsCoLtd.code          => AxelElectronicsCoLtd
    case TileraCorporation.code             => TileraCorporation
    case Aquantia.code                      => Aquantia
    case VivaceSemiconductor.code           => VivaceSemiconductor
    case RedpineSignals.code                => RedpineSignals
    case Octalica.code                      => Octalica
    case InterDigitalCommunications.code    => InterDigitalCommunications
    case AvantTechnology.code               => AvantTechnology
    case AsrockInc.code                     => AsrockInc
    case Availink.code                      => Availink
    case QuarticsInc.code                   => QuarticsInc
    case ElementCxi.code                    => ElementCxi
    case InnovacionesMicroelectronicas.code => InnovacionesMicroelectronicas
    case VeriSiliconMicroelectronics.code   => VeriSiliconMicroelectronics
    case W5Networks.code                    => W5Networks
  }

  case object SpecularNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x01.toByte
    val name = "Networks"
  }

  case object PatriotMemoryPdpSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x02.toByte
    val name = "Patriot Memory (PDP Systems)"
  }

  case object UchipTechnologyCorp extends ManufacturerIdentificationCode {
    val code: Byte = 0x83.toByte
    val name = "U-Chip Technology Corp."
  }

  case object SiliconOptix extends ManufacturerIdentificationCode {
    val code: Byte = 0x04.toByte
    val name = "Silicon Optix"
  }

  case object GreenfieldNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x85.toByte
    val name = "Greenfield Networks"
  }

  case object CompuRamGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0x86.toByte
    val name = "CompuRAM GmbH"
  }

  case object StargenInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x07.toByte
    val name = "Stargen, Inc."
  }

  case object NetCellCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x08.toByte
    val name = "NetCell Corporation"
  }

  case object ExcalibrusTechnologiesLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x89.toByte
    val name = "Excalibrus Technologies Ltd"
  }

  case object ScmMicrosystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x8A.toByte
    val name = "SCM Microsystems"
  }

  case object XsigoSystemsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x0B.toByte
    val name = "Xsigo Systems, Inc."
  }

  case object ChipsAndSystemsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x8C.toByte
    val name = ""
  }

  case object Tier1MultichipSolutions extends ManufacturerIdentificationCode {
    val code: Byte = 0x0D.toByte
    val name = "Tier 1 Multichip Solutions"
  }

  case object CwrlLabs extends ManufacturerIdentificationCode {
    val code: Byte = 0x0E.toByte
    val name = "CWRL Labs"
  }

  case object Teradici extends ManufacturerIdentificationCode {
    val code: Byte = 0x8F.toByte
    val name = "Teradici"
  }

  case object GigaramInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x10.toByte
    val name = "Gigaram, Inc."
  }

  case object G2Microsystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x91.toByte
    val name = "g2 Microsystems"
  }

  case object PowerFlashSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x92.toByte
    val name = "PowerFlash Semiconductor"
  }

  case object PaSemiInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x13.toByte
    val name = "P.A. Semi, Inc."
  }

  case object NovaTechSolutionsSa extends ManufacturerIdentificationCode {
    val code: Byte = 0x94.toByte
    val name = "NovaTech Solutions, S.A."
  }

  case object C2MicrosystemsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x15.toByte
    val name = "c2 Microsystems, Inc."
  }

  case object Level5Networks extends ManufacturerIdentificationCode {
    val code: Byte = 0x16.toByte
    val name = "Level5 Networks"
  }

  case object CosMemoryAg extends ManufacturerIdentificationCode {
    val code: Byte = 0x97.toByte
    val name = "COS Memory AG"
  }

  case object InnovasicSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x98.toByte
    val name = "Innovasic Semiconductor"
  }

  case object Zero2IcCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x19.toByte
    val name = "02IC Co. Ltd"
  }

  case object TabulaInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x1A.toByte
    val name = "Tabula, Inc."
  }

  case object CrucialTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x9B.toByte
    val name = "Crucial Technology"
  }

  case object ChelsioCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x1C.toByte
    val name = "Chelsio Communications"
  }

  case object SolarflareCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x9D.toByte
    val name = "Solarflare Communications"
  }

  case object XambalaInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x9E.toByte
    val name = "Xambala Inc."
  }

  case object EadsAstrium extends ManufacturerIdentificationCode {
    val code: Byte = 0x1F.toByte
    val name = "EADS Astrium"
  }

  case object TerraSemiconductorInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x20.toByte
    val name = "Terra Semiconductor, Inc."
  }

  case object ImagingWorksInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xA1.toByte
    val name = "Imaging Works, Inc."
  }

  case object AstuteNetworksInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xA2.toByte
    val name = "Astute Networks, Inc."
  }

  case object Tzero extends ManufacturerIdentificationCode {
    val code: Byte = 0x23.toByte
    val name = "Tzero"
  }

  case object Emulex extends ManufacturerIdentificationCode {
    val code: Byte = 0xA4.toByte
    val name = "Emulex"
  }

  case object PowerOne extends ManufacturerIdentificationCode {
    val code: Byte = 0x25.toByte
    val name = "Power-One"
  }

  case object PulseLinkInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x26.toByte
    val name = "Pulse~LINK Inc."
  }

  case object HonHaiPrecisionIndustry extends ManufacturerIdentificationCode {
    val code: Byte = 0xA7.toByte
    val name = "Hon Hai Precision Industry"
  }

  case object WhiteRockNetworksInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xA8.toByte
    val name = "White Rock Networks Inc."
  }

  case object TelegentSystemsUSAInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x29.toByte
    val name = "Telegent Systems USA, Inc."
  }

  case object AtruaTechnologiesInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x2A.toByte
    val name = "Atrua Technologies, Inc."
  }

  case object AcbelPolytechInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xAB.toByte
    val name = "Acbel Polytech Inc."
  }

  case object ErideInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x2C.toByte
    val name = "eRide Inc."
  }

  case object ULiElectronicsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xAD.toByte
    val name = "ULi Electronics Inc."
  }

  case object MagnumSemiconductorInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xAE.toByte
    val name = "Magnum Semiconductor Inc."
  }

  case object NeoOneTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x2F.toByte
    val name = "neoOne Technology, Inc."
  }

  case object ConnexTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xB0.toByte
    val name = "Connex Technology, Inc."
  }

  case object StreamProcessorsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x31.toByte
    val name = "Stream Processors, Inc."
  }

  case object FocusEnhancements extends ManufacturerIdentificationCode {
    val code: Byte = 0x32.toByte
    val name = "Focus Enhancements"
  }

  case object TelecisWirelessInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xB3.toByte
    val name = "Telecis Wireless, Inc."
  }

  case object UnavMicroelectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x34.toByte
    val name = "uNav Microelectronics"
  }

  case object TarariInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xB5.toByte
    val name = "Tarari, Inc."
  }

  case object AmbricInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xB6.toByte
    val name = "Ambric, Inc."
  }

  case object NewportMediaInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x37.toByte
    val name = "Newport Media, Inc."
  }

  case object Vmts extends ManufacturerIdentificationCode {
    val code: Byte = 0x38.toByte
    val name = "VMTS"
  }

  case object EnucliaSemiconductorInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xB9.toByte
    val name = "Enuclia Semiconductor, Inc."
  }

  case object VirtiumTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xBA.toByte
    val name = "Virtium Technology Inc."
  }

  case object SolidStateSystemCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x3B.toByte
    val name = "Solid State System Co., Ltd."
  }

  case object KianTechLlc extends ManufacturerIdentificationCode {
    val code: Byte = 0xBC.toByte
    val name = "Kian Tech LLC"
  }

  case object Artimi extends ManufacturerIdentificationCode {
    val code: Byte = 0x3D.toByte
    val name = "Artimi"
  }

  case object PowerQuotientInternational extends ManufacturerIdentificationCode {
    val code: Byte = 0x3E.toByte
    val name = "Power Quotient International"
  }

  case object AvagoTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xBF.toByte
    val name = "Avago Technologies"
  }

  case object AdTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x40.toByte
    val name = "ADTechnology"
  }

  case object SigmaDesigns extends ManufacturerIdentificationCode {
    val code: Byte = 0xC1.toByte
    val name = "Sigma Designs"
  }

  case object SiCortexInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xC2.toByte
    val name = "SiCortex, Inc."
  }

  case object VenturaTechnologyGroup extends ManufacturerIdentificationCode {
    val code: Byte = 0x43.toByte
    val name = "Ventura Technology Group"
  }

  case object Easic extends ManufacturerIdentificationCode {
    val code: Byte = 0xC4.toByte
    val name = "eASIC"
  }

  case object MhsSas extends ManufacturerIdentificationCode {
    val code: Byte = 0x45.toByte
    val name = "M.H.S. SAS"
  }

  case object MicroStarInternational extends ManufacturerIdentificationCode {
    val code: Byte = 0x46.toByte
    val name = "Micro Star International"
  }

  case object RapportInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xC7.toByte
    val name = "Rapport Inc."
  }

  case object MakwayInternational extends ManufacturerIdentificationCode {
    val code: Byte = 0xC8.toByte
    val name = "Makway International"
  }

  case object BroadReachEngineeringCo extends ManufacturerIdentificationCode {
    val code: Byte = 0x49.toByte
    val name = "Broad Reach Engineering Co."
  }

  case object SemiconductorMfgIntlCorp extends ManufacturerIdentificationCode {
    val code: Byte = 0x4A.toByte
    val name = "Semiconductor Mfg Intl Corp"
  }

  case object SiConnect extends ManufacturerIdentificationCode {
    val code: Byte = 0xCB.toByte
    val name = "SiConnect"
  }

  case object FciUsaInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x4C.toByte
    val name = "FCI USA Inc."
  }

  case object ValiditySensors extends ManufacturerIdentificationCode {
    val code: Byte = 0xCD.toByte
    val name = "Validity Sensors"
  }

  case object ConeyTechnologyCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xCE.toByte
    val name = "Coney Technology Co. Ltd."
  }

  case object SpansLogic extends ManufacturerIdentificationCode {
    val code: Byte = 0x4F.toByte
    val name = "Spans Logic"
  }

  case object NeterionInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xD0.toByte
    val name = "Neterion Inc."
  }

  case object Qimonda extends ManufacturerIdentificationCode {
    val code: Byte = 0x51.toByte
    val name = "Qimonda"
  }

  case object NewJapanRadioCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x52.toByte
    val name = "New Japan Radio Co. Ltd."
  }

  case object Velogix extends ManufacturerIdentificationCode {
    val code: Byte = 0xD3.toByte
    val name = "Velogix"
  }

  case object MontalvoSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x54.toByte
    val name = "Montalvo Systems"
  }

  case object iVivityInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xD5.toByte
    val name = "iVivity Inc."
  }

  case object WaltonChaintech extends ManufacturerIdentificationCode {
    val code: Byte = 0xD6.toByte
    val name = "Walton Chaintech"
  }

  case object Aeneon extends ManufacturerIdentificationCode {
    val code: Byte = 0x57.toByte
    val name = "AENEON"
  }

  case object LoromIndustrialCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x58.toByte
    val name = "Lorom Industrial Co. Ltd."
  }

  case object RadiospireNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xD9.toByte
    val name = "Radiospire Networks"
  }

  case object SensioTechnologiesInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xDA.toByte
    val name = "Sensio Technologies, Inc."
  }

  case object NethraImaging extends ManufacturerIdentificationCode {
    val code: Byte = 0x5B.toByte
    val name = "Nethra Imaging"
  }

  case object HexonTechnologyPteLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xDC.toByte
    val name = "Hexon Technology Pte Ltd"
  }

  case object CompuStocxCsx extends ManufacturerIdentificationCode {
    val code: Byte = 0x5D.toByte
    val name = "CompuStocx (CSX)"
  }

  case object MethodeElectronicsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x5E.toByte
    val name = "Methode Electronics, Inc."
  }

  case object ConnectOneLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xDF.toByte
    val name = "Connect One Ltd."
  }

  case object OpulanTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xE0.toByte
    val name = "Opulan Technologies"
  }

  case object SeptentrioNv extends ManufacturerIdentificationCode {
    val code: Byte = 0x61.toByte
    val name = "Septentrio NV"
  }

  case object GoldenmarsTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x62.toByte
    val name = "Goldenmars Technology Inc."
  }

  case object KretonCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xE3.toByte
    val name = "Kreton Corporation"
  }

  case object CochlearLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x64.toByte
    val name = "Cochlear Ltd."
  }

  case object AltairSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xE5.toByte
    val name = "Altair Semiconductor"
  }

  case object NetEffectInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xE6.toByte
    val name = "NetEffect, Inc."
  }

  case object SpansionInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x67.toByte
    val name = "Spansion, Inc."
  }

  case object TaiwanSemiconductorMfg extends ManufacturerIdentificationCode {
    val code: Byte = 0x68.toByte
    val name = "Taiwan Semiconductor Mfg"
  }

  case object EmphanySystemsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xE9.toByte
    val name = "Emphany Systems Inc."
  }

  case object ApaceWaveTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xEA.toByte
    val name = "ApaceWave Technologies"
  }

  case object MobilygenCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x6B.toByte
    val name = "Mobilygen Corporation"
  }

  case object Tego extends ManufacturerIdentificationCode {
    val code: Byte = 0xEC.toByte
    val name = "Tego"
  }

  case object CswitchCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x6D.toByte
    val name = "Cswitch Corporation"
  }

  case object HaierBeijingIcDesignCo extends ManufacturerIdentificationCode {
    val code: Byte = 0x6E.toByte
    val name = "Haier (Beijing) IC Design Co."
  }

  case object MetaRam extends ManufacturerIdentificationCode {
    val code: Byte = 0xEF.toByte
    val name = "MetaRAM"
  }

  case object AxelElectronicsCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x70.toByte
    val name = "Axel Electronics Co. Ltd."
  }

  case object TileraCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xF1.toByte
    val name = "Tilera Corporation"
  }

  case object Aquantia extends ManufacturerIdentificationCode {
    val code: Byte = 0xF2.toByte
    val name = "Aquantia"
  }

  case object VivaceSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x73.toByte
    val name = "Vivace Semiconductor"
  }

  case object RedpineSignals extends ManufacturerIdentificationCode {
    val code: Byte = 0xF4.toByte
    val name = "Redpine Signals"
  }

  case object Octalica extends ManufacturerIdentificationCode {
    val code: Byte = 0x75.toByte
    val name = "Octalica"
  }

  case object InterDigitalCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x76.toByte
    val name = "InterDigital Communications"
  }

  case object AvantTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xF7.toByte
    val name = "Avant Technology"
  }

  case object AsrockInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xF8.toByte
    val name = "Asrock, Inc."
  }

  case object Availink extends ManufacturerIdentificationCode {
    val code: Byte = 0x79.toByte
    val name = "Availink"
  }

  case object QuarticsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x7A.toByte
    val name = "Quartics, Inc."
  }

  case object ElementCxi extends ManufacturerIdentificationCode {
    val code: Byte = 0xFB.toByte
    val name = "Element CXI"
  }

  case object InnovacionesMicroelectronicas extends ManufacturerIdentificationCode {
    val code: Byte = 0x7C.toByte
    val name = "Innovaciones Microelectronicas"
  }

  case object VeriSiliconMicroelectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0xFD.toByte
    val name = "VeriSilicon Microelectronics"
  }

  case object W5Networks extends ManufacturerIdentificationCode {
    val code: Byte = 0xFE.toByte
    val name = "W5 Networks"
  }

}
