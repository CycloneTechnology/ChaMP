package com.cyclone.ipmi.sdr.jedec

// JEDEC Manufacturer lookup
// See STANDARD MANUFACTURERS IDENTIFICATION CODE at https://www.jedec.org/
// This code is based on document JEP106AT published Spe 2016

object Bank05 {

  val bank: PartialFunction[Byte, ManufacturerIdentificationCode] = {

    case TRamIncorporated.code       => TRamIncorporated
    case InnovicsWireless.code       => InnovicsWireless
    case Teknovus.code               => Teknovus
    case KeyEyeCommunications.code   => KeyEyeCommunications
    case RuncomTechnologies.code     => RuncomTechnologies
    case RedSwitch.code              => RedSwitch
    case Dotcast.code                => Dotcast
    case SiliconMountainMemory.code  => SiliconMountainMemory
    case SigniaTechnologies.code     => SigniaTechnologies
    case Pixim.code                  => Pixim
    case GalazarNetworks.code        => GalazarNetworks
    case WhiteElectronicDesigns.code => WhiteElectronicDesigns
    case PatriotScientific.code      => PatriotScientific
    case NeoaxiomCorporation.code    => NeoaxiomCorporation
    case ThreeYPowerTechnology.code  => ThreeYPowerTechnology
    case ScaleoChip.code             => ScaleoChip
    case PotentiaPowerSystems.code   => PotentiaPowerSystems
    case CguysIncorporated.code      => CguysIncorporated
    case DigitalCommunicationsTechnologyIncorporated.code =>
      DigitalCommunicationsTechnologyIncorporated
    case SiliconBasedTechnology.code           => SiliconBasedTechnology
    case FulcrumMicrosystems.code              => FulcrumMicrosystems
    case PositivoInformaticaLtd.code           => PositivoInformaticaLtd
    case XIOtechCorporation.code               => XIOtechCorporation
    case PortalPlayer.code                     => PortalPlayer
    case ZhiyingSoftware.code                  => ZhiyingSoftware
    case ParkerVisionInc.code                  => ParkerVisionInc
    case PhonexBroadband.code                  => PhonexBroadband
    case SkyworksSolutions.code                => SkyworksSolutions
    case EntropicCommunications.code           => EntropicCommunications
    case ImIntelligentMemoryLtd.code           => ImIntelligentMemoryLtd
    case ZensysAs.code                         => ZensysAs
    case LegendSiliconCorp.code                => LegendSiliconCorp
    case SciWorxGmbH.code                      => SciWorxGmbH
    case SmscStandardMicrosystems.code         => SmscStandardMicrosystems
    case RenesasElectronics.code               => RenesasElectronics
    case RazaMicroelectronics.code             => RazaMicroelectronics
    case Phyworks.code                         => Phyworks
    case MediaTek.code                         => MediaTek
    case NonCentsProductions.code              => NonCentsProductions
    case UsModular.code                        => UsModular
    case WintegraLtd.code                      => WintegraLtd
    case Mathstar.code                         => Mathstar
    case StarCore.code                         => StarCore
    case OplusTechnologies.code                => OplusTechnologies
    case Mindspeed.code                        => Mindspeed
    case JustYoungComputer.code                => JustYoungComputer
    case RadiaCommunications.code              => RadiaCommunications
    case Ocz.code                              => Ocz
    case Emuzed.code                           => Emuzed
    case LogicDevices.code                     => LogicDevices
    case InphiCorporation.code                 => InphiCorporation
    case QuakeTechnologies.code                => QuakeTechnologies
    case Vixel.code                            => Vixel
    case SolusTek.code                         => SolusTek
    case KongsbergMaritime.code                => KongsbergMaritime
    case FaradayTechnology.code                => FaradayTechnology
    case AltiumLtd.code                        => AltiumLtd
    case Insyte.code                           => Insyte
    case ArmLtd.code                           => ArmLtd
    case DigiVision.code                       => DigiVision
    case VativTechnologies.code                => VativTechnologies
    case EndicottInterconnectTechnologies.code => EndicottInterconnectTechnologies
    case Pericom.code                          => Pericom
    case Bandspeed.code                        => Bandspeed
    case LeWizCommunications.code              => LeWizCommunications
    case CpuTechnology.code                    => CpuTechnology
    case RamaxelTechnology.code                => RamaxelTechnology
    case DspGroup.code                         => DspGroup
    case AxisCommunications.code               => AxisCommunications
    case LegacyElectronics.code                => LegacyElectronics
    case Chrontel.code                         => Chrontel
    case PowerchipSemiconductor.code           => PowerchipSemiconductor
    case MobilEyeTechnologies.code             => MobilEyeTechnologies
    case ExcelSemiconductor.code               => ExcelSemiconductor
    case ADATATechnology.code                  => ADATATechnology
    case VirtualDigm.code                      => VirtualDigm
    case GSkillIntl.code                       => GSkillIntl
    case QuantaComputer.code                   => QuantaComputer
    case YieldMicroelectronics.code            => YieldMicroelectronics
    case AfaTechnologies.code                  => AfaTechnologies
    case KingboxTechnologyCoLtd.code           => KingboxTechnologyCoLtd
    case Ceva.code                             => Ceva
    case IstorNetworks.code                    => IstorNetworks
    case AdvanceModules.code                   => AdvanceModules
    case Microsoft.code                        => Microsoft
    case OpenSilicon.code                      => OpenSilicon
    case GoalSemiconductor.code                => GoalSemiconductor
    case ArcInternational.code                 => ArcInternational
    case Simmtec.code                          => Simmtec
    case Metanoia.code                         => Metanoia
    case KeyStream.code                        => KeyStream
    case LowranceElectronics.code              => LowranceElectronics
    case Adimos.code                           => Adimos
    case SiGeSemiconductor.code                => SiGeSemiconductor
    case FodusCommunications.code              => FodusCommunications
    case CredenceSystemsCorp.code              => CredenceSystemsCorp
    case GenesisMicrochipInc.code              => GenesisMicrochipInc
    case VihanaInc.code                        => VihanaInc
    case WisTechnologies.code                  => WisTechnologies
    case GateChangeTechnologies.code           => GateChangeTechnologies
    case HighDensityDevicesAs.code             => HighDensityDevicesAs
    case Synopsys.code                         => Synopsys
    case Gigaram.code                          => Gigaram
    case EnigmaSemiconductorInc.code           => EnigmaSemiconductorInc
    case CenturyMicroInc.code                  => CenturyMicroInc
    case IceraSemiconductor.code               => IceraSemiconductor
    case MediaworksIntegratedSystems.code      => MediaworksIntegratedSystems
    case ONeilProductDevelopment.code          => ONeilProductDevelopment
    case SupremeTopTechnologyLtd.code          => SupremeTopTechnologyLtd
    case MicroDisplayCorporation.code          => MicroDisplayCorporation
    case TeamGroupInc.code                     => TeamGroupInc
    case SinettCorporation.code                => SinettCorporation
    case ToshibaCorporation.code               => ToshibaCorporation
    case Tensilica.code                        => Tensilica
    case SiRFTechnology.code                   => SiRFTechnology
    case BacocInc.code                         => BacocInc
    case SMaLCameraTechnologies.code           => SMaLCameraTechnologies
    case ThomsonSc.code                        => ThomsonSc
    case AirgoNetworks.code                    => AirgoNetworks
    case WisairLtd.code                        => WisairLtd
    case SigmaTel.code                         => SigmaTel
    case Arkados.code                          => Arkados
    case CompeteItGmbHCoKg.code                => CompeteItGmbHCoKg
    case EudarTechnologyInc.code               => EudarTechnologyInc
    case FocusEnhancements.code                => FocusEnhancements
    case Xyratex.code                          => Xyratex
  }

  case object TRamIncorporated extends ManufacturerIdentificationCode {
    val code: Byte = 0x01.toByte
    val name = "T-RAM Incorporated"
  }

  case object InnovicsWireless extends ManufacturerIdentificationCode {
    val code: Byte = 0x02.toByte
    val name = "Innovics Wireless"
  }

  case object Teknovus extends ManufacturerIdentificationCode {
    val code: Byte = 0x83.toByte
    val name = "Teknovus"
  }

  case object KeyEyeCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x04.toByte
    val name = "KeyEye Communications"
  }

  case object RuncomTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x85.toByte
    val name = "Runcom Technologies"
  }

  case object RedSwitch extends ManufacturerIdentificationCode {
    val code: Byte = 0x86.toByte
    val name = "RedSwitch"
  }

  case object Dotcast extends ManufacturerIdentificationCode {
    val code: Byte = 0x07.toByte
    val name = "Dotcast"
  }

  case object SiliconMountainMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0x08.toByte
    val name = "Silicon Mountain Memory"
  }

  case object SigniaTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x89.toByte
    val name = "Signia Technologies"
  }

  case object Pixim extends ManufacturerIdentificationCode {
    val code: Byte = 0x8A.toByte
    val name = "Pixim"
  }

  case object GalazarNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x0B.toByte
    val name = "Galazar Networks"
  }

  case object WhiteElectronicDesigns extends ManufacturerIdentificationCode {
    val code: Byte = 0x8C.toByte
    val name = "White Electronic Designs"
  }

  case object PatriotScientific extends ManufacturerIdentificationCode {
    val code: Byte = 0x0D.toByte
    val name = "Patriot Scientific"
  }

  case object NeoaxiomCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x0E.toByte
    val name = "Neoaxiom Corporation"
  }

  case object ThreeYPowerTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x8F.toByte
    val name = "3Y Power Technology"
  }

  case object ScaleoChip extends ManufacturerIdentificationCode {
    val code: Byte = 0x10.toByte
    val name = "Scaleo Chip"
  }

  case object PotentiaPowerSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x91.toByte
    val name = "Potentia Power Systems"
  }

  case object CguysIncorporated extends ManufacturerIdentificationCode {
    val code: Byte = 0x92.toByte
    val name = "C-guys Incorporated"
  }

  case object DigitalCommunicationsTechnologyIncorporated extends ManufacturerIdentificationCode {
    val code: Byte = 0x13.toByte
    val name = "Digital Communications Technology Incorporated"
  }

  case object SiliconBasedTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x94.toByte
    val name = "Silicon-Based Technology"
  }

  case object FulcrumMicrosystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x15.toByte
    val name = "Fulcrum Microsystems"
  }

  case object PositivoInformaticaLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x16.toByte
    val name = "Positivo Informatica Ltd"
  }

  case object XIOtechCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x97.toByte
    val name = "XIOtech Corporation"
  }

  case object PortalPlayer extends ManufacturerIdentificationCode {
    val code: Byte = 0x98.toByte
    val name = "PortalPlayer"
  }

  case object ZhiyingSoftware extends ManufacturerIdentificationCode {
    val code: Byte = 0x19.toByte
    val name = "Zhiying Software"
  }

  case object ParkerVisionInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x1A.toByte
    val name = "ParkerVision, Inc."
  }

  case object PhonexBroadband extends ManufacturerIdentificationCode {
    val code: Byte = 0x9B.toByte
    val name = "Phonex Broadband"
  }

  case object SkyworksSolutions extends ManufacturerIdentificationCode {
    val code: Byte = 0x1C.toByte
    val name = "Skyworks Solutions"
  }

  case object EntropicCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x9D.toByte
    val name = "Entropic Communications"
  }

  case object ImIntelligentMemoryLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x9E.toByte
    val name = "I’M Intelligent Memory Ltd."
  }

  case object ZensysAs extends ManufacturerIdentificationCode {
    val code: Byte = 0x1F.toByte
    val name = "Zensys A/S"
  }

  case object LegendSiliconCorp extends ManufacturerIdentificationCode {
    val code: Byte = 0x20.toByte
    val name = "Legend Silicon Corp."
  }

  case object SciWorxGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0xA1.toByte
    val name = "Sci-worx GmbH"
  }

  case object SmscStandardMicrosystems extends ManufacturerIdentificationCode {
    val code: Byte = 0xA2.toByte
    val name = "SMSC (Standard Microsystems)"
  }

  case object RenesasElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x23.toByte
    val name = "Renesas Electronics"
  }

  case object RazaMicroelectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0xA4.toByte
    val name = "Raza Microelectronics"
  }

  case object Phyworks extends ManufacturerIdentificationCode {
    val code: Byte = 0x25.toByte
    val name = "Phyworks"
  }

  case object MediaTek extends ManufacturerIdentificationCode {
    val code: Byte = 0x26.toByte
    val name = "MediaTek"
  }

  case object NonCentsProductions extends ManufacturerIdentificationCode {
    val code: Byte = 0xA7.toByte
    val name = "Non-cents Productions"
  }

  case object UsModular extends ManufacturerIdentificationCode {
    val code: Byte = 0xA8.toByte
    val name = "US Modular"
  }

  case object WintegraLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x29.toByte
    val name = "Wintegra Ltd."
  }

  case object Mathstar extends ManufacturerIdentificationCode {
    val code: Byte = 0x2A.toByte
    val name = "Mathstar"
  }

  case object StarCore extends ManufacturerIdentificationCode {
    val code: Byte = 0xAB.toByte
    val name = "StarCore"
  }

  case object OplusTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x2C.toByte
    val name = "Oplus Technologies"
  }

  case object Mindspeed extends ManufacturerIdentificationCode {
    val code: Byte = 0xAD.toByte
    val name = "Mindspeed"
  }

  case object JustYoungComputer extends ManufacturerIdentificationCode {
    val code: Byte = 0xAE.toByte
    val name = "Just Young Computer"
  }

  case object RadiaCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x2F.toByte
    val name = "Radia Communications"
  }

  case object Ocz extends ManufacturerIdentificationCode {
    val code: Byte = 0xB0.toByte
    val name = "OCZ"
  }

  case object Emuzed extends ManufacturerIdentificationCode {
    val code: Byte = 0x31.toByte
    val name = "Emuzed"
  }

  case object LogicDevices extends ManufacturerIdentificationCode {
    val code: Byte = 0x32.toByte
    val name = "LOGIC Devices"
  }

  case object InphiCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xB3.toByte
    val name = "Inphi Corporation"
  }

  case object QuakeTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x34.toByte
    val name = "Quake Technologies"
  }

  case object Vixel extends ManufacturerIdentificationCode {
    val code: Byte = 0xB5.toByte
    val name = "Vixel"
  }

  case object SolusTek extends ManufacturerIdentificationCode {
    val code: Byte = 0xB6.toByte
    val name = "SolusTek"
  }

  case object KongsbergMaritime extends ManufacturerIdentificationCode {
    val code: Byte = 0x37.toByte
    val name = "Kongsberg Maritime"
  }

  case object FaradayTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x38.toByte
    val name = "Faraday Technology"
  }

  case object AltiumLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xB9.toByte
    val name = "Altium Ltd."
  }

  case object Insyte extends ManufacturerIdentificationCode {
    val code: Byte = 0xBA.toByte
    val name = "Insyte"
  }

  case object ArmLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x3B.toByte
    val name = "ARM Ltd."
  }

  case object DigiVision extends ManufacturerIdentificationCode {
    val code: Byte = 0xBC.toByte
    val name = "DigiVision"
  }

  case object VativTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x3D.toByte
    val name = "Vativ Technologies"
  }

  case object EndicottInterconnectTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x3E.toByte
    val name = "Endicott Interconnect Technologies"
  }

  case object Pericom extends ManufacturerIdentificationCode {
    val code: Byte = 0xBF.toByte
    val name = "Pericom"
  }

  case object Bandspeed extends ManufacturerIdentificationCode {
    val code: Byte = 0x40.toByte
    val name = "Bandspeed"
  }

  case object LeWizCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0xC1.toByte
    val name = "LeWiz Communications"
  }

  case object CpuTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xC2.toByte
    val name = "CPU Technology"
  }

  case object RamaxelTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x43.toByte
    val name = "Ramaxel Technology"
  }

  case object DspGroup extends ManufacturerIdentificationCode {
    val code: Byte = 0xC4.toByte
    val name = "DSP Group"
  }

  case object AxisCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x45.toByte
    val name = "Axis Communications"
  }

  case object LegacyElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x46.toByte
    val name = "Legacy Electronics"
  }

  case object Chrontel extends ManufacturerIdentificationCode {
    val code: Byte = 0xC7.toByte
    val name = "Chrontel"
  }

  case object PowerchipSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xC8.toByte
    val name = "Powerchip Semiconductor"
  }

  case object MobilEyeTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x49.toByte
    val name = "MobilEye Technologies"
  }

  case object ExcelSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x4A.toByte
    val name = "Excel Semiconductor"
  }

  case object ADATATechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xCB.toByte
    val name = "A-DATA Technology"
  }

  case object VirtualDigm extends ManufacturerIdentificationCode {
    val code: Byte = 0x4C.toByte
    val name = "VirtualDigm"
  }

  case object GSkillIntl extends ManufacturerIdentificationCode {
    val code: Byte = 0xCD.toByte
    val name = "G Skill Intl"
  }

  case object QuantaComputer extends ManufacturerIdentificationCode {
    val code: Byte = 0xCE.toByte
    val name = "Quanta Computer"
  }

  case object YieldMicroelectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x4F.toByte
    val name = "Yield Microelectronics"
  }

  case object AfaTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xD0.toByte
    val name = "Afa Technologies"
  }

  case object KingboxTechnologyCoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x51.toByte
    val name = "KINGBOX Technology Co. Ltd."
  }

  case object Ceva extends ManufacturerIdentificationCode {
    val code: Byte = 0x52.toByte
    val name = "Ceva"
  }

  case object IstorNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xD3.toByte
    val name = "iStor Networks"
  }

  case object AdvanceModules extends ManufacturerIdentificationCode {
    val code: Byte = 0x54.toByte
    val name = "Advance Modules"
  }

  case object Microsoft extends ManufacturerIdentificationCode {
    val code: Byte = 0xD5.toByte
    val name = "Microsoft"
  }

  case object OpenSilicon extends ManufacturerIdentificationCode {
    val code: Byte = 0xD6.toByte
    val name = "Open-Silicon"
  }

  case object GoalSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x57.toByte
    val name = "Goal Semiconductor"
  }

  case object ArcInternational extends ManufacturerIdentificationCode {
    val code: Byte = 0x58.toByte
    val name = "ARC International"
  }

  case object Simmtec extends ManufacturerIdentificationCode {
    val code: Byte = 0xD9.toByte
    val name = "Simmtec"
  }

  case object Metanoia extends ManufacturerIdentificationCode {
    val code: Byte = 0xDA.toByte
    val name = "Metanoia"
  }

  case object KeyStream extends ManufacturerIdentificationCode {
    val code: Byte = 0x5B.toByte
    val name = "Key Stream"
  }

  case object LowranceElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0xDC.toByte
    val name = "Lowrance Electronics"
  }

  case object Adimos extends ManufacturerIdentificationCode {
    val code: Byte = 0x5D.toByte
    val name = "Adimos"
  }

  case object SiGeSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x5E.toByte
    val name = "SiGe Semiconductor"
  }

  case object FodusCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0xDF.toByte
    val name = "Fodus Communications"
  }

  case object CredenceSystemsCorp extends ManufacturerIdentificationCode {
    val code: Byte = 0xE0.toByte
    val name = "Credence Systems Corp."
  }

  case object GenesisMicrochipInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x61.toByte
    val name = "Genesis Microchip Inc."
  }

  case object VihanaInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x62.toByte
    val name = "Vihana, Inc."
  }

  case object WisTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0xE3.toByte
    val name = "WIS Technologies"
  }

  case object GateChangeTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x64.toByte
    val name = "GateChange Technologies"
  }

  case object HighDensityDevicesAs extends ManufacturerIdentificationCode {
    val code: Byte = 0xE5.toByte
    val name = "High Density Devices AS"
  }

  case object Synopsys extends ManufacturerIdentificationCode {
    val code: Byte = 0xE6.toByte
    val name = "Synopsys"
  }

  case object Gigaram extends ManufacturerIdentificationCode {
    val code: Byte = 0x67.toByte
    val name = "Gigaram"
  }

  case object EnigmaSemiconductorInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x68.toByte
    val name = "Enigma Semiconductor Inc."
  }

  case object CenturyMicroInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xE9.toByte
    val name = "Century Micro Inc."
  }

  case object IceraSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xEA.toByte
    val name = "Icera Semiconductor"
  }

  case object MediaworksIntegratedSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x6B.toByte
    val name = "Mediaworks Integrated Systems"
  }

  case object ONeilProductDevelopment extends ManufacturerIdentificationCode {
    val code: Byte = 0xEC.toByte
    val name = "O’Neil Product Development"
  }

  case object SupremeTopTechnologyLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x6D.toByte
    val name = "Supreme Top Technology Ltd."
  }

  case object MicroDisplayCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x6E.toByte
    val name = "MicroDisplay Corporation"
  }

  case object TeamGroupInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xEF.toByte
    val name = "Team Group Inc."
  }

  case object SinettCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x70.toByte
    val name = "Sinett Corporation"
  }

  case object ToshibaCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xF1.toByte
    val name = "Toshiba Corporation"
  }

  case object Tensilica extends ManufacturerIdentificationCode {
    val code: Byte = 0xF2.toByte
    val name = "Tensilica"
  }

  case object SiRFTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x73.toByte
    val name = "SiRF Technology"
  }

  case object BacocInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xF4.toByte
    val name = "Bacoc Inc."
  }

  case object SMaLCameraTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x75.toByte
    val name = "SMaL Camera Technologies"
  }

  case object ThomsonSc extends ManufacturerIdentificationCode {
    val code: Byte = 0x76.toByte
    val name = "Thomson SC"
  }

  case object AirgoNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xF7.toByte
    val name = "Airgo Networks"
  }

  case object WisairLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xF8.toByte
    val name = "Wisair Ltd."
  }

  case object SigmaTel extends ManufacturerIdentificationCode {
    val code: Byte = 0x79.toByte
    val name = "SigmaTel"
  }

  case object Arkados extends ManufacturerIdentificationCode {
    val code: Byte = 0x7A.toByte
    val name = "Arkados"
  }

  case object CompeteItGmbHCoKg extends ManufacturerIdentificationCode {
    val code: Byte = 0xFB.toByte
    val name = "Compete IT gmbH Co. KG"
  }

  case object EudarTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x7C.toByte
    val name = "Eudar Technology Inc."
  }

  case object FocusEnhancements extends ManufacturerIdentificationCode {
    val code: Byte = 0xFD.toByte
    val name = "Focus Enhancements"
  }

  case object Xyratex extends ManufacturerIdentificationCode {
    val code: Byte = 0xFE.toByte
    val name = "Xyratex"
  }

}
