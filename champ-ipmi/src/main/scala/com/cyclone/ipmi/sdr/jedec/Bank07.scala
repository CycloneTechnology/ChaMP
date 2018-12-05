package com.cyclone.ipmi.sdr.jedec

// JEDEC Manufacturer lookup
// See STANDARD MANUFACTURERS IDENTIFICATION CODE at https://www.jedec.org/
// This code is based on document JEP106AT published Spe 2016

object Bank07 {

  val bank: PartialFunction[Byte, ManufacturerIdentificationCode] = {

    case Moveking.code                              => Moveking
    case MavrixTechnologyInc.code                   => MavrixTechnologyInc
    case CellGuideLtd.code                          => CellGuideLtd
    case FaradayTechnology.code                     => FaradayTechnology
    case DiabloTechnologiesInc.code                 => DiabloTechnologiesInc
    case Jennic.code                                => Jennic
    case Octasic.code                               => Octasic
    case MolexIncorporated.code                     => MolexIncorporated
    case ThreeLeafNetworks.code                     => ThreeLeafNetworks
    case BrightMicronTechnology.code                => BrightMicronTechnology
    case Netxen.code                                => Netxen
    case NextWaveBroadbandInc.code                  => NextWaveBroadbandInc
    case DisplayLink.code                           => DisplayLink
    case ZmosTechnology.code                        => ZmosTechnology
    case TecHill.code                               => TecHill
    case MultigigInc.code                           => MultigigInc
    case Amimon.code                                => Amimon
    case EuphonicTechnologiesInc.code               => EuphonicTechnologiesInc
    case BrnPhoenix.code                            => BrnPhoenix
    case InSilica.code                              => InSilica
    case EmberCorporation.code                      => EmberCorporation
    case AvexirTechnologiesCorporation.code         => AvexirTechnologiesCorporation
    case EchelonCorporation.code                    => EchelonCorporation
    case EdgewaterComputerSystems.code              => EdgewaterComputerSystems
    case XmosSemiconductorLtd.code                  => XmosSemiconductorLtd
    case GenusionInc.code                           => GenusionInc
    case MemoryCorpNv.code                          => MemoryCorpNv
    case SiliconBlueTechnologies.code               => SiliconBlueTechnologies
    case RambusInc.code                             => RambusInc
    case AndesTechnologyCorporation.code            => AndesTechnologyCorporation
    case CoronisSystems.code                        => CoronisSystems
    case AchronixSemiconductor.code                 => AchronixSemiconductor
    case SianoMobileSiliconLtd.code                 => SianoMobileSiliconLtd
    case SemtechCorporation.code                    => SemtechCorporation
    case PixelworksInc.code                         => PixelworksInc
    case GaislerResearchAb.code                     => GaislerResearchAb
    case Teranetics.code                            => Teranetics
    case ToppanPrintingCoLtd.code                   => ToppanPrintingCoLtd
    case Kingxcon.code                              => Kingxcon
    case SiliconIntegratedSystems.code              => SiliconIntegratedSystems
    case IoDataDeviceInc.code                       => IoDataDeviceInc
    case NdsAmericasInc.code                        => NdsAmericasInc
    case SolomonSystechLimited.code                 => SolomonSystechLimited
    case OnDemandMicroelectronics.code              => OnDemandMicroelectronics
    case AmicusWirelessInc.code                     => AmicusWirelessInc
    case SmardtvSnc.code                            => SmardtvSnc
    case ComsysCommunicationLtd.code                => ComsysCommunicationLtd
    case MovidiaLtd.code                            => MovidiaLtd
    case JavadGnssInc.code                          => JavadGnssInc
    case MontageTechnologyGroup.code                => MontageTechnologyGroup
    case TridentMicrosystems.code                   => TridentMicrosystems
    case SuperTalent.code                           => SuperTalent
    case OptichronInc.code                          => OptichronInc
    case FutureWavesUkLtd.code                      => FutureWavesUkLtd
    case SiBeamInc.code                             => SiBeamInc
    case InicoreInc.code                            => InicoreInc
    case ViridentSystems.code                       => ViridentSystems
    case M2000Inc.code                              => M2000Inc
    case ZeroGWirelessInc.code                      => ZeroGWirelessInc
    case GingleTechnologyCoLtd.code                 => GingleTechnologyCoLtd
    case SpaceMicroInc.code                         => SpaceMicroInc
    case Wilocity.code                              => Wilocity
    case NovaforaInc.code                           => NovaforaInc
    case iKoaCorporation.code                       => iKoaCorporation
    case ASintTechnology.code                       => ASintTechnology
    case Ramtron.code                               => Ramtron
    case PlatoNetworksInc.code                      => PlatoNetworksInc
    case IPtronicsAs.code                           => IPtronicsAs
    case InfiniteMemories.code                      => InfiniteMemories
    case ParadeTechnologiesInc.code                 => ParadeTechnologiesInc
    case DuneNetworks.code                          => DuneNetworks
    case GigaDeviceSemiconductor.code               => GigaDeviceSemiconductor
    case ModuLtd.code                               => ModuLtd
    case Ceitec.code                                => Ceitec
    case NorthropGrumman.code                       => NorthropGrumman
    case XronetCorporation.code                     => XronetCorporation
    case SiconSemiconductorAb.code                  => SiconSemiconductorAb
    case AtlaElectronicsCoLtd.code                  => AtlaElectronicsCoLtd
    case TopramTechnology.code                      => TopramTechnology
    case SilegoTechnologyInc.code                   => SilegoTechnologyInc
    case Kinglife.code                              => Kinglife
    case AbilityIndustriesLtd.code                  => AbilityIndustriesLtd
    case SiliconPowerComputerAndCommunications.code => SiliconPowerComputerAndCommunications
    case AugustaTechnologyInc.code                  => AugustaTechnologyInc
    case NantronicsSemiconductors.code              => NantronicsSemiconductors
    case HilscherGesellschaft.code                  => HilscherGesellschaft
    case QuixantLtd.code                            => QuixantLtd
    case PercelloLtd.code                           => PercelloLtd
    case NextIoInc.code                             => NextIoInc
    case ScanimetricsInc.code                       => ScanimetricsInc
    case FsSemiCompanyLtd.code                      => FsSemiCompanyLtd
    case InfineraCorporation.code                   => InfineraCorporation
    case SandForceInc.code                          => SandForceInc
    case LexarMedia.code                            => LexarMedia
    case TeradyneInc.code                           => TeradyneInc
    case MemoryExchangeCorp.code                    => MemoryExchangeCorp
    case SuzhouSmartekElectronics.code              => SuzhouSmartekElectronics
    case AvantiumCorporation.code                   => AvantiumCorporation
    case AtpElectronicsInc.code                     => AtpElectronicsInc
    case ValensSemiconductorLtd.code                => ValensSemiconductorLtd
    case AgateLogicInc.code                         => AgateLogicInc
    case Netronome.code                             => Netronome
    case ZenvergeInc.code                           => ZenvergeInc
    case NtrigLtd.code                              => NtrigLtd
    case SanMaxTechnologiesInc.code                 => SanMaxTechnologiesInc
    case ContourSemiconductorInc.code               => ContourSemiconductorInc
    case TwinMOS.code                               => TwinMOS
    case SiliconSystemsInc.code                     => SiliconSystemsInc
    case VColorTechnologyInc.code                   => VColorTechnologyInc
    case CerticomCorporation.code                   => CerticomCorporation
    case JscIccMilandr.code                         => JscIccMilandr
    case PhotoFastGlobalInc.code                    => PhotoFastGlobalInc
    case InnoDiskCorporation.code                   => InnoDiskCorporation
    case MusclePower.code                           => MusclePower
    case EnergyMicro.code                           => EnergyMicro
    case Innofidei.code                             => Innofidei
    case CopperGateCommunications.code              => CopperGateCommunications
    case HoltekSemiconductorInc.code                => HoltekSemiconductorInc
    case MysonCenturyInc.code                       => MysonCenturyInc
    case Fidelix.code                               => Fidelix
    case RedDigitalCinema.code                      => RedDigitalCinema
    case DensbitsTechnology.code                    => DensbitsTechnology
    case Zempro.code                                => Zempro
    case MoSys.code                                 => MoSys
    case Provigent.code                             => Provigent
    case TriadSemiconductorInc.code                 => TriadSemiconductorInc
  }

  case object Moveking extends ManufacturerIdentificationCode {
    val code: Byte = 0x01.toByte
    val name = "MOVEKING"
  }

  case object MavrixTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x02.toByte
    val name = "Mavrix Technology, Inc."
  }

  case object CellGuideLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x83.toByte
    val name = "CellGuide Ltd."
  }

  case object FaradayTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x04.toByte
    val name = "Faraday Technology"
  }

  case object DiabloTechnologiesInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x85.toByte
    val name = "Diablo Technologies, Inc."
  }

  case object Jennic extends ManufacturerIdentificationCode {
    val code: Byte = 0x86.toByte
    val name = "Jennic"
  }

  case object Octasic extends ManufacturerIdentificationCode {
    val code: Byte = 0x07.toByte
    val name = "Octasic"
  }

  case object MolexIncorporated extends ManufacturerIdentificationCode {
    val code: Byte = 0x08.toByte
    val name = "Molex Incorporated"
  }

  case object ThreeLeafNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x89.toByte
    val name = "3Leaf Networks"
  }

  case object BrightMicronTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x8A.toByte
    val name = "Bright Micron Technology"
  }

  case object Netxen extends ManufacturerIdentificationCode {
    val code: Byte = 0x0B.toByte
    val name = "Netxen"
  }

  case object NextWaveBroadbandInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x8C.toByte
    val name = "NextWave Broadband Inc."
  }

  case object DisplayLink extends ManufacturerIdentificationCode {
    val code: Byte = 0x0D.toByte
    val name = "DisplayLink"
  }

  case object ZmosTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x0E.toByte
    val name = "ZMOS Technology"
  }

  case object TecHill extends ManufacturerIdentificationCode {
    val code: Byte = 0x8F.toByte
    val name = "Tec-Hill"
  }

  case object MultigigInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x10.toByte
    val name = "Multigig, Inc."
  }

  case object Amimon extends ManufacturerIdentificationCode {
    val code: Byte = 0x91.toByte
    val name = "Amimon"
  }

  case object EuphonicTechnologiesInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x92.toByte
    val name = "Euphonic Technologies, Inc."
  }

  case object BrnPhoenix extends ManufacturerIdentificationCode {
    val code: Byte = 0x13.toByte
    val name = "BRN Phoenix"
  }

  case object InSilica extends ManufacturerIdentificationCode {
    val code: Byte = 0x94.toByte
    val name = "InSilica"
  }

  case object EmberCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x15.toByte
    val name = "Ember Corporation"
  }

  case object AvexirTechnologiesCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x16.toByte
    val name = "Avexir Technologies Corporation"
  }

  case object EchelonCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x97.toByte
    val name = "Echelon Corporation"
  }

  case object EdgewaterComputerSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x98.toByte
    val name = "Edgewater Computer Systems"
  }

  case object XmosSemiconductorLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x19.toByte
    val name = "XMOS Semiconductor Ltd."
  }

  case object GenusionInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x1A.toByte
    val name = "GENUSION, Inc."
  }

  case object MemoryCorpNv extends ManufacturerIdentificationCode {
    val code: Byte = 0x9B.toByte
    val name = "Memory Corp NV"
  }

  case object SiliconBlueTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x1C.toByte
    val name = "SiliconBlue Technologies"
  }

  case object RambusInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x9D.toByte
    val name = "Rambus Inc."
  }

  case object AndesTechnologyCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x9E.toByte
    val name = "Andes Technology Corporation"
  }

  case object CoronisSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x1F.toByte
    val name = "Coronis Systems"
  }

  case object AchronixSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x20.toByte
    val name = "Achronix Semiconductor"
  }

  case object SianoMobileSiliconLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xA1.toByte
    val name = "Siano Mobile Silicon Ltd."
  }

  case object SemtechCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xA2.toByte
    val name = "Semtech Corporation"
  }

  case object PixelworksInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x23.toByte
    val name = "Pixelworks Inc."
  }

  case object GaislerResearchAb extends ManufacturerIdentificationCode {
    val code: Byte = 0xA4.toByte
    val name = "Gaisler Research AB"
  }

  case object Teranetics extends ManufacturerIdentificationCode {
    val code: Byte = 0x25.toByte
    val name = "Teranetics"
  }

  case object ToppanPrintingCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x26.toByte
    val name = "Toppan Printing Co. Ltd."
  }

  case object Kingxcon extends ManufacturerIdentificationCode {
    val code: Byte = 0xA7.toByte
    val name = "Kingxcon"
  }

  case object SiliconIntegratedSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0xA8.toByte
    val name = "Silicon Integrated Systems"
  }

  case object IoDataDeviceInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x29.toByte
    val name = "I-O Data Device, Inc."
  }

  case object NdsAmericasInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x2A.toByte
    val name = "NDS Americas Inc."
  }

  case object SolomonSystechLimited extends ManufacturerIdentificationCode {
    val code: Byte = 0xAB.toByte
    val name = "Solomon Systech Limited"
  }

  case object OnDemandMicroelectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x2C.toByte
    val name = "On Demand Microelectronics"
  }

  case object AmicusWirelessInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xAD.toByte
    val name = "Amicus Wireless Inc."
  }

  case object SmardtvSnc extends ManufacturerIdentificationCode {
    val code: Byte = 0xAE.toByte
    val name = "SMARDTV SNC"
  }

  case object ComsysCommunicationLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x2F.toByte
    val name = "Comsys Communication Ltd."
  }

  case object MovidiaLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xB0.toByte
    val name = "Movidia Ltd."
  }

  case object JavadGnssInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x31.toByte
    val name = "Javad GNSS, Inc."
  }

  case object MontageTechnologyGroup extends ManufacturerIdentificationCode {
    val code: Byte = 0x32.toByte
    val name = "Montage Technology Group"
  }

  case object TridentMicrosystems extends ManufacturerIdentificationCode {
    val code: Byte = 0xB3.toByte
    val name = "Trident Microsystems"
  }

  case object SuperTalent extends ManufacturerIdentificationCode {
    val code: Byte = 0x34.toByte
    val name = "Super Talent"
  }

  case object OptichronInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xB5.toByte
    val name = "Optichron, Inc."
  }

  case object FutureWavesUkLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xB6.toByte
    val name = "Future Waves UK Ltd."
  }

  case object SiBeamInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x37.toByte
    val name = "SiBEAM, Inc."
  }

  case object InicoreInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x38.toByte
    val name = "Inicore,Inc."
  }

  case object ViridentSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0xB9.toByte
    val name = "Virident Systems"
  }

  case object M2000Inc extends ManufacturerIdentificationCode {
    val code: Byte = 0xBA.toByte
    val name = "M2000, Inc."
  }

  case object ZeroGWirelessInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x3B.toByte
    val name = "ZeroG Wireless, Inc."
  }

  case object GingleTechnologyCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xBC.toByte
    val name = "Gingle Technology Co. Ltd."
  }

  case object SpaceMicroInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x3D.toByte
    val name = "Space Micro Inc."
  }

  case object Wilocity extends ManufacturerIdentificationCode {
    val code: Byte = 0x3E.toByte
    val name = "Wilocity"
  }

  case object NovaforaInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xBF.toByte
    val name = "Novafora, Inc."
  }

  case object iKoaCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x40.toByte
    val name = "iKoa Corporation"
  }

  case object ASintTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xC1.toByte
    val name = "ASint Technology"
  }

  case object Ramtron extends ManufacturerIdentificationCode {
    val code: Byte = 0xC2.toByte
    val name = "Ramtron"
  }

  case object PlatoNetworksInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x43.toByte
    val name = "Plato Networks Inc."
  }

  case object IPtronicsAs extends ManufacturerIdentificationCode {
    val code: Byte = 0xC4.toByte
    val name = "IPtronics AS"
  }

  case object InfiniteMemories extends ManufacturerIdentificationCode {
    val code: Byte = 0x45.toByte
    val name = "Infinite-Memories"
  }

  case object ParadeTechnologiesInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x46.toByte
    val name = "Parade Technologies Inc."
  }

  case object DuneNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xC7.toByte
    val name = "Dune Networks"
  }

  case object GigaDeviceSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xC8.toByte
    val name = "GigaDevice Semiconductor"
  }

  case object ModuLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x49.toByte
    val name = "Modu Ltd."
  }

  case object Ceitec extends ManufacturerIdentificationCode {
    val code: Byte = 0x4A.toByte
    val name = "CEITEC"
  }

  case object NorthropGrumman extends ManufacturerIdentificationCode {
    val code: Byte = 0xCB.toByte
    val name = "Northrop Grumman"
  }

  case object XronetCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x4C.toByte
    val name = "XRONET Corporation"
  }

  case object SiconSemiconductorAb extends ManufacturerIdentificationCode {
    val code: Byte = 0xCD.toByte
    val name = "Sicon Semiconductor AB"
  }

  case object AtlaElectronicsCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xCE.toByte
    val name = "Atla Electronics Co. Ltd."
  }

  case object TopramTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x4F.toByte
    val name = "TOPRAM Technology"
  }

  case object SilegoTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xD0.toByte
    val name = "Silego Technology Inc."
  }

  case object Kinglife extends ManufacturerIdentificationCode {
    val code: Byte = 0x51.toByte
    val name = "Kinglife"
  }

  case object AbilityIndustriesLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x52.toByte
    val name = "Ability Industries Ltd."
  }

  case object SiliconPowerComputerAndCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0xD3.toByte
    val name = "Silicon Power Computer & Communications"
  }

  case object AugustaTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x54.toByte
    val name = "Augusta Technology, Inc."
  }

  case object NantronicsSemiconductors extends ManufacturerIdentificationCode {
    val code: Byte = 0xD5.toByte
    val name = "Nantronics Semiconductors"
  }

  case object HilscherGesellschaft extends ManufacturerIdentificationCode {
    val code: Byte = 0xD6.toByte
    val name = "Hilscher Gesellschaft"
  }

  case object QuixantLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x57.toByte
    val name = "Quixant Ltd."
  }

  case object PercelloLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x58.toByte
    val name = "Percello Ltd."
  }

  case object NextIoInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xD9.toByte
    val name = "NextIO Inc."
  }

  case object ScanimetricsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xDA.toByte
    val name = "Scanimetrics Inc."
  }

  case object FsSemiCompanyLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x5B.toByte
    val name = "FS-Semi Company Ltd."
  }

  case object InfineraCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xDC.toByte
    val name = "Infinera Corporation"
  }

  case object SandForceInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x5D.toByte
    val name = "SandForce Inc."
  }

  case object LexarMedia extends ManufacturerIdentificationCode {
    val code: Byte = 0x5E.toByte
    val name = "Lexar Media"
  }

  case object TeradyneInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xDF.toByte
    val name = "Teradyne Inc."
  }

  case object MemoryExchangeCorp extends ManufacturerIdentificationCode {
    val code: Byte = 0xE0.toByte
    val name = "Memory Exchange Corp."
  }

  case object SuzhouSmartekElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x61.toByte
    val name = "Suzhou Smartek Electronics"
  }

  case object AvantiumCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x62.toByte
    val name = "Avantium Corporation"
  }

  case object AtpElectronicsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xE3.toByte
    val name = "ATP Electronics Inc."
  }

  case object ValensSemiconductorLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x64.toByte
    val name = "Valens Semiconductor Ltd"
  }

  case object AgateLogicInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xE5.toByte
    val name = "Agate Logic, Inc."
  }

  case object Netronome extends ManufacturerIdentificationCode {
    val code: Byte = 0xE6.toByte
    val name = "Netronome"
  }

  case object ZenvergeInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x67.toByte
    val name = "Zenverge, Inc."
  }

  case object NtrigLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x68.toByte
    val name = "N-trig Ltd"
  }

  case object SanMaxTechnologiesInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xE9.toByte
    val name = "SanMax Technologies Inc."
  }

  case object ContourSemiconductorInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xEA.toByte
    val name = "Contour Semiconductor Inc."
  }

  case object TwinMOS extends ManufacturerIdentificationCode {
    val code: Byte = 0x6B.toByte
    val name = "TwinMOS"
  }

  case object SiliconSystemsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xEC.toByte
    val name = "Silicon Systems, Inc."
  }

  case object VColorTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x6D.toByte
    val name = "V-Color Technology Inc."
  }

  case object CerticomCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x6E.toByte
    val name = "Certicom Corporation"
  }

  case object JscIccMilandr extends ManufacturerIdentificationCode {
    val code: Byte = 0xEF.toByte
    val name = "JSC ICC Milandr"
  }

  case object PhotoFastGlobalInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x70.toByte
    val name = "PhotoFast Global Inc."
  }

  case object InnoDiskCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xF1.toByte
    val name = "InnoDisk Corporation"
  }

  case object MusclePower extends ManufacturerIdentificationCode {
    val code: Byte = 0xF2.toByte
    val name = "Muscle Power"
  }

  case object EnergyMicro extends ManufacturerIdentificationCode {
    val code: Byte = 0x73.toByte
    val name = "Energy Micro"
  }

  case object Innofidei extends ManufacturerIdentificationCode {
    val code: Byte = 0xF4.toByte
    val name = "Innofidei"
  }

  case object CopperGateCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x75.toByte
    val name = "CopperGate Communications"
  }

  case object HoltekSemiconductorInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x76.toByte
    val name = "Holtek Semiconductor Inc."
  }

  case object MysonCenturyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xF7.toByte
    val name = "Myson Century, Inc."
  }

  case object Fidelix extends ManufacturerIdentificationCode {
    val code: Byte = 0xF8.toByte
    val name = "FIDELIX"
  }

  case object RedDigitalCinema extends ManufacturerIdentificationCode {
    val code: Byte = 0x79.toByte
    val name = "Red Digital Cinema"
  }

  case object DensbitsTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x7A.toByte
    val name = "Densbits Technology"
  }

  case object Zempro extends ManufacturerIdentificationCode {
    val code: Byte = 0xFB.toByte
    val name = "Zempro"
  }

  case object MoSys extends ManufacturerIdentificationCode {
    val code: Byte = 0x7C.toByte
    val name = "MoSys"
  }

  case object Provigent extends ManufacturerIdentificationCode {
    val code: Byte = 0xFD.toByte
    val name = "Provigent"
  }

  case object TriadSemiconductorInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xFE.toByte
    val name = "Triad Semiconductor, Inc."
  }

}
