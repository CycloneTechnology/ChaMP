package com.cyclone.ipmi.sdr

import java.lang.Math._

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait Linearization

object Linearization {
  implicit val decoder: Decoder[Linearization] = new Decoder[Linearization] {
    def decode(data: ByteString): Linearization = data(0).toUnsignedInt match {
      case 0  => Linearization.Linear
      case 1  => Linearization.Log_e
      case 2  => Linearization.Log_10
      case 3  => Linearization.Log_2
      case 4  => Linearization.Exp_e
      case 5  => Linearization.Exp_10
      case 6  => Linearization.Exp_2
      case 7  => Linearization.Reciprocal
      case 8  => Linearization.Square
      case 9  => Linearization.Cube
      case 10 => Linearization.SquareRoot
      case 11 => Linearization.CubeRoot
      case _  => NonLinearizable
    }
  }

  case object NonLinearizable extends Linearization

  sealed trait Linearizable extends Linearization {
    def evaluate(x: Double): Double
  }

  private val log2 = log(2)
  private val `1/3` = 1.toDouble / 3


  case object Linear extends Linearizable {
    def evaluate(x: Double): Double = x
  }

  case object Exp_e extends Linearizable {
    def evaluate(x: Double): Double = exp(x)
  }

  case object Exp_10 extends Linearizable {
    def evaluate(x: Double): Double = pow(x, 10)
  }

  case object Exp_2 extends Linearizable {
    def evaluate(x: Double): Double = pow(x, 2)
  }

  case object Log_e extends Linearizable {
    def evaluate(x: Double): Double = log(x)
  }

  case object Log_10 extends Linearizable {
    def evaluate(x: Double): Double = log10(x)
  }

  case object Log_2 extends Linearizable {
    def evaluate(x: Double): Double = log(x) / log2
  }

  case object Reciprocal extends Linearizable {
    def evaluate(x: Double): Double = 1 / x
  }

  case object Square extends Linearizable {
    def evaluate(x: Double): Double = x * x
  }

  case object Cube extends Linearizable {
    def evaluate(x: Double): Double = x * x * x
  }

  case object SquareRoot extends Linearizable {
    def evaluate(x: Double): Double = sqrt(x)
  }

  case object CubeRoot extends Linearizable {
    def evaluate(x: Double): Double = pow(x, `1/3`)
  }

}



