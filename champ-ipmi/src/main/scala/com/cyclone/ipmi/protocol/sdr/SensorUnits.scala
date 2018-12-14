package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait RateUnit {
  def abbreviation: String = "/" + unit.abbreviation

  def unit: SensorUnit
}

object RateUnit {
  implicit val decoder: Decoder[Option[RateUnit]] = new Decoder[Option[RateUnit]] {

    def decode(data: ByteString): Option[RateUnit] = data(0).bits3To5.toUnsignedInt match {
      case 0 => None
      case 1 => Some(PerMicrosecond)
      case 2 => Some(PerMillisecond)
      case 3 => Some(PerSecond)
      case 4 => Some(PerMinute)
      case 5 => Some(PerHour)
      case 6 => Some(PerDay)
    }
  }

  case object PerMicrosecond extends RateUnit {
    def unit: SensorUnit = SensorUnit.Microsecond
  }

  case object PerMillisecond extends RateUnit {
    def unit: SensorUnit = SensorUnit.Millisecond
  }

  case object PerSecond extends RateUnit {
    def unit: SensorUnit = SensorUnit.Second
  }

  case object PerMinute extends RateUnit {
    def unit: SensorUnit = SensorUnit.Minute
  }

  case object PerHour extends RateUnit {
    def unit: SensorUnit = SensorUnit.Hour
  }

  case object PerDay extends RateUnit {
    def unit: SensorUnit = SensorUnit.Day
  }

}

sealed trait UnitCombination {
  def infix: String
}

object UnitCombination {
  implicit val decoder: Decoder[Option[UnitCombination]] = new Decoder[Option[UnitCombination]] {

    def decode(data: ByteString): Option[UnitCombination] = data(0).bits1To2.toUnsignedInt match {
      case 0 => None
      case 1 => Some(Divide)
      case 2 => Some(Multiply)
    }
  }

  /**
    * Modification that caters for things like 'metres per second'
    */
  case object Divide extends UnitCombination {
    def infix: String = "/"
  }

  /**
    * Modification that caters for things like 'kilowatt hours'
    */
  case object Multiply extends UnitCombination {
    def infix: String = " "
  }

}

sealed trait SensorUnits {
  def abbreviation: String
}

object SensorUnits {

  implicit val decoder: Decoder[SensorUnits] = new Decoder[SensorUnits] {

    def decode(data: ByteString): SensorUnits = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val sensorsUnitByte = is.readByte

      val baseUnit = is.readByte.as[SensorUnit]
      val modifierUnit = is.readByte.as[SensorUnit]

      val rateUnit = sensorsUnitByte.as[Option[RateUnit]]

      sensorsUnitByte.as[Option[UnitCombination]] match {
        case None              => Simple(baseUnit, rateUnit)
        case Some(combination) => Combined(baseUnit, combination, modifierUnit, rateUnit)
      }
    }
  }

  case class Simple(unit: SensorUnit, optRateUnit: Option[RateUnit] = None) extends SensorUnits {
    def abbreviation: String = s"${unit.abbreviation}${optRateUnit.map(_.abbreviation).getOrElse("")}"
  }

  case class Combined(
    baseUnit: SensorUnit,
    combination: UnitCombination,
    modifierUnit: SensorUnit,
    optRateUnit: Option[RateUnit] = None
  ) extends SensorUnits {

    def abbreviation: String =
      s"${baseUnit.abbreviation}${combination.infix}${modifierUnit.abbreviation}${optRateUnit.map(_.abbreviation).getOrElse("")}"
  }

  case class Percentage(optRateUnit: Option[RateUnit]) extends SensorUnits {
    def abbreviation: String = "%"
  }

  case object NoUnits extends SensorUnits {
    def abbreviation: String = ""
  }

}
