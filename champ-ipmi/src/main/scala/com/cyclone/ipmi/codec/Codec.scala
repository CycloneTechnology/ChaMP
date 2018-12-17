package com.cyclone.ipmi.codec

import akka.util.ByteString
import com.cyclone.ipmi.{IpmiError, IpmiExceptionError}
import com.typesafe.scalalogging.LazyLogging
import scalaz.Scalaz._
import scalaz.{Failure => _, Success => _, _}

import scala.util.{Failure, Success, Try}

/**
  * Knows how to encode a type to bytes
  *
  * @tparam A the type
  */
trait Coder[A] {
  def encode(a: A): ByteString
}

/** Convenience holder for an object and its [[Coder]] */
trait Codable {
  type A
  def a: A
  def encode: ByteString
}

object Codable {
  type Aux[A0] = Codable { type A = A0 }

  def apply[A](a: A)(implicit coder: Coder[A]): Codable.Aux[A] = CoderAnd(a)

  case class CoderAnd[A0](a: A0)(implicit coder: Coder[A0]) extends Codable {
    type A = A0
    def encode: ByteString = coder.encode(a)
  }
}

/**
  * Knows how to decode bytes to types
  *
  * @tparam A the type
  */
trait Decoder[+A] {
  def decode(data: ByteString): A

  def handleExceptions: ErrorDecoder[A] = ErrorDecoder.wrapToHandleExceptions(this)
}

/**
  * Decoder that can evaluate to an error
  *
  * @tparam E the error type
  * @tparam A the success type
  */
trait OrDecoder[E, +A] {
  def decode(data: ByteString): E \/ A
}

trait Codec[A] extends Coder[A] with Decoder[A]

trait ErrorDecoder[+A] extends OrDecoder[IpmiError, A]

object ErrorDecoder extends LazyLogging {

  def wrapToHandleExceptions[A](decoder: Decoder[A]): ErrorDecoder[A] = new ErrorDecoder[A] {

    def decode(data: ByteString): IpmiExceptionError \/ A = {
      Try(decoder.decode(data)) match {
        case Success(a) => a.right
        case Failure(e) =>
          logger.error(s"Failed to decode $data", e)
          IpmiExceptionError(e).left
      }
    }
  }
}
