package com.cyclone.ipmi.command.oem

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait IanaEnterpriseNumber {
  def number: Int
}

object IanaEnterpriseNumber {
  implicit val decoder: Decoder[IanaEnterpriseNumber] = new Decoder[IanaEnterpriseNumber] {

    def decode(data: ByteString): IanaEnterpriseNumber = (data :+ 0x0.toByte).as[Int] match {
      case Ibm.number                  => Ibm
      case Hp.number                   => Hp
      case SunMicrosystems.number      => SunMicrosystems
      case Intel.number                => Intel
      case Dell.number                 => Dell
      case MagnumTechnologies.number   => MagnumTechnologies
      case Quanta.number               => Quanta
      case Fujitsu.number              => Fujitsu
      case Peppercon.number            => Peppercon
      case Supermicro.number           => Supermicro
      case Wistron.number              => Wistron
      case Inventec.number             => Inventec
      case SupermicroWorkaround.number => SupermicroWorkaround
      case number: Int                 => Other(number)
    }
  }

  implicit val encoder: Coder[IanaEnterpriseNumber] = new Coder[IanaEnterpriseNumber] {

    def encode(a: IanaEnterpriseNumber): ByteString =
      a.number.toBin.take(3)
  }

  case object Ibm extends IanaEnterpriseNumber {
    val number = 2
  }

  case object Hp extends IanaEnterpriseNumber {
    val number = 11
  }

  case object SunMicrosystems extends IanaEnterpriseNumber {
    val number = 42
  }

  case object Intel extends IanaEnterpriseNumber {
    val number = 343
  }

  case object Dell extends IanaEnterpriseNumber {
    val number = 674
  }

  case object MagnumTechnologies extends IanaEnterpriseNumber {
    val number = 5593
  }

  case object Quanta extends IanaEnterpriseNumber {
    val number = 7244
  }

  case object Fujitsu extends IanaEnterpriseNumber {
    val number = 10368
  }

  case object Peppercon extends IanaEnterpriseNumber {
    val number = 10437
  }

  case object Supermicro extends IanaEnterpriseNumber {
    val number = 10876
  }

  case object Wistron extends IanaEnterpriseNumber {
    val number = 11161
  }

  case object Inventec extends IanaEnterpriseNumber {
    val number = 20569
  }

  /* Workarounds for motherboards with invalid enterprise IDs */
  case object SupermicroWorkaround extends IanaEnterpriseNumber {
    val number = 47488
  }

  case class Other(number: Int) extends IanaEnterpriseNumber

}
