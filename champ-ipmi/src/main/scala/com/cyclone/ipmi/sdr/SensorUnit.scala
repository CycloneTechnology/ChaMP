package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait SensorUnit {
  def code: Byte

  def message: String

  def abbreviation: String
}

object SensorUnit {

  implicit val decoder: Decoder[SensorUnit] = new Decoder[SensorUnit] {
    def decode(data: ByteString): SensorUnit = fromCode(data(0))
  }

  case object Unspecified extends SensorUnit {
    val code: Byte = 0.toByte
    val message = "unspecified"
    val abbreviation = "unspecified"
  }

  case object DegreesCentigrade extends SensorUnit {
    val code: Byte = 1.toByte
    val message = "degrees C"
    val abbreviation = "C"
  }

  case object DegreesFahrenheight extends SensorUnit {
    val code: Byte = 2.toByte
    val message = "degrees F"
    val abbreviation = "F"
  }

  case object DegreesKelvin extends SensorUnit {
    val code: Byte = 3.toByte
    val message = "degrees K"
    val abbreviation = "K"
  }

  case object Volts extends SensorUnit {
    val code: Byte = 4.toByte
    val message = "Volts"
    val abbreviation = "V"
  }

  case object Amps extends SensorUnit {
    val code: Byte = 5.toByte
    val message = "Amps"
    val abbreviation = "A"
  }

  case object Watts extends SensorUnit {
    val code: Byte = 6.toByte
    val message = "Watts"
    val abbreviation = "W"
  }

  case object Joules extends SensorUnit {
    val code: Byte = 7.toByte
    val message = "Joules"
    val abbreviation = "J"
  }

  case object Coulombs extends SensorUnit {
    val code: Byte = 8.toByte
    val message = "Coulombs"
    val abbreviation = "C"
  }

  case object VA extends SensorUnit {
    val code: Byte = 9.toByte
    val message = "VA"
    val abbreviation = "VA"
  }

  case object Nits extends SensorUnit {
    val code: Byte = 10.toByte
    val message = "Nits"
    val abbreviation = "nits"
  }

  case object Lumen extends SensorUnit {
    val code: Byte = 11.toByte
    val message =
      "lumen"
    val abbreviation = "lm"
  }

  case object Lux extends SensorUnit {
    val code: Byte = 12.toByte
    val message =
      "lux"
    val abbreviation = "lux"
  }

  case object Candela extends SensorUnit {
    val code: Byte = 13.toByte
    val message = "Candela"
    val abbreviation = "cd"
  }

  case object kPa extends SensorUnit {
    val code: Byte = 14.toByte
    val message = "kPa"
    val abbreviation = "KPA"
  }

  case object PSI extends SensorUnit {
    val code: Byte = 15.toByte
    val message = "PSI"
    val abbreviation = "PSI"
  }

  case object Newton extends SensorUnit {
    val code: Byte = 16.toByte
    val message = "Newton"
    val abbreviation = "N"
  }

  case object CFM extends SensorUnit {
    val code: Byte = 17.toByte
    val message =
      "CFM"
    val abbreviation = "CFM"
  }

  case object RPM extends SensorUnit {
    val code: Byte = 18.toByte
    val message = "RPM"
    val abbreviation = "RPM"
  }

  case object Hertz extends SensorUnit {
    val code: Byte = 19.toByte
    val message = "Hz"
    val abbreviation = "Hz"
  }

  case object Microsecond extends SensorUnit {
    val code: Byte = 20.toByte
    val message = "microsecond"
    val abbreviation = "us"
  }

  case object Millisecond extends SensorUnit {
    val code: Byte = 21.toByte
    val message = "millisecond"
    val abbreviation = "ms"
  }

  case object Second extends SensorUnit {
    val code: Byte = 22.toByte
    val message = "second"
    val abbreviation = "s"
  }

  case object Minute extends SensorUnit {
    val code: Byte = 23.toByte
    val message = "minute"
    val abbreviation = "min"
  }

  case object Hour extends SensorUnit {
    val code: Byte = 24.toByte
    val message = "hour"
    val abbreviation = "hr"
  }

  case object Day extends SensorUnit {
    val code: Byte = 25.toByte
    val message = "day"
    val abbreviation = "day"
  }

  case object Week extends SensorUnit {
    val code: Byte = 26.toByte
    val message = "week"
    val abbreviation = "wk"
  }

  case object Mil extends SensorUnit {
    val code: Byte = 27.toByte
    val message = "mil"
    val abbreviation = "mil"
  }

  case object Inches extends SensorUnit {
    val code: Byte = 28.toByte
    val message = "inches"
    val abbreviation = "in"
  }

  case object Feet extends SensorUnit {
    val code: Byte = 29.toByte
    val message = "feet"
    val abbreviation = "ft"
  }

  case object CubicInches extends SensorUnit {
    val code: Byte = 30.toByte
    val message = "cu in"
    val abbreviation = "cu in"
  }

  case object CubicFeet extends SensorUnit {
    val code: Byte = 31.toByte
    val message = "cu feet"
    val abbreviation = "cu feet"
  }

  case object Millimetre extends SensorUnit {
    val code: Byte = 32.toByte
    val message = "mm"
    val abbreviation = "mm"
  }

  case object Centimetre extends SensorUnit {
    val code: Byte = 33.toByte
    val message = "cm"
    val abbreviation = "cm"
  }

  case object Metre extends SensorUnit {
    val code: Byte = 34.toByte
    val message = "m"
    val abbreviation = "m"
  }

  case object CubicCentimetre extends SensorUnit {
    val code: Byte = 35.toByte
    val message = "cu cm"
    val abbreviation = "cu cm"
  }

  case object CubicMetre extends SensorUnit {
    val code: Byte = 36.toByte
    val message = "cu m"
    val abbreviation = "cu m"
  }

  case object Liters extends SensorUnit {
    val code: Byte = 37.toByte
    val message = "liters"
    val abbreviation = "L"
  }

  case object FluidOunce extends SensorUnit {
    val code: Byte = 38.toByte
    val message = "fluid ounce"
    val abbreviation = "fl oz"
  }

  case object Radians extends SensorUnit {
    val code: Byte = 39.toByte
    val message = "radians"
    val abbreviation = "rad"
  }

  case object Steradians extends SensorUnit {
    val code: Byte = 40.toByte
    val message = "steradians"
    val abbreviation = "sr"
  }

  case object Revolutions extends SensorUnit {
    val code: Byte = 41.toByte
    val message = "revolutions"
    val abbreviation = "rev"
  }

  case object Cycles extends SensorUnit {
    val code: Byte = 42.toByte
    val message = "cycles"
    val abbreviation = "c"
  }

  case object Gravities extends SensorUnit {
    val code: Byte = 43.toByte
    val message = "gravities"
    val abbreviation = "g"
  }

  case object Ounce extends SensorUnit {
    val code: Byte = 44.toByte
    val message = "ounce"
    val abbreviation = "oz"
  }

  case object Pound extends SensorUnit {
    val code: Byte = 45.toByte
    val message = "pound"
    val abbreviation = "lb"
  }

  case object FootPound extends SensorUnit {
    val code: Byte = 46.toByte
    val message = "ft-lb"
    val abbreviation = "ft lb"
  }

  case object OunceInch extends SensorUnit {
    val code: Byte = 47.toByte
    val message = "oz-in"
    val abbreviation = "oz in"
  }

  case object Gauss extends SensorUnit {
    val code: Byte = 48.toByte
    val message = "gauss"
    val abbreviation = "G"
  }

  case object Gilberts extends SensorUnit {
    val code: Byte = 49.toByte
    val message = "gilberts"
    val abbreviation = "Gi"
  }

  case object Henry extends SensorUnit {
    val code: Byte = 50.toByte
    val message = "henry"
    val abbreviation = "H"
  }

  case object MilliHenry extends SensorUnit {
    val code: Byte = 51.toByte
    val message = "millihenry"
    val abbreviation = "mH"
  }

  case object Farad extends SensorUnit {
    val code: Byte = 52.toByte
    val message = "farad"
    val abbreviation = "F"
  }

  case object MicroFarad extends SensorUnit {
    val code: Byte = 53.toByte
    val message = "microfarad"
    val abbreviation = "uF"
  }

  case object Ohms extends SensorUnit {
    val code: Byte = 54.toByte
    val message = "ohms"
    val abbreviation = "ohms"
  }

  case object Siemens extends SensorUnit {
    val code: Byte = 55.toByte
    val message = "siemens"
    val abbreviation = "S"
  }

  case object Mole extends SensorUnit {
    val code: Byte = 56.toByte
    val message = "mole"
    val abbreviation = "mol"
  }

  case object Becquerel extends SensorUnit {
    val code: Byte = 57.toByte
    val message = "becquerel"
    val abbreviation = "Bq"
  }

  case object PartsPerMillion extends SensorUnit {
    val code: Byte = 58.toByte
    val message = "PPM (parts/million)"
    val abbreviation = "PPM"
  }

  case object Reserved extends SensorUnit {
    val code: Byte = 59.toByte
    val message = "reserved"
    val abbreviation = "Reserved"
  }

  case object Decibels extends SensorUnit {
    val code: Byte = 60.toByte
    val message = "Decibels"
    val abbreviation = "dB"
  }

  case object DbA extends SensorUnit {
    val code: Byte = 61.toByte
    val message = "DbA"
    val abbreviation = "DbA"
  }

  case object DbC extends SensorUnit {
    val code: Byte = 62.toByte
    val message = "DbC"
    val abbreviation = "DbC"
  }

  case object Gray extends SensorUnit {
    val code: Byte = 63.toByte
    val message = "gray"
    val abbreviation = "gy"
  }

  case object Sievert extends SensorUnit {
    val code: Byte = 64.toByte
    val message = "sievert"
    val abbreviation = "Sv"
  }

  case object ColorTempDegK extends SensorUnit {
    val code: Byte = 65.toByte
    val message = "color temp deg K"
    val abbreviation = "color temp deg K"
  }

  case object Bit extends SensorUnit {
    val code: Byte = 66.toByte
    val message = "bit"
    val abbreviation = "b"
  }

  case object KiloBit extends SensorUnit {
    val code: Byte = 67.toByte
    val message = "kilobit"
    val abbreviation = "Kb"
  }

  case object MegaBit extends SensorUnit {
    val code: Byte = 68.toByte
    val message = "megabit"
    val abbreviation = "Mb"
  }

  case object GigaBit extends SensorUnit {
    val code: Byte = 69.toByte
    val message = "gigabit"
    val abbreviation = "Gb"
  }

  case object Byte extends SensorUnit {
    val code: Byte = 70.toByte
    val message = "byte"
    val abbreviation = "B"
  }

  case object KiloByte extends SensorUnit {
    val code: Byte = 71.toByte
    val message = "kilobyte"
    val abbreviation = "KB"
  }

  case object MegaByte extends SensorUnit {
    val code: Byte = 72.toByte
    val message = "megabyte"
    val abbreviation = "MB"
  }

  case object GigaByte extends SensorUnit {
    val code: Byte = 73.toByte
    val message = "gigabyte"
    val abbreviation = "GB"
  }

  case object WordData extends SensorUnit {
    val code: Byte = 74.toByte
    val message = "word (data)"
    val abbreviation = "word"
  }

  case object DWord extends SensorUnit {
    val code: Byte = 75.toByte
    val message = "dword"
    val abbreviation = "dword"
  }

  case object QWord extends SensorUnit {
    val code: Byte = 76.toByte
    val message = "qword"
    val abbreviation = "qword"
  }

  case object LineReMemLine extends SensorUnit {
    val code: Byte = 77.toByte
    val message = "line (re. mem. line)"
    val abbreviation = "line"
  }

  case object Hit extends SensorUnit {
    val code: Byte = 78.toByte
    val message = "hit"
    val abbreviation = "hit"
  }

  case object Miss extends SensorUnit {
    val code: Byte = 79.toByte
    val message = "miss"
    val abbreviation = "miss"
  }

  case object Retry extends SensorUnit {
    val code: Byte = 80.toByte
    val message = "retry"
    val abbreviation = "retry"
  }

  case object Reset extends SensorUnit {
    val code: Byte = 81.toByte
    val message = "reset"
    val abbreviation = "reset"
  }

  case object OverrunOverflow extends SensorUnit {
    val code: Byte = 82.toByte
    val message = "overrun / overflow"
    val abbreviation = "overrun / overflow"
  }

  case object Underrun extends SensorUnit {
    val code: Byte = 83.toByte
    val message = "underrun"
    val abbreviation = "underrun"
  }

  case object Collision extends SensorUnit {
    val code: Byte = 84.toByte
    val message = "collision"
    val abbreviation = "collision"
  }

  case object Packets extends SensorUnit {
    val code: Byte = 85.toByte
    val message = "packets"
    val abbreviation = "pkts"
  }

  case object Messages extends SensorUnit {
    val code: Byte = 86.toByte
    val message = "messages"
    val abbreviation = "msgs"
  }

  case object Characters extends SensorUnit {
    val code: Byte = 87.toByte
    val message = "characters"
    val abbreviation = "chars"
  }

  case object Error extends SensorUnit {
    val code: Byte = 88.toByte
    val message = "error"
    val abbreviation = "err"
  }

  case object CorrectableError extends SensorUnit {
    val code: Byte = 89.toByte
    val message = "correctable error"
    val abbreviation = "correctaqble err"
  }

  case object UncorrectableError extends SensorUnit {
    val code: Byte = 90.toByte
    val message = "uncorrectable error"
    val abbreviation = "uncorrectable err"
  }

  case object FatalError extends SensorUnit {
    val code: Byte = 91.toByte
    val message = "fatal error"
    val abbreviation = "fatal err"
  }

  case object Grams extends SensorUnit {
    val code: Byte = 92.toByte
    val message = "grams"
    val abbreviation = "g"
  }

  def fromCode(code: Byte): SensorUnit = code match {
    case Unspecified.code         => Unspecified
    case DegreesCentigrade.code   => DegreesCentigrade
    case DegreesFahrenheight.code => DegreesFahrenheight
    case DegreesKelvin.code       => DegreesKelvin
    case Volts.code               => Volts
    case Amps.code                => Amps
    case Watts.code               => Watts
    case Joules.code              => Joules
    case Coulombs.code            => Coulombs
    case VA.code                  => VA
    case Nits.code                => Nits
    case Lumen.code               => Lumen
    case Lux.code                 => Lux
    case Candela.code             => Candela
    case kPa.code                 => kPa
    case PSI.code                 => PSI
    case Newton.code              => Newton
    case CFM.code                 => CFM
    case RPM.code                 => RPM
    case Hertz.code               => Hertz
    case Microsecond.code         => Microsecond
    case Millisecond.code         => Millisecond
    case Second.code              => Second
    case Minute.code              => Minute
    case Hour.code                => Hour
    case Day.code                 => Day
    case Week.code                => Week
    case Mil.code                 => Mil
    case Inches.code              => Inches
    case Feet.code                => Feet
    case CubicInches.code         => CubicInches
    case CubicFeet.code           => CubicFeet
    case Millimetre.code          => Millimetre
    case Centimetre.code          => Centimetre
    case Metre.code               => Metre
    case CubicCentimetre.code     => CubicCentimetre
    case CubicMetre.code          => CubicMetre
    case Liters.code              => Liters
    case FluidOunce.code          => FluidOunce
    case Radians.code             => Radians
    case Steradians.code          => Steradians
    case Revolutions.code         => Revolutions
    case Cycles.code              => Cycles
    case Gravities.code           => Gravities
    case Ounce.code               => Ounce
    case Pound.code               => Pound
    case FootPound.code           => FootPound
    case OunceInch.code           => OunceInch
    case Gauss.code               => Gauss
    case Gilberts.code            => Gilberts
    case Henry.code               => Henry
    case MilliHenry.code          => MilliHenry
    case Farad.code               => Farad
    case MicroFarad.code          => MicroFarad
    case Ohms.code                => Ohms
    case Siemens.code             => Siemens
    case Mole.code                => Mole
    case Becquerel.code           => Becquerel
    case PartsPerMillion.code     => PartsPerMillion
    case Reserved.code            => Reserved
    case Decibels.code            => Decibels
    case DbA.code                 => DbA
    case DbC.code                 => DbC
    case Gray.code                => Gray
    case Sievert.code             => Sievert
    case ColorTempDegK.code       => ColorTempDegK
    case Bit.code                 => Bit
    case KiloBit.code             => KiloBit
    case MegaBit.code             => MegaBit
    case GigaBit.code             => GigaBit
    case Byte.code                => Byte
    case KiloByte.code            => KiloByte
    case MegaByte.code            => MegaByte
    case GigaByte.code            => GigaByte
    case WordData.code            => WordData
    case DWord.code               => DWord
    case QWord.code               => QWord
    case LineReMemLine.code       => LineReMemLine
    case Hit.code                 => Hit
    case Miss.code                => Miss
    case Retry.code               => Retry
    case Reset.code               => Reset
    case OverrunOverflow.code     => OverrunOverflow
    case Underrun.code            => Underrun
    case Collision.code           => Collision
    case Packets.code             => Packets
    case Messages.code            => Messages
    case Characters.code          => Characters
    case Error.code               => Error
    case CorrectableError.code    => CorrectableError
    case UncorrectableError.code  => UncorrectableError
    case FatalError.code          => FatalError
    case Grams.code               => Grams
  }
}
