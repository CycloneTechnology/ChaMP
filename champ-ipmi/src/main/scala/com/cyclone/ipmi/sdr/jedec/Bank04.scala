package com.cyclone.ipmi.sdr.jedec

// JEDEC Manufacturer lookup
// See STANDARD MANUFACTURERS IDENTIFICATION CODE at https://www.jedec.org/
// This code is based on document JEP106AT published Spe 2016

object Bank04 {

  val bank: PartialFunction[Byte, ManufacturerIdentificationCode] = {

    case Solectron.code                    => Solectron
    case OptosysTechnologies.code          => OptosysTechnologies
    case BuffaloFormerlyMelco.code         => BuffaloFormerlyMelco
    case TriMediaTechnologies.code         => TriMediaTechnologies
    case CyanTechnologies.code             => CyanTechnologies
    case GlobalLocate.code                 => GlobalLocate
    case Optillion.code                    => Optillion
    case TeragoCommunications.code         => TeragoCommunications
    case IkanosCommunications.code         => IkanosCommunications
    case PrincetonTechnology.code          => PrincetonTechnology
    case NanyaTechnology.code              => NanyaTechnology
    case EliteFlashStorage.code            => EliteFlashStorage
    case Mysticom.code                     => Mysticom
    case LightSandCommunications.code      => LightSandCommunications
    case AtiTechnologies.code              => AtiTechnologies
    case AgereSystems.code                 => AgereSystems
    case NeoMagic.code                     => NeoMagic
    case AuroraNetics.code                 => AuroraNetics
    case GoldenEmpire.code                 => GoldenEmpire
    case Mushkin.code                      => Mushkin
    case TiogaTechnologies.code            => TiogaTechnologies
    case Netlist.code                      => Netlist
    case TeraLogic.code                    => TeraLogic
    case CicadaSemiconductor.code          => CicadaSemiconductor
    case CentonElectronics.code            => CentonElectronics
    case TycoElectronics.code              => TycoElectronics
    case MagisWorks.code                   => MagisWorks
    case Zettacom.code                     => Zettacom
    case CogencySemiconductor.code         => CogencySemiconductor
    case ChipconAs.code                    => ChipconAs
    case AspexTechnology.code              => AspexTechnology
    case F5Networks.code                   => F5Networks
    case ProgrammableSiliconSolutions.code => ProgrammableSiliconSolutions
    case ChipWrights.code                  => ChipWrights
    case AcornNetworks.code                => AcornNetworks
    case Quicklogic.code                   => Quicklogic
    case KingmaxSemiconductor.code         => KingmaxSemiconductor
    case Bops.code                         => Bops
    case Flasys.code                       => Flasys
    case BitBlitzCommunications.code       => BitBlitzCommunications
    case EmemoryTechnology.code            => EmemoryTechnology
    case ProcketNetworks.code              => ProcketNetworks
    case PurpleRay.code                    => PurpleRay
    case TrebiaNetworks.code               => TrebiaNetworks
    case DeltaElectronics.code             => DeltaElectronics
    case OnexCommunications.code           => OnexCommunications
    case AmpleCommunications.code          => AmpleCommunications
    case MemoryExpertsIntl.code            => MemoryExpertsIntl
    case AstuteNetworks.code               => AstuteNetworks
    case AzandaNetworkDevices.code         => AzandaNetworkDevices
    case Dibcom.code                       => Dibcom
    case Tekmos.code                       => Tekmos
    case ApiNetWorks.code                  => ApiNetWorks
    case BayMicrosystems.code              => BayMicrosystems
    case FirecronLtd.code                  => FirecronLtd
    case ResonextCommunications.code       => ResonextCommunications
    case TachysTechnologies.code           => TachysTechnologies
    case EquatorTechnology.code            => EquatorTechnology
    case ConceptComputer.code              => ConceptComputer
    case Silcom.code                       => Silcom
    case ThreeDlabs.code                   => ThreeDlabs
    case CtMagazine.code                   => CtMagazine
    case SaneraSystems.code                => SaneraSystems
    case SiliconPackets.code               => SiliconPackets
    case ViasystemsGroup.code              => ViasystemsGroup
    case Simtek.code                       => Simtek
    case SemiconDevicesSingapore.code      => SemiconDevicesSingapore
    case SatronHandelsges.code             => SatronHandelsges
    case ImprovSystems.code                => ImprovSystems
    case IndusysGmbH.code                  => IndusysGmbH
    case Corrent.code                      => Corrent
    case InfrantTechnologies.code          => InfrantTechnologies
    case RitekCorp.code                    => RitekCorp
    case empowerTelNetworks.code           => empowerTelNetworks
    case Hypertec.code                     => Hypertec
    case CaviumNetworks.code               => CaviumNetworks
    case PlxTechnology.code                => PlxTechnology
    case MassanaDesign.code                => MassanaDesign
    case Intrinsity.code                   => Intrinsity
    case ValenceSemiconductor.code         => ValenceSemiconductor
    case TerawaveCommunications.code       => TerawaveCommunications
    case IceFyreSemiconductor.code         => IceFyreSemiconductor
    case Primarion.code                    => Primarion
    case PicochipDesignsLtd.code           => PicochipDesignsLtd
    case SilverbackSystems.code            => SilverbackSystems
    case JadeStarTechnologies.code         => JadeStarTechnologies
    case PijnenburgSecurealink.code        => PijnenburgSecurealink
    case TakeMsUltronAg.code               => TakeMsUltronAg
    case CambridgeSiliconRadio.code        => CambridgeSiliconRadio
    case Swissbit.code                     => Swissbit
    case NazomiCommunications.code         => NazomiCommunications
    case EwaveSystem.code                  => EwaveSystem
    case RockwellCollins.code              => RockwellCollins
    case PicocelCoLtdPaion.code            => PicocelCoLtdPaion
    case AlphamosaicLtd.code               => AlphamosaicLtd
    case Sandburst.code                    => Sandburst
    case SiConVideo.code                   => SiConVideo
    case NanoAmpSolutions.code             => NanoAmpSolutions
    case EricssonTechnology.code           => EricssonTechnology
    case PrairieComm.code                  => PrairieComm
    case MitacInternational.code           => MitacInternational
    case LayerNNetworks.code               => LayerNNetworks
    case MtekVisionAtsana.code             => MtekVisionAtsana
    case AllegroNetworks.code              => AllegroNetworks
    case MarvellSemiconductors.code        => MarvellSemiconductors
    case NetergyMicroelectronic.code       => NetergyMicroelectronic
    case Nvidia.code                       => Nvidia
    case InternetMachines.code             => InternetMachines
    case MemorysolutionGmbH.code           => MemorysolutionGmbH
    case LitchfieldCommunication.code      => LitchfieldCommunication
    case AcctonTechnology.code             => AcctonTechnology
    case TeradiantNetworks.code            => TeradiantNetworks
    case ScaleoChip.code                   => ScaleoChip
    case CortinaSystems.code               => CortinaSystems
    case RamComponents.code                => RamComponents
    case RaqiaNetworks.code                => RaqiaNetworks
    case ClearSpeed.code                   => ClearSpeed
    case MatsushitaBattery.code            => MatsushitaBattery
    case Xelerated.code                    => Xelerated
    case SimpleTech.code                   => SimpleTech
    case UtronTechnology.code              => UtronTechnology
    case AstecInternational.code           => AstecInternational
    case AvmGmbH.code                      => AvmGmbH
    case ReduxCommunications.code          => ReduxCommunications
    case DotHillSystems.code               => DotHillSystems
    case TeraChip.code                     => TeraChip
  }

  case object Solectron extends ManufacturerIdentificationCode {
    val code: Byte = 0x01.toByte
    val name = "Solectron"
  }

  case object OptosysTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x02.toByte
    val name = "Optosys Technologies"
  }

  case object BuffaloFormerlyMelco extends ManufacturerIdentificationCode {
    val code: Byte = 0x83.toByte
    val name = "Buffalo (Formerly Melco)"
  }

  case object TriMediaTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x04.toByte
    val name = "TriMedia Technologies"
  }

  case object CyanTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x85.toByte
    val name = "Cyan Technologies"
  }

  case object GlobalLocate extends ManufacturerIdentificationCode {
    val code: Byte = 0x86.toByte
    val name = "Global Locate"
  }

  case object Optillion extends ManufacturerIdentificationCode {
    val code: Byte = 0x07.toByte
    val name = "Optillion"
  }

  case object TeragoCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x08.toByte
    val name = "TeragoCommunications"
  }

  case object IkanosCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x89.toByte
    val name = "Ikanos Communications"
  }

  case object PrincetonTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x8A.toByte
    val name = "Princeton Technology"
  }

  case object NanyaTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x0B.toByte
    val name = "Nanya Technology"
  }

  case object EliteFlashStorage extends ManufacturerIdentificationCode {
    val code: Byte = 0x8C.toByte
    val name = "Elite Flash Storage"
  }

  case object Mysticom extends ManufacturerIdentificationCode {
    val code: Byte = 0x0D.toByte
    val name = "Mysticom"
  }

  case object LightSandCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x0E.toByte
    val name = "LightSand Communications"
  }

  case object AtiTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x8F.toByte
    val name = "ATI Technologies"
  }

  case object AgereSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x10.toByte
    val name = "Agere Systems"
  }

  case object NeoMagic extends ManufacturerIdentificationCode {
    val code: Byte = 0x91.toByte
    val name = "NeoMagic"
  }

  case object AuroraNetics extends ManufacturerIdentificationCode {
    val code: Byte = 0x92.toByte
    val name = "AuroraNetics"
  }

  case object GoldenEmpire extends ManufacturerIdentificationCode {
    val code: Byte = 0x13.toByte
    val name = "Golden Empire"
  }

  case object Mushkin extends ManufacturerIdentificationCode {
    val code: Byte = 0x94.toByte
    val name = "Mushkin"
  }

  case object TiogaTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x15.toByte
    val name = "Tioga Technologies"
  }

  case object Netlist extends ManufacturerIdentificationCode {
    val code: Byte = 0x16.toByte
    val name = "Netlist"
  }

  case object TeraLogic extends ManufacturerIdentificationCode {
    val code: Byte = 0x97.toByte
    val name = "TeraLogic"
  }

  case object CicadaSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x98.toByte
    val name = "Cicada Semiconductor"
  }

  case object CentonElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x19.toByte
    val name = "Centon Electronics"
  }

  case object TycoElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x1A.toByte
    val name = "Tyco Electronics"
  }

  case object MagisWorks extends ManufacturerIdentificationCode {
    val code: Byte = 0x9B.toByte
    val name = "Magis Works"
  }

  case object Zettacom extends ManufacturerIdentificationCode {
    val code: Byte = 0x1C.toByte
    val name = "Zettacom"
  }

  case object CogencySemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x9D.toByte
    val name = "Cogency Semiconductor"
  }

  case object ChipconAs extends ManufacturerIdentificationCode {
    val code: Byte = 0x9E.toByte
    val name = "Chipcon AS"
  }

  case object AspexTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x1F.toByte
    val name = "Aspex Technology"
  }

  case object F5Networks extends ManufacturerIdentificationCode {
    val code: Byte = 0x20.toByte
    val name = "F5 Networks"
  }

  case object ProgrammableSiliconSolutions extends ManufacturerIdentificationCode {
    val code: Byte = 0xA1.toByte
    val name = "Programmable Silicon Solutions"
  }

  case object ChipWrights extends ManufacturerIdentificationCode {
    val code: Byte = 0xA2.toByte
    val name = "ChipWrights"
  }

  case object AcornNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x23.toByte
    val name = "Acorn Networks"
  }

  case object Quicklogic extends ManufacturerIdentificationCode {
    val code: Byte = 0xA4.toByte
    val name = "Quicklogic"
  }

  case object KingmaxSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x25.toByte
    val name = "Kingmax Semiconductor"
  }

  case object Bops extends ManufacturerIdentificationCode {
    val code: Byte = 0x26.toByte
    val name = "BOPS"
  }

  case object Flasys extends ManufacturerIdentificationCode {
    val code: Byte = 0xA7.toByte
    val name = "Flasys"
  }

  case object BitBlitzCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0xA8.toByte
    val name = "BitBlitz Communications"
  }

  case object EmemoryTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x29.toByte
    val name = "eMemory Technology"
  }

  case object ProcketNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x2A.toByte
    val name = "Procket Networks"
  }

  case object PurpleRay extends ManufacturerIdentificationCode {
    val code: Byte = 0xAB.toByte
    val name = "Purple Ray"
  }

  case object TrebiaNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x2C.toByte
    val name = "Trebia Networks"
  }

  case object DeltaElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0xAD.toByte
    val name = "Delta Electronics"
  }

  case object OnexCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0xAE.toByte
    val name = "Onex Communications"
  }

  case object AmpleCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x2F.toByte
    val name = "Ample Communications"
  }

  case object MemoryExpertsIntl extends ManufacturerIdentificationCode {
    val code: Byte = 0xB0.toByte
    val name = "Memory Experts Intl"
  }

  case object AstuteNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x31.toByte
    val name = "Astute Networks"
  }

  case object AzandaNetworkDevices extends ManufacturerIdentificationCode {
    val code: Byte = 0x32.toByte
    val name = "Azanda Network Devices"
  }

  case object Dibcom extends ManufacturerIdentificationCode {
    val code: Byte = 0xB3.toByte
    val name = "Dibcom"
  }

  case object Tekmos extends ManufacturerIdentificationCode {
    val code: Byte = 0x34.toByte
    val name = "Tekmos"
  }

  case object ApiNetWorks extends ManufacturerIdentificationCode {
    val code: Byte = 0xB5.toByte
    val name = "API NetWorks"
  }

  case object BayMicrosystems extends ManufacturerIdentificationCode {
    val code: Byte = 0xB6.toByte
    val name = "Bay Microsystems"
  }

  case object FirecronLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x37.toByte
    val name = "Firecron Ltd"
  }

  case object ResonextCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x38.toByte
    val name = "Resonext Communications"
  }

  case object TachysTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xB9.toByte
    val name = "Tachys Technologies"
  }

  case object EquatorTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xBA.toByte
    val name = "Equator Technology"
  }

  case object ConceptComputer extends ManufacturerIdentificationCode {
    val code: Byte = 0x3B.toByte
    val name = "Concept Computer"
  }

  case object Silcom extends ManufacturerIdentificationCode {
    val code: Byte = 0xBC.toByte
    val name = "SILCOM"
  }

  case object ThreeDlabs extends ManufacturerIdentificationCode {
    val code: Byte = 0x3D.toByte
    val name = "3Dlabs"
  }

  case object CtMagazine extends ManufacturerIdentificationCode {
    val code: Byte = 0x3E.toByte
    val name = "c’t Magazine"
  }

  case object SaneraSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0xBF.toByte
    val name = "Sanera Systems"
  }

  case object SiliconPackets extends ManufacturerIdentificationCode {
    val code: Byte = 0x40.toByte
    val name = "Silicon Packets"
  }

  case object ViasystemsGroup extends ManufacturerIdentificationCode {
    val code: Byte = 0xC1.toByte
    val name = "Viasystems Group"
  }

  case object Simtek extends ManufacturerIdentificationCode {
    val code: Byte = 0xC2.toByte
    val name = "Simtek"
  }

  case object SemiconDevicesSingapore extends ManufacturerIdentificationCode {
    val code: Byte = 0x43.toByte
    val name = "Semicon Devices Singapore"
  }

  case object SatronHandelsges extends ManufacturerIdentificationCode {
    val code: Byte = 0xC4.toByte
    val name = "Satron Handelsges"
  }

  case object ImprovSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x45.toByte
    val name = "Improv Systems"
  }

  case object IndusysGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0x46.toByte
    val name = "INDUSYS GmbH"
  }

  case object Corrent extends ManufacturerIdentificationCode {
    val code: Byte = 0xC7.toByte
    val name = "Corrent"
  }

  case object InfrantTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xC8.toByte
    val name = "Infrant Technologies"
  }

  case object RitekCorp extends ManufacturerIdentificationCode {
    val code: Byte = 0x49.toByte
    val name = "Ritek Corp"
  }

  case object empowerTelNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x4A.toByte
    val name = "empowerTel Networks"
  }

  case object Hypertec extends ManufacturerIdentificationCode {
    val code: Byte = 0xCB.toByte
    val name = "Hypertec"
  }

  case object CaviumNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x4C.toByte
    val name = "Cavium Networks"
  }

  case object PlxTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xCD.toByte
    val name = "PLX Technology"
  }

  case object MassanaDesign extends ManufacturerIdentificationCode {
    val code: Byte = 0xCE.toByte
    val name = "Massana Design"
  }

  case object Intrinsity extends ManufacturerIdentificationCode {
    val code: Byte = 0x4F.toByte
    val name = "Intrinsity"
  }

  case object ValenceSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xD0.toByte
    val name = "Valence Semiconductor"
  }

  case object TerawaveCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x51.toByte
    val name = "Terawave Communications"
  }

  case object IceFyreSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x52.toByte
    val name = "IceFyre Semiconductor"
  }

  case object Primarion extends ManufacturerIdentificationCode {
    val code: Byte = 0xD3.toByte
    val name = "Primarion"
  }

  case object PicochipDesignsLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x54.toByte
    val name = "Picochip Designs Ltd"
  }

  case object SilverbackSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0xD5.toByte
    val name = "Silverback Systems"
  }

  case object JadeStarTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xD6.toByte
    val name = "Jade Star Technologies"
  }

  case object PijnenburgSecurealink extends ManufacturerIdentificationCode {
    val code: Byte = 0x57.toByte
    val name = "Pijnenburg Securealink"
  }

  case object TakeMsUltronAg extends ManufacturerIdentificationCode {
    val code: Byte = 0x58.toByte
    val name = "takeMS - Ultron AG"
  }

  case object CambridgeSiliconRadio extends ManufacturerIdentificationCode {
    val code: Byte = 0xD9.toByte
    val name = "Cambridge Silicon Radio"
  }

  case object Swissbit extends ManufacturerIdentificationCode {
    val code: Byte = 0xDA.toByte
    val name = "Swissbit"
  }

  case object NazomiCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x5B.toByte
    val name = "Nazomi Communications"
  }

  case object EwaveSystem extends ManufacturerIdentificationCode {
    val code: Byte = 0xDC.toByte
    val name = "eWave System"
  }

  case object RockwellCollins extends ManufacturerIdentificationCode {
    val code: Byte = 0x5D.toByte
    val name = "Rockwell Collins"
  }

  case object PicocelCoLtdPaion extends ManufacturerIdentificationCode {
    val code: Byte = 0x5E.toByte
    val name = "Picocel Co. Ltd. (Paion)"
  }

  case object AlphamosaicLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xDF.toByte
    val name = "Alphamosaic Ltd"
  }

  case object Sandburst extends ManufacturerIdentificationCode {
    val code: Byte = 0xE0.toByte
    val name = "Sandburst"
  }

  case object SiConVideo extends ManufacturerIdentificationCode {
    val code: Byte = 0x61.toByte
    val name = "SiCon Video"
  }

  case object NanoAmpSolutions extends ManufacturerIdentificationCode {
    val code: Byte = 0x62.toByte
    val name = "NanoAmp Solutions"
  }

  case object EricssonTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xE3.toByte
    val name = "Ericsson Technology"
  }

  case object PrairieComm extends ManufacturerIdentificationCode {
    val code: Byte = 0x64.toByte
    val name = "PrairieComm"
  }

  case object MitacInternational extends ManufacturerIdentificationCode {
    val code: Byte = 0xE5.toByte
    val name = "Mitac International"
  }

  case object LayerNNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xE6.toByte
    val name = "Layer N Networks"
  }

  case object MtekVisionAtsana extends ManufacturerIdentificationCode {
    val code: Byte = 0x67.toByte
    val name = "MtekVision (Atsana)"
  }

  case object AllegroNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x68.toByte
    val name = "Allegro Networks"
  }

  case object MarvellSemiconductors extends ManufacturerIdentificationCode {
    val code: Byte = 0xE9.toByte
    val name = "Marvell Semiconductors"
  }

  case object NetergyMicroelectronic extends ManufacturerIdentificationCode {
    val code: Byte = 0xEA.toByte
    val name = "Netergy Microelectronic"
  }

  case object Nvidia extends ManufacturerIdentificationCode {
    val code: Byte = 0x6B.toByte
    val name = "NVIDIA"
  }

  case object InternetMachines extends ManufacturerIdentificationCode {
    val code: Byte = 0xEC.toByte
    val name = "Internet Machines"
  }

  case object MemorysolutionGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0x6D.toByte
    val name = "Memorysolution GmbH"
  }

  case object LitchfieldCommunication extends ManufacturerIdentificationCode {
    val code: Byte = 0x6E.toByte
    val name = "Litchfield Communication"
  }

  case object AcctonTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xEF.toByte
    val name = "Accton Technology"
  }

  case object TeradiantNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x70.toByte
    val name = "Teradiant Networks"
  }

  case object ScaleoChip extends ManufacturerIdentificationCode {
    val code: Byte = 0xF1.toByte
    val name = "Scaleo Chip"
  }

  case object CortinaSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0xF2.toByte
    val name = "Cortina Systems"
  }

  case object RamComponents extends ManufacturerIdentificationCode {
    val code: Byte = 0x73.toByte
    val name = "RAM Components"
  }

  case object RaqiaNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xF4.toByte
    val name = "Raqia Networks"
  }

  case object ClearSpeed extends ManufacturerIdentificationCode {
    val code: Byte = 0x75.toByte
    val name = "ClearSpeed"
  }

  case object MatsushitaBattery extends ManufacturerIdentificationCode {
    val code: Byte = 0x76.toByte
    val name = "Matsushita Battery"
  }

  case object Xelerated extends ManufacturerIdentificationCode {
    val code: Byte = 0xF7.toByte
    val name = "Xelerated"
  }

  case object SimpleTech extends ManufacturerIdentificationCode {
    val code: Byte = 0xF8.toByte
    val name = "SimpleTech"
  }

  case object UtronTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x79.toByte
    val name = "Utron Technology"
  }

  case object AstecInternational extends ManufacturerIdentificationCode {
    val code: Byte = 0x7A.toByte
    val name = "Astec International"
  }

  case object AvmGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0xFB.toByte
    val name = "AVM gmbH"
  }

  case object ReduxCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x7C.toByte
    val name = "Redux Communications"
  }

  case object DotHillSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0xFD.toByte
    val name = "Dot Hill Systems"
  }

  case object TeraChip extends ManufacturerIdentificationCode {
    val code: Byte = 0xFE.toByte
    val name = "TeraChip"
  }

}