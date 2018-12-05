package com.cyclone.ipmi.sdr.jedec

// JEDEC Manufacturer lookup
// See STANDARD MANUFACTURERS IDENTIFICATION CODE at https://www.jedec.org/
// This code is based on document JEP106AT published Spe 2016

/////////////////////////////////////////////////////////////////////////////
// Bank 1
/////////////////////////////////////////////////////////////////////////////

object Bank02 {

  val bank: PartialFunction[Byte, ManufacturerIdentificationCode] = {

    case CirrusLogic.code                             => CirrusLogic
    case NationalInstruments.code                     => NationalInstruments
    case IlcDataDevice.code                           => IlcDataDevice
    case AlcatelMietec.code                           => AlcatelMietec
    case MicroLinear.code                             => MicroLinear
    case UnivOfNc.code                                => UnivOfNc
    case JtagTechnologies.code                        => JtagTechnologies
    case BaeSystemsLoral.code                         => BaeSystemsLoral
    case Nchip.code                                   => Nchip
    case GalileoTech.code                             => GalileoTech
    case BestlinkSystems.code                         => BestlinkSystems
    case Graychip.code                                => Graychip
    case Gennum.code                                  => Gennum
    case VideoLogic.code                              => VideoLogic
    case RobertBosch.code                             => RobertBosch
    case ChipExpress.code                             => ChipExpress
    case Dataram.code                                 => Dataram
    case UnitedMicroelectronicsCorp.code              => UnitedMicroelectronicsCorp
    case Tcsi.code                                    => Tcsi
    case SmartModular.code                            => SmartModular
    case HughesAircraft.code                          => HughesAircraft
    case LanstarSemiconductor.code                    => LanstarSemiconductor
    case Qlogic.code                                  => Qlogic
    case Kingston.code                                => Kingston
    case MusicSemi.code                               => MusicSemi
    case EricssonComponents.code                      => EricssonComponents
    case SpaSe.code                                   => SpaSe
    case EonSiliconDevices.code                       => EonSiliconDevices
    case IntegratedSiliconSolutionIssi.code           => IntegratedSiliconSolutionIssi
    case DoD.code                                     => DoD
    case IntegMemoriesTech.code                       => IntegMemoriesTech
    case CorollaryInc.code                            => CorollaryInc
    case DallasSemiconductor.code                     => DallasSemiconductor
    case Omnivision.code                              => Omnivision
    case EivSwitzerland.code                          => EivSwitzerland
    case NovatelWireless.code                         => NovatelWireless
    case ZarlinkMitel.code                            => ZarlinkMitel
    case Clearpoint.code                              => Clearpoint
    case Cabletron.code                               => Cabletron
    case StecSiliconTech.code                         => StecSiliconTech
    case Vanguard.code                                => Vanguard
    case HagiwaraSysCom.code                          => HagiwaraSysCom
    case Vantis.code                                  => Vantis
    case Celestica.code                               => Celestica
    case Century.code                                 => Century
    case HalComputers.code                            => HalComputers
    case RohmCompanyLtd.code                          => RohmCompanyLtd
    case JuniperNetworks.code                         => JuniperNetworks
    case LibitSignalProcessing.code                   => LibitSignalProcessing
    case MushkinEnhancedMemory.code                   => MushkinEnhancedMemory
    case TundraSemiconductor.code                     => TundraSemiconductor
    case AdaptecInc.code                              => AdaptecInc
    case LightSpeedSemi.code                          => LightSpeedSemi
    case ZspCorp.code                                 => ZspCorp
    case AmicTechnology.code                          => AmicTechnology
    case AdobeSystems.code                            => AdobeSystems
    case Dynachip.code                                => Dynachip
    case PnyTechnologiesInc.code                      => PnyTechnologiesInc
    case NewportDigital.code                          => NewportDigital
    case MmcNetworks.code                             => MmcNetworks
    case Tsquare.code                                 => Tsquare
    case SeikoEpson.code                              => SeikoEpson
    case Broadcom.code                                => Broadcom
    case VikingComponents.code                        => VikingComponents
    case V3Semiconductor.code                         => V3Semiconductor
    case FlextronicsOrbitSemiconductor.code           => FlextronicsOrbitSemiconductor
    case SuwaElectronics.code                         => SuwaElectronics
    case Transmeta.code                               => Transmeta
    case MicronCms.code                               => MicronCms
    case AmericanComputerAndDigitalComponentsInc.code => AmericanComputerAndDigitalComponentsInc
    case Enhance3000Inc.code                          => Enhance3000Inc
    case TowerSemiconductor.code                      => TowerSemiconductor
    case CPUDesign.code                               => CPUDesign
    case PricePoint.code                              => PricePoint
    case MaximIntegratedProduct.code                  => MaximIntegratedProduct
    case Tellabs.code                                 => Tellabs
    case CentaurTechnology.code                       => CentaurTechnology
    case UnigenCorporation.code                       => UnigenCorporation
    case TranscendInformation.code                    => TranscendInformation
    case MemoryCardTechnology.code                    => MemoryCardTechnology
    case CkdCorporationLtd.code                       => CkdCorporationLtd
    case CapitalInstrumentsInc.code                   => CapitalInstrumentsInc
    case AicaKogyoLtd.code                            => AicaKogyoLtd
    case LinvexTechnology.code                        => LinvexTechnology
    case MscVertriebsGmbH.code                        => MscVertriebsGmbH
    case AkmCompanyLtd.code                           => AkmCompanyLtd
    case DynamemInc.code                              => DynamemInc
    case NeraAsa.code                                 => NeraAsa
    case GsiTechnology.code                           => GsiTechnology
    case DaneElecCMemory.code                         => DaneElecCMemory
    case AcornComputers.code                          => AcornComputers
    case LaraTechnology.code                          => LaraTechnology
    case OakTechnologyInc.code                        => OakTechnologyInc
    case ItecMemory.code                              => ItecMemory
    case TanisysTechnology.code                       => TanisysTechnology
    case Truevision.code                              => Truevision
    case WintecIndustries.code                        => WintecIndustries
    case SuperPcMemory.code                           => SuperPcMemory
    case MgvMemory.code                               => MgvMemory
    case Galvantech.code                              => Galvantech
    case GadzooxNetworks.code                         => GadzooxNetworks
    case MultiDimensionalCons.code                    => MultiDimensionalCons
    case GateField.code                               => GateField
    case IntegratedMemorySystem.code                  => IntegratedMemorySystem
    case Triscend.code                                => Triscend
    case XaQti.code                                   => XaQti
    case Goldenram.code                               => Goldenram
    case ClearLogic.code                              => ClearLogic
    case CimaronCommunications.code                   => CimaronCommunications
    case NipponSteelSemiCorp.code                     => NipponSteelSemiCorp
    case AdvantageMemory.code                         => AdvantageMemory
    case Amcc.code                                    => Amcc
    case LeCroy.code                                  => LeCroy
    case YamahaCorporation.code                       => YamahaCorporation
    case DigitalMicrowave.code                        => DigitalMicrowave
    case NetLogicMicrosystems.code                    => NetLogicMicrosystems
    case MimosSemiconductor.code                      => MimosSemiconductor
    case AdvancedFibre.code                           => AdvancedFibre
    case BfGoodrichData.code                          => BfGoodrichData
    case Epigram.code                                 => Epigram
    case AcbelPolytechInc.code                        => AcbelPolytechInc
    case ApacerTechnology.code                        => ApacerTechnology
    case AdmorMemory.code                             => AdmorMemory
    case Foxconn.code                                 => Foxconn
    case QuadraticsSuperconductor.code                => QuadraticsSuperconductor
    case ThreeCom.code                                => ThreeCom
  }

  case object CirrusLogic extends ManufacturerIdentificationCode {
    val code: Byte = 0x01.toByte
    val name = "Cirrus Logic"
  }

  case object NationalInstruments extends ManufacturerIdentificationCode {
    val code: Byte = 0x02.toByte
    val name = "National Instruments"
  }

  case object IlcDataDevice extends ManufacturerIdentificationCode {
    val code: Byte = 0x83.toByte
    val name = "ILC Data Device"
  }

  case object AlcatelMietec extends ManufacturerIdentificationCode {
    val code: Byte = 0x04.toByte
    val name = "Alcatel Mietec"
  }

  case object MicroLinear extends ManufacturerIdentificationCode {
    val code: Byte = 0x85.toByte
    val name = "Micro Linear"
  }

  case object UnivOfNc extends ManufacturerIdentificationCode {
    val code: Byte = 0x86.toByte
    val name = "Univ. of NC"
  }

  case object JtagTechnologies extends ManufacturerIdentificationCode {
    val code: Byte = 0x07.toByte
    val name = "JTAG Technologies"
  }

  case object BaeSystemsLoral extends ManufacturerIdentificationCode {
    val code: Byte = 0x08.toByte
    val name = "BAE Systems (Loral)"
  }

  case object Nchip extends ManufacturerIdentificationCode {
    val code: Byte = 0x89.toByte
    val name = "Nchip"
  }

  case object GalileoTech extends ManufacturerIdentificationCode {
    val code: Byte = 0x8A.toByte
    val name = "Galileo Tech"
  }

  case object BestlinkSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x0B.toByte
    val name = "Bestlink Systems"
  }

  case object Graychip extends ManufacturerIdentificationCode {
    val code: Byte = 0x8C.toByte
    val name = "Graychip"
  }

  case object Gennum extends ManufacturerIdentificationCode {
    val code: Byte = 0x0D.toByte
    val name = "GENNUM"
  }

  case object VideoLogic extends ManufacturerIdentificationCode {
    val code: Byte = 0x0E.toByte
    val name = "VideoLogic"
  }

  case object RobertBosch extends ManufacturerIdentificationCode {
    val code: Byte = 0x8F.toByte
    val name = "Robert Bosch"
  }

  case object ChipExpress extends ManufacturerIdentificationCode {
    val code: Byte = 0x10.toByte
    val name = "Chip Express"
  }

  case object Dataram extends ManufacturerIdentificationCode {
    val code: Byte = 0x91.toByte
    val name = "DATARAM"
  }

  case object UnitedMicroelectronicsCorp extends ManufacturerIdentificationCode {
    val code: Byte = 0x92.toByte
    val name = "United Microelectronics Corp."
  }

  case object Tcsi extends ManufacturerIdentificationCode {
    val code: Byte = 0x13.toByte
    val name = "TCSI"
  }

  case object SmartModular extends ManufacturerIdentificationCode {
    val code: Byte = 0x94.toByte
    val name = "Smart Modular"
  }

  case object HughesAircraft extends ManufacturerIdentificationCode {
    val code: Byte = 0x15.toByte
    val name = "Hughes Aircraft"
  }

  case object LanstarSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x16.toByte
    val name = "Lanstar Semiconductor"
  }

  case object Qlogic extends ManufacturerIdentificationCode {
    val code: Byte = 0x97.toByte
    val name = "Qlogic"
  }

  case object Kingston extends ManufacturerIdentificationCode {
    val code: Byte = 0x98.toByte
    val name = "Kingston"
  }

  case object MusicSemi extends ManufacturerIdentificationCode {
    val code: Byte = 0x19.toByte
    val name = "Music Semi"
  }

  case object EricssonComponents extends ManufacturerIdentificationCode {
    val code: Byte = 0x1A.toByte
    val name = "Ericsson Components"
  }

  case object SpaSe extends ManufacturerIdentificationCode {
    val code: Byte = 0x9B.toByte
    val name = "SpaSE"
  }

  case object EonSiliconDevices extends ManufacturerIdentificationCode {
    val code: Byte = 0x1C.toByte
    val name = "Eon Silicon Devices"
  }

  case object IntegratedSiliconSolutionIssi extends ManufacturerIdentificationCode {
    val code: Byte = 0x9D.toByte
    val name = "Integrated Silicon Solution (ISSI)"
  }

  case object DoD extends ManufacturerIdentificationCode {
    val code: Byte = 0x9E.toByte
    val name = "DoD"
  }

  case object IntegMemoriesTech extends ManufacturerIdentificationCode {
    val code: Byte = 0x1F.toByte
    val name = "Integ. Memories Tech."
  }

  case object CorollaryInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x20.toByte
    val name = "Corollary Inc."
  }

  case object DallasSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xA1.toByte
    val name = "Dallas Semiconductor"
  }

  case object Omnivision extends ManufacturerIdentificationCode {
    val code: Byte = 0xA2.toByte
    val name = "Omnivision"
  }

  case object EivSwitzerland extends ManufacturerIdentificationCode {
    val code: Byte = 0x23.toByte
    val name = "EIV(Switzerland)"
  }

  case object NovatelWireless extends ManufacturerIdentificationCode {
    val code: Byte = 0xA4.toByte
    val name = "Novatel Wireless"
  }

  case object ZarlinkMitel extends ManufacturerIdentificationCode {
    val code: Byte = 0x25.toByte
    val name = "Zarlink (Mitel)"
  }

  case object Clearpoint extends ManufacturerIdentificationCode {
    val code: Byte = 0x26.toByte
    val name = "Clearpoint"
  }

  case object Cabletron extends ManufacturerIdentificationCode {
    val code: Byte = 0xA7.toByte
    val name = "Cabletron"
  }

  case object StecSiliconTech extends ManufacturerIdentificationCode {
    val code: Byte = 0xA8.toByte
    val name = "STEC (Silicon Tech)"
  }

  case object Vanguard extends ManufacturerIdentificationCode {
    val code: Byte = 0x29.toByte
    val name = "Vanguard"
  }

  case object HagiwaraSysCom extends ManufacturerIdentificationCode {
    val code: Byte = 0x2A.toByte
    val name = "Hagiwara Sys-Com"
  }

  case object Vantis extends ManufacturerIdentificationCode {
    val code: Byte = 0xAB.toByte
    val name = "Vantis"
  }

  case object Celestica extends ManufacturerIdentificationCode {
    val code: Byte = 0x2C.toByte
    val name = "Celestica"
  }

  case object Century extends ManufacturerIdentificationCode {
    val code: Byte = 0xAD.toByte
    val name = "Century"
  }

  case object HalComputers extends ManufacturerIdentificationCode {
    val code: Byte = 0xAE.toByte
    val name = "Hal Computers"
  }

  case object RohmCompanyLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x2F.toByte
    val name = "Rohm Company Ltd."
  }

  case object JuniperNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xB0.toByte
    val name = "Juniper Networks"
  }

  case object LibitSignalProcessing extends ManufacturerIdentificationCode {
    val code: Byte = 0x31.toByte
    val name = "Libit Signal Processing"
  }

  case object MushkinEnhancedMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0x32.toByte
    val name = "Mushkin Enhanced Memory"
  }

  case object TundraSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xB3.toByte
    val name = "Tundra Semiconductor"
  }

  case object AdaptecInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x34.toByte
    val name = "Adaptec Inc."
  }

  case object LightSpeedSemi extends ManufacturerIdentificationCode {
    val code: Byte = 0xB5.toByte
    val name = "LightSpeed Semi."
  }

  case object ZspCorp extends ManufacturerIdentificationCode {
    val code: Byte = 0xB6.toByte
    val name = "ZSP Corp."
  }

  case object AmicTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x37.toByte
    val name = "AMIC Technology"
  }

  case object AdobeSystems extends ManufacturerIdentificationCode {
    val code: Byte = 0x38.toByte
    val name = "Adobe Systems"
  }

  case object Dynachip extends ManufacturerIdentificationCode {
    val code: Byte = 0xB9.toByte
    val name = "Dynachip"
  }

  case object PnyTechnologiesInc extends ManufacturerIdentificationCode {
    val code: Byte = 0xBA.toByte
    val name = "PNY Technologies, Inc."
  }

  case object NewportDigital extends ManufacturerIdentificationCode {
    val code: Byte = 0x3B.toByte
    val name = "Newport Digital"
  }

  case object MmcNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xBC.toByte
    val name = "MMC Networks"
  }

  case object Tsquare extends ManufacturerIdentificationCode {
    val code: Byte = 0x3D.toByte
    val name = "T Square"
  }

  case object SeikoEpson extends ManufacturerIdentificationCode {
    val code: Byte = 0x3E.toByte
    val name = "Seiko Epson"
  }

  case object Broadcom extends ManufacturerIdentificationCode {
    val code: Byte = 0xBF.toByte
    val name = "Broadcom"
  }

  case object VikingComponents extends ManufacturerIdentificationCode {
    val code: Byte = 0x40.toByte
    val name = "Viking Components"
  }

  case object V3Semiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xC1.toByte
    val name = "V3 Semiconductor"
  }

  case object FlextronicsOrbitSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xC2.toByte
    val name = "Flextronics (Orbit Semiconductor)"
  }

  case object SuwaElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x43.toByte
    val name = "Suwa Electronics"
  }

  case object Transmeta extends ManufacturerIdentificationCode {
    val code: Byte = 0xC4.toByte
    val name = "Transmeta"
  }

  case object MicronCms extends ManufacturerIdentificationCode {
    val code: Byte = 0x45.toByte
    val name = "Micron CMS"
  }

  case object AmericanComputerAndDigitalComponentsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x46.toByte
    val name = "American Computer & Digital Components Inc."
  }

  case object Enhance3000Inc extends ManufacturerIdentificationCode {
    val code: Byte = 0xC7.toByte
    val name = "Enhance 3000 Inc."
  }

  case object TowerSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xC8.toByte
    val name = "Tower Semiconductor"
  }

  case object CPUDesign extends ManufacturerIdentificationCode {
    val code: Byte = 0x49.toByte
    val name = "CPU Design"
  }

  case object PricePoint extends ManufacturerIdentificationCode {
    val code: Byte = 0x4A.toByte
    val name = "Price Point"
  }

  case object MaximIntegratedProduct extends ManufacturerIdentificationCode {
    val code: Byte = 0xCB.toByte
    val name = "Maxim Integrated Product"
  }

  case object Tellabs extends ManufacturerIdentificationCode {
    val code: Byte = 0x4C.toByte
    val name = "Tellabs"
  }

  case object CentaurTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xCD.toByte
    val name = "Centaur Technology"
  }

  case object UnigenCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xCE.toByte
    val name = "Unigen Corporation"
  }

  case object TranscendInformation extends ManufacturerIdentificationCode {
    val code: Byte = 0x4F.toByte
    val name = "Transcend Information"
  }

  case object MemoryCardTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xD0.toByte
    val name = "Memory Card Technology"
  }

  case object CkdCorporationLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x51.toByte
    val name = "CKD Corporation Ltd."
  }

  case object CapitalInstrumentsInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x52.toByte
    val name = "Capital Instruments, Inc."
  }

  case object AicaKogyoLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xD3.toByte
    val name = "Aica Kogyo, Ltd."
  }

  case object LinvexTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x54.toByte
    val name = "Linvex Technology"
  }

  case object MscVertriebsGmbH extends ManufacturerIdentificationCode {
    val code: Byte = 0xD5.toByte
    val name = "MSC Vertriebs GmbH"
  }

  case object AkmCompanyLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0xD6.toByte
    val name = "AKM Company, Ltd."
  }

  case object DynamemInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x57.toByte
    val name = "Dynamem, Inc."
  }

  case object NeraAsa extends ManufacturerIdentificationCode {
    val code: Byte = 0x58.toByte
    val name = "NERA ASA"
  }

  case object GsiTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xD9.toByte
    val name = "GSI Technology"
  }

  case object DaneElecCMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0xDA.toByte
    val name = "Dane-Elec (C Memory)"
  }

  case object AcornComputers extends ManufacturerIdentificationCode {
    val code: Byte = 0x5B.toByte
    val name = "Acorn Computers"
  }

  case object LaraTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xDC.toByte
    val name = "Lara Technology"
  }

  case object OakTechnologyInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x5D.toByte
    val name = "Oak Technology, Inc."
  }

  case object ItecMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0x5E.toByte
    val name = "Itec Memory"
  }

  case object TanisysTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xDF.toByte
    val name = "Tanisys Technology"
  }

  case object Truevision extends ManufacturerIdentificationCode {
    val code: Byte = 0xE0.toByte
    val name = "Truevision"
  }

  case object WintecIndustries extends ManufacturerIdentificationCode {
    val code: Byte = 0x61.toByte
    val name = "Wintec Industries"
  }

  case object SuperPcMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0x62.toByte
    val name = "Super PC Memory"
  }

  case object MgvMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0xE3.toByte
    val name = "MGV Memory"
  }

  case object Galvantech extends ManufacturerIdentificationCode {
    val code: Byte = 0x64.toByte
    val name = "Galvantech"
  }

  case object GadzooxNetworks extends ManufacturerIdentificationCode {
    val code: Byte = 0xE5.toByte
    val name = "Gadzoox Networks"
  }

  case object MultiDimensionalCons extends ManufacturerIdentificationCode {
    val code: Byte = 0xE6.toByte
    val name = "Multi Dimensional Cons."
  }

  case object GateField extends ManufacturerIdentificationCode {
    val code: Byte = 0x67.toByte
    val name = "GateField"
  }

  case object IntegratedMemorySystem extends ManufacturerIdentificationCode {
    val code: Byte = 0x68.toByte
    val name = "Integrated Memory System"
  }

  case object Triscend extends ManufacturerIdentificationCode {
    val code: Byte = 0xE9.toByte
    val name = "Triscend"
  }

  case object XaQti extends ManufacturerIdentificationCode {
    val code: Byte = 0xEA.toByte
    val name = "XaQti"
  }

  case object Goldenram extends ManufacturerIdentificationCode {
    val code: Byte = 0x6B.toByte
    val name = "Goldenram"
  }

  case object ClearLogic extends ManufacturerIdentificationCode {
    val code: Byte = 0xEC.toByte
    val name = "Clear Logic"
  }

  case object CimaronCommunications extends ManufacturerIdentificationCode {
    val code: Byte = 0x6D.toByte
    val name = "Cimaron Communications"
  }

  case object NipponSteelSemiCorp extends ManufacturerIdentificationCode {
    val code: Byte = 0x6E.toByte
    val name = "Nippon Steel Semi. Corp."
  }

  case object AdvantageMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0xEF.toByte
    val name = "Advantage Memory"
  }

  case object Amcc extends ManufacturerIdentificationCode {
    val code: Byte = 0x70.toByte
    val name = "AMCC"
  }

  case object LeCroy extends ManufacturerIdentificationCode {
    val code: Byte = 0xF1.toByte
    val name = "LeCroy"
  }

  case object YamahaCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xF2.toByte
    val name = "Yamaha Corporation"
  }

  case object DigitalMicrowave extends ManufacturerIdentificationCode {
    val code: Byte = 0x73.toByte
    val name = "Digital Microwave"
  }

  case object NetLogicMicrosystems extends ManufacturerIdentificationCode {
    val code: Byte = 0xF4.toByte
    val name = "NetLogic Microsystems"
  }

  case object MimosSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x75.toByte
    val name = "MIMOS Semiconductor"
  }

  case object AdvancedFibre extends ManufacturerIdentificationCode {
    val code: Byte = 0x76.toByte
    val name = "Advanced Fibre"
  }

  case object BfGoodrichData extends ManufacturerIdentificationCode {
    val code: Byte = 0xF7.toByte
    val name = "BF Goodrich Data."
  }

  case object Epigram extends ManufacturerIdentificationCode {
    val code: Byte = 0xF8.toByte
    val name = "Epigram"
  }

  case object AcbelPolytechInc extends ManufacturerIdentificationCode {
    val code: Byte = 0x79.toByte
    val name = "Acbel Polytech Inc."
  }

  case object ApacerTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x7A.toByte
    val name = "Apacer Technology"
  }

  case object AdmorMemory extends ManufacturerIdentificationCode {
    val code: Byte = 0xFB.toByte
    val name = "Admor Memory"
  }

  case object Foxconn extends ManufacturerIdentificationCode {
    val code: Byte = 0x7C.toByte
    val name = "FOXCONN"
  }

  case object QuadraticsSuperconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xFD.toByte
    val name = "Quadratics Superconductor"
  }

  case object ThreeCom extends ManufacturerIdentificationCode {
    val code: Byte = 0xFE.toByte
    val name = "3COM"
  }

}