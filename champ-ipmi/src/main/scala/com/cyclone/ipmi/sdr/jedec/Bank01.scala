package com.cyclone.ipmi.sdr.jedec

// JEDEC Manufacturer lookup
// See STANDARD MANUFACTURERS IDENTIFICATION CODE at https://www.jedec.org/
// This code is based on document JEP106AT published Spe 2016

object Bank01 {

  val bank: PartialFunction[Byte, ManufacturerIdentificationCode] = {

    case Amd.code                        => Amd
    case Ami.code                        => Ami
    case Fairchild.code                  => Fairchild
    case Fujitsu.code                    => Fujitsu
    case Gte.code                        => Gte
    case Harris.code                     => Harris
    case Hitachi.code                    => Hitachi
    case Inmos.code                      => Inmos
    case Intel.code                      => Intel
    case Itt.code                        => Itt
    case Intersil.code                   => Intersil
    case MonolithicMemories.code         => MonolithicMemories
    case Mostek.code                     => Mostek
    case FreescaleMotorola.code          => FreescaleMotorola
    case National.code                   => National
    case Nec.code                        => Nec
    case Rca.code                        => Rca
    case Raytheon.code                   => Raytheon
    case ConexantRockwell.code           => ConexantRockwell
    case Seeq.code                       => Seeq
    case NxpPhilips.code                 => NxpPhilips
    case Synertek.code                   => Synertek
    case TexasInstruments.code           => TexasInstruments
    case Toshiba.code                    => Toshiba
    case Xicor.code                      => Xicor
    case Zilog.code                      => Zilog
    case Eurotechnique.code              => Eurotechnique
    case Mitsubishi.code                 => Mitsubishi
    case LucentAtt.code                  => LucentAtt
    case Exel.code                       => Exel
    case Atmel.code                      => Atmel
    case StMicroelectronics.code         => StMicroelectronics
    case LatticeSemi.code                => LatticeSemi
    case Ncr.code                        => Ncr
    case WaferScaleIntegration.code      => WaferScaleIntegration
    case Ibm.code                        => Ibm
    case Tristar.code                    => Tristar
    case Visic.code                      => Visic
    case IntlCmosTechnology.code         => IntlCmosTechnology
    case Sssi.code                       => Sssi
    case MicrochipTechnology.code        => MicrochipTechnology
    case RicohLtd.code                   => RicohLtd
    case Vlsi.code                       => Vlsi
    case MicronTechnology.code           => MicronTechnology
    case SkHynix.code                    => SkHynix
    case OkiSemiconductor.code           => OkiSemiconductor
    case Actel.code                      => Actel
    case Sharp.code                      => Sharp
    case Catalyst.code                   => Catalyst
    case Panasonic.code                  => Panasonic
    case Idt.code                        => Idt
    case Cypress.code                    => Cypress
    case Dec.code                        => Dec
    case LsiLogic.code                   => LsiLogic
    case ZarlinkPlessey.code             => ZarlinkPlessey
    case Utmc.code                       => Utmc
    case ThinkingMachine.code            => ThinkingMachine
    case ThomsonCsf.code                 => ThomsonCsf
    case IntegratedCmosVertex.code       => IntegratedCmosVertex
    case Honeywell.code                  => Honeywell
    case Tektronix.code                  => Tektronix
    case OracleCorporation.code          => OracleCorporation
    case SiliconStorageTechnology.code   => SiliconStorageTechnology
    case ProMosMoselVitelic.code         => ProMosMoselVitelic
    case InfineonSiemens.code            => InfineonSiemens
    case Macronix.code                   => Macronix
    case Xerox.code                      => Xerox
    case PlusLogic.code                  => PlusLogic
    case SanDiskCorporation.code         => SanDiskCorporation
    case ElanCircuitTech.code            => ElanCircuitTech
    case EuropeanSiliconStr.code         => EuropeanSiliconStr
    case AppleComputer.code              => AppleComputer
    case Xilinx.code                     => Xilinx
    case Compaq.code                     => Compaq
    case ProtocolEngines.code            => ProtocolEngines
    case Sci.code                        => Sci
    case SeikoInstruments.code           => SeikoInstruments
    case Samsung.code                    => Samsung
    case I3DesignSystem.code             => I3DesignSystem
    case Klic.code                       => Klic
    case CrosspointSolutions.code        => CrosspointSolutions
    case AllianceSemiconductor.code      => AllianceSemiconductor
    case Tandem.code                     => Tandem
    case HewlettPackard.code             => HewlettPackard
    case IntegratedSiliconSolutions.code => IntegratedSiliconSolutions
    case Brooktree.code                  => Brooktree
    case NewMedia.code                   => NewMedia
    case MhsElectronic.code              => MhsElectronic
    case PerformanceSemi.code            => PerformanceSemi
    case WinbondElectronic.code          => WinbondElectronic
    case KawasakiSteel.code              => KawasakiSteel
    case BrightMicro.code                => BrightMicro
    case Tecmar.code                     => Tecmar
    case Exar.code                       => Exar
    case Pcmcia.code                     => Pcmcia
    case LGSemiGoldstar.code             => LGSemiGoldstar
    case NorthernTelecom.code            => NorthernTelecom
    case Sanyo.code                      => Sanyo
    case ArrayMicrosystems.code          => ArrayMicrosystems
    case CrystalSemiconductor.code       => CrystalSemiconductor
    case AnalogDevices.code              => AnalogDevices
    case PmcSierra.code                  => PmcSierra
    case Asparix.code                    => Asparix
    case ConvexComputer.code             => ConvexComputer
    case QualitySemiconductor.code       => QualitySemiconductor
    case NimbusTechnology.code           => NimbusTechnology
    case Transwitch.code                 => Transwitch
    case MicronasIttIntermetall.code     => MicronasIttIntermetall
    case Cannon.code                     => Cannon
    case Altera.code                     => Altera
    case Nexcom.code                     => Nexcom
    case Qualcomm.code                   => Qualcomm
    case Sony.code                       => Sony
    case CrayResearch.code               => CrayResearch
    case AmsAustriaMicro.code            => AmsAustriaMicro
    case Vitesse.code                    => Vitesse
    case AsterElectronics.code           => AsterElectronics
    case BayNetworksSynoptic.code        => BayNetworksSynoptic
    case ZentrumZMD.code                 => ZentrumZMD
    case Trw.code                        => Trw
    case Thesys.code                     => Thesys
    case SolbourneComputer.code          => SolbourneComputer
    case AlliedSignal.code               => AlliedSignal
    case DialogSemiconductor.code        => DialogSemiconductor
    case MediaVision.code                => MediaVision
    case NumonyxCorporation.code         => NumonyxCorporation
  }

  case object Amd extends ManufacturerIdentificationCode {
    val code: Byte = 0x01.toByte
    val name = "AMD"
  }

  case object Ami extends ManufacturerIdentificationCode {
    val code: Byte = 0x02.toByte
    val name = "AMI"
  }

  case object Fairchild extends ManufacturerIdentificationCode {
    val code: Byte = 0x83.toByte
    val name = "Fairchild"
  }

  case object Fujitsu extends ManufacturerIdentificationCode {
    val code: Byte = 0x04.toByte
    val name = "Fujitsu"
  }

  case object Gte extends ManufacturerIdentificationCode {
    val code: Byte = 0x85.toByte
    val name = "GTE"
  }

  case object Harris extends ManufacturerIdentificationCode {
    val code: Byte = 0x86.toByte
    val name = "Harris"
  }

  case object Hitachi extends ManufacturerIdentificationCode {
    val code: Byte = 0x07.toByte
    val name = "Hitachi"
  }

  case object Inmos extends ManufacturerIdentificationCode {
    val code: Byte = 0x08.toByte
    val name = "Inmos"
  }

  case object Intel extends ManufacturerIdentificationCode {
    val code: Byte = 0x89.toByte
    val name = "Intel"
  }

  case object Itt extends ManufacturerIdentificationCode {
    val code: Byte = 0x8A.toByte
    val name = "I.T.T."
  }

  case object Intersil extends ManufacturerIdentificationCode {
    val code: Byte = 0x0B.toByte
    val name = "Intersil"
  }

  case object MonolithicMemories extends ManufacturerIdentificationCode {
    val code: Byte = 0x8C.toByte
    val name = "Monolithic Memories"
  }

  case object Mostek extends ManufacturerIdentificationCode {
    val code: Byte = 0x0D.toByte
    val name = "Mostek"
  }

  case object FreescaleMotorola extends ManufacturerIdentificationCode {
    val code: Byte = 0x0E.toByte
    val name = "Freescale (Motorola)"
  }

  case object National extends ManufacturerIdentificationCode {
    val code: Byte = 0x8F.toByte
    val name = "National"
  }

  case object Nec extends ManufacturerIdentificationCode {
    val code: Byte = 0x10.toByte
    val name = "NEC"
  }

  case object Rca extends ManufacturerIdentificationCode {
    val code: Byte = 0x91.toByte
    val name = "RCA"
  }

  case object Raytheon extends ManufacturerIdentificationCode {
    val code: Byte = 0x92.toByte
    val name = "Raytheon"
  }

  case object ConexantRockwell extends ManufacturerIdentificationCode {
    val code: Byte = 0x13.toByte
    val name = "Conexant (Rockwell)"
  }

  case object Seeq extends ManufacturerIdentificationCode {
    val code: Byte = 0x94.toByte
    val name = "Seeq"
  }

  case object NxpPhilips extends ManufacturerIdentificationCode {
    val code: Byte = 0x15.toByte
    val name = "NXP (Philips)"
  }

  case object Synertek extends ManufacturerIdentificationCode {
    val code: Byte = 0x16.toByte
    val name = "Synertek"
  }

  case object TexasInstruments extends ManufacturerIdentificationCode {
    val code: Byte = 0x97.toByte
    val name = "Texas Instruments"
  }

  case object Toshiba extends ManufacturerIdentificationCode {
    val code: Byte = 0x98.toByte
    val name = "Toshiba"
  }

  case object Xicor extends ManufacturerIdentificationCode {
    val code: Byte = 0x19.toByte
    val name = "Xicor"
  }

  case object Zilog extends ManufacturerIdentificationCode {
    val code: Byte = 0x1A.toByte
    val name = "Zilog"
  }

  case object Eurotechnique extends ManufacturerIdentificationCode {
    val code: Byte = 0x9B.toByte
    val name = "Eurotechnique"
  }

  case object Mitsubishi extends ManufacturerIdentificationCode {
    val code: Byte = 0x1C.toByte
    val name = "Mitsubishi"
  }

  case object LucentAtt extends ManufacturerIdentificationCode {
    val code: Byte = 0x9D.toByte
    val name = "Lucent (AT&T)"
  }

  case object Exel extends ManufacturerIdentificationCode {
    val code: Byte = 0x9E.toByte
    val name = "Exel"
  }

  case object Atmel extends ManufacturerIdentificationCode {
    val code: Byte = 0x1F.toByte
    val name = "Atmel"
  }

  case object StMicroelectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x20.toByte
    val name = "STMicroelectronics"
  }

  case object LatticeSemi extends ManufacturerIdentificationCode {
    val code: Byte = 0xA1.toByte
    val name = "Lattice Semi."
  }

  case object Ncr extends ManufacturerIdentificationCode {
    val code: Byte = 0xA2.toByte
    val name = "NCR"
  }

  case object WaferScaleIntegration extends ManufacturerIdentificationCode {
    val code: Byte = 0x23.toByte
    val name = "Wafer Scale Integration"
  }

  case object Ibm extends ManufacturerIdentificationCode {
    val code: Byte = 0xA4.toByte
    val name = "IBM"
  }

  case object Tristar extends ManufacturerIdentificationCode {
    val code: Byte = 0x25.toByte
    val name = "Tristar"
  }

  case object Visic extends ManufacturerIdentificationCode {
    val code: Byte = 0x26.toByte
    val name = "Visic"
  }

  case object IntlCmosTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xA7.toByte
    val name = "Intl. CMOS Technology"
  }

  case object Sssi extends ManufacturerIdentificationCode {
    val code: Byte = 0xA8.toByte
    val name = "SSSI"
  }

  case object MicrochipTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x29.toByte
    val name = "MicrochipTechnology"
  }

  case object RicohLtd extends ManufacturerIdentificationCode {
    val code: Byte = 0x2A.toByte
    val name = "Ricoh Ltd."
  }

  case object Vlsi extends ManufacturerIdentificationCode {
    val code: Byte = 0xAB.toByte
    val name = "VLSI"
  }

  case object MicronTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0x2C.toByte
    val name = "Micron Technology"
  }

  case object SkHynix extends ManufacturerIdentificationCode {
    val code: Byte = 0xAD.toByte
    val name = "SK Hynix"
  }

  case object OkiSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xAE.toByte
    val name = "OKI Semiconductor"
  }

  case object Actel extends ManufacturerIdentificationCode {
    val code: Byte = 0x2F.toByte
    val name = "ACTEL"
  }

  case object Sharp extends ManufacturerIdentificationCode {
    val code: Byte = 0xB0.toByte
    val name = "Sharp"
  }

  case object Catalyst extends ManufacturerIdentificationCode {
    val code: Byte = 0x31.toByte
    val name = "Catalyst"
  }

  case object Panasonic extends ManufacturerIdentificationCode {
    val code: Byte = 0x32.toByte
    val name = "Panasonic"
  }

  case object Idt extends ManufacturerIdentificationCode {
    val code: Byte = 0xB3.toByte
    val name = "IDT"
  }

  case object Cypress extends ManufacturerIdentificationCode {
    val code: Byte = 0x34.toByte
    val name = "Cypress"
  }

  case object Dec extends ManufacturerIdentificationCode {
    val code: Byte = 0xB5.toByte
    val name = "DEC"
  }

  case object LsiLogic extends ManufacturerIdentificationCode {
    val code: Byte = 0xB6.toByte
    val name = "LSI Logic"
  }

  case object ZarlinkPlessey extends ManufacturerIdentificationCode {
    val code: Byte = 0x37.toByte
    val name = "Zarlink (Plessey)"
  }

  case object Utmc extends ManufacturerIdentificationCode {
    val code: Byte = 0x38.toByte
    val name = "UTMC"
  }

  case object ThinkingMachine extends ManufacturerIdentificationCode {
    val code: Byte = 0xB9.toByte
    val name = "Thinking Machine"
  }

  case object ThomsonCsf extends ManufacturerIdentificationCode {
    val code: Byte = 0xBA.toByte
    val name = "Thomson CSF"
  }

  case object IntegratedCmosVertex extends ManufacturerIdentificationCode {
    val code: Byte = 0x3B.toByte
    val name = "Integrated CMOS (Vertex)"
  }

  case object Honeywell extends ManufacturerIdentificationCode {
    val code: Byte = 0xBC.toByte
    val name = "Honeywell"
  }

  case object Tektronix extends ManufacturerIdentificationCode {
    val code: Byte = 0x3D.toByte
    val name = "Tektronix"
  }

  case object OracleCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x3E.toByte
    val name = "Oracle Corporation"
  }

  case object SiliconStorageTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xBF.toByte
    val name = "Silicon Storage Technology"
  }

  case object ProMosMoselVitelic extends ManufacturerIdentificationCode {
    val code: Byte = 0x40.toByte
    val name = "ProMos/Mosel Vitelic"
  }

  case object InfineonSiemens extends ManufacturerIdentificationCode {
    val code: Byte = 0xC1.toByte
    val name = "Infineon (Siemens)"
  }

  case object Macronix extends ManufacturerIdentificationCode {
    val code: Byte = 0xC2.toByte
    val name = "Macronix"
  }

  case object Xerox extends ManufacturerIdentificationCode {
    val code: Byte = 0x43.toByte
    val name = "Xerox"
  }

  case object PlusLogic extends ManufacturerIdentificationCode {
    val code: Byte = 0xC4.toByte
    val name = "Plus Logic"
  }

  case object SanDiskCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0x45.toByte
    val name = "SanDisk Corporation"
  }

  case object ElanCircuitTech extends ManufacturerIdentificationCode {
    val code: Byte = 0x46.toByte
    val name = "Elan Circuit Tech."
  }

  case object EuropeanSiliconStr extends ManufacturerIdentificationCode {
    val code: Byte = 0xC7.toByte
    val name = "European Silicon Str."
  }

  case object AppleComputer extends ManufacturerIdentificationCode {
    val code: Byte = 0xC8.toByte
    val name = "Apple Computer"
  }

  case object Xilinx extends ManufacturerIdentificationCode {
    val code: Byte = 0x49.toByte
    val name = "Xilinx"
  }

  case object Compaq extends ManufacturerIdentificationCode {
    val code: Byte = 0x4A.toByte
    val name = "Compaq"
  }

  case object ProtocolEngines extends ManufacturerIdentificationCode {
    val code: Byte = 0xCB.toByte
    val name = "Protocol Engines"
  }

  case object Sci extends ManufacturerIdentificationCode {
    val code: Byte = 0x4C.toByte
    val name = "SCI"
  }

  case object SeikoInstruments extends ManufacturerIdentificationCode {
    val code: Byte = 0xCD.toByte
    val name = "Seiko Instruments"
  }

  case object Samsung extends ManufacturerIdentificationCode {
    val code: Byte = 0xCE.toByte
    val name = "Samsung"
  }

  case object I3DesignSystem extends ManufacturerIdentificationCode {
    val code: Byte = 0x4F.toByte
    val name = "I3 Design System"
  }

  case object Klic extends ManufacturerIdentificationCode {
    val code: Byte = 0xD0.toByte
    val name = "Klic"
  }

  case object CrosspointSolutions extends ManufacturerIdentificationCode {
    val code: Byte = 0x51.toByte
    val name = "Crosspoint Solutions"
  }

  case object AllianceSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x52.toByte
    val name = "Alliance Semiconductor"
  }

  case object Tandem extends ManufacturerIdentificationCode {
    val code: Byte = 0xD3.toByte
    val name = "Tandem"
  }

  case object HewlettPackard extends ManufacturerIdentificationCode {
    val code: Byte = 0x54.toByte
    val name = "Hewlett-Packard"
  }

  case object IntegratedSiliconSolutions extends ManufacturerIdentificationCode {
    val code: Byte = 0xD5.toByte
    val name = "Integrated Silicon Solutions"
  }

  case object Brooktree extends ManufacturerIdentificationCode {
    val code: Byte = 0xD6.toByte
    val name = "Brooktree"
  }

  case object NewMedia extends ManufacturerIdentificationCode {
    val code: Byte = 0x57.toByte
    val name = "New Media"
  }

  case object MhsElectronic extends ManufacturerIdentificationCode {
    val code: Byte = 0x58.toByte
    val name = "MHS Electronic"
  }

  case object PerformanceSemi extends ManufacturerIdentificationCode {
    val code: Byte = 0xD9.toByte
    val name = "Performance Semi."
  }

  case object WinbondElectronic extends ManufacturerIdentificationCode {
    val code: Byte = 0xDA.toByte
    val name = "Winbond Electronic"
  }

  case object KawasakiSteel extends ManufacturerIdentificationCode {
    val code: Byte = 0x5B.toByte
    val name = "Kawasaki Steel"
  }

  case object BrightMicro extends ManufacturerIdentificationCode {
    val code: Byte = 0xDC.toByte
    val name = "Bright Micro"
  }

  case object Tecmar extends ManufacturerIdentificationCode {
    val code: Byte = 0x5D.toByte
    val name = "TECMAR"
  }

  case object Exar extends ManufacturerIdentificationCode {
    val code: Byte = 0x5E.toByte
    val name = "Exar"
  }

  case object Pcmcia extends ManufacturerIdentificationCode {
    val code: Byte = 0xDF.toByte
    val name = "PCMCIA"
  }

  case object LGSemiGoldstar extends ManufacturerIdentificationCode {
    val code: Byte = 0xE0.toByte
    val name = "LG Semi (Goldstar)"
  }

  case object NorthernTelecom extends ManufacturerIdentificationCode {
    val code: Byte = 0x61.toByte
    val name = "Northern Telecom"
  }

  case object Sanyo extends ManufacturerIdentificationCode {
    val code: Byte = 0x62.toByte
    val name = "Sanyo"
  }

  case object ArrayMicrosystems extends ManufacturerIdentificationCode {
    val code: Byte = 0xE3.toByte
    val name = "Array Microsystems"
  }

  case object CrystalSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x64.toByte
    val name = "Crystal Semiconductor"
  }

  case object AnalogDevices extends ManufacturerIdentificationCode {
    val code: Byte = 0xE5.toByte
    val name = "Analog Devices"
  }

  case object PmcSierra extends ManufacturerIdentificationCode {
    val code: Byte = 0xE6.toByte
    val name = "PMC-Sierra"
  }

  case object Asparix extends ManufacturerIdentificationCode {
    val code: Byte = 0x67.toByte
    val name = "Asparix"
  }

  case object ConvexComputer extends ManufacturerIdentificationCode {
    val code: Byte = 0x68.toByte
    val name = "Convex Computer"
  }

  case object QualitySemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0xE9.toByte
    val name = "Quality Semiconductor"
  }

  case object NimbusTechnology extends ManufacturerIdentificationCode {
    val code: Byte = 0xEA.toByte
    val name = "Nimbus Technology"
  }

  case object Transwitch extends ManufacturerIdentificationCode {
    val code: Byte = 0x6B.toByte
    val name = "Transwitch"
  }

  case object MicronasIttIntermetall extends ManufacturerIdentificationCode {
    val code: Byte = 0xEC.toByte
    val name = "Micronas (ITT Intermetall)"
  }

  case object Cannon extends ManufacturerIdentificationCode {
    val code: Byte = 0x6D.toByte
    val name = "Cannon"
  }

  case object Altera extends ManufacturerIdentificationCode {
    val code: Byte = 0x6E.toByte
    val name = "Altera"
  }

  case object Nexcom extends ManufacturerIdentificationCode {
    val code: Byte = 0xEF.toByte
    val name = "NEXCOM"
  }

  case object Qualcomm extends ManufacturerIdentificationCode {
    val code: Byte = 0x70.toByte
    val name = "Qualcomm"
  }

  case object Sony extends ManufacturerIdentificationCode {
    val code: Byte = 0xF1.toByte
    val name = "Sony"
  }

  case object CrayResearch extends ManufacturerIdentificationCode {
    val code: Byte = 0xF2.toByte
    val name = "Cray Research"
  }

  case object AmsAustriaMicro extends ManufacturerIdentificationCode {
    val code: Byte = 0x73.toByte
    val name = "AMS(Austria Micro)"
  }

  case object Vitesse extends ManufacturerIdentificationCode {
    val code: Byte = 0xF4.toByte
    val name = "Vitesse"
  }

  case object AsterElectronics extends ManufacturerIdentificationCode {
    val code: Byte = 0x75.toByte
    val name = "Aster Electronics"
  }

  case object BayNetworksSynoptic extends ManufacturerIdentificationCode {
    val code: Byte = 0x76.toByte
    val name = "Bay Networks (Synoptic)"
  }

  case object ZentrumZMD extends ManufacturerIdentificationCode {
    val code: Byte = 0xF7.toByte
    val name = "Zentrum/ZMD"
  }

  case object Trw extends ManufacturerIdentificationCode {
    val code: Byte = 0xF8.toByte
    val name = "TRW"
  }

  case object Thesys extends ManufacturerIdentificationCode {
    val code: Byte = 0x79.toByte
    val name = "Thesys"
  }

  case object SolbourneComputer extends ManufacturerIdentificationCode {
    val code: Byte = 0x7A.toByte
    val name = "Solbourne Computer"
  }

  case object AlliedSignal extends ManufacturerIdentificationCode {
    val code: Byte = 0xFB.toByte
    val name = "Allied-Signal"
  }

  case object DialogSemiconductor extends ManufacturerIdentificationCode {
    val code: Byte = 0x7C.toByte
    val name = "Dialog Semiconductor"
  }

  case object MediaVision extends ManufacturerIdentificationCode {
    val code: Byte = 0xFD.toByte
    val name = "Media Vision"
  }

  case object NumonyxCorporation extends ManufacturerIdentificationCode {
    val code: Byte = 0xFE.toByte
    val name = "Numonyx Corporation"
  }

}
