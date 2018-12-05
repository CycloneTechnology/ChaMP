package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec._

import scala.collection.mutable.ListBuffer

/**
  * Holds a sequence of fields.
  *
  * Not thread safe.
  */
object FieldsIterator {

  def from(data: ByteString)(implicit languageCode: LanguageCode): FieldsIterator =
    new FieldsIterator(data)
}

class FieldsIterator private (data: ByteString)(implicit languageCode: LanguageCode) {
  private val iterator = data.iterator
  private val is = iterator.asInputStream

  /**
    * Reads the next field.
    *
    * @param treatAsEnglish whether to ignore the language code and treat as 'English'
    *                       (by which the spec means 8-bit ASCII)
    */
  def nextOpt(treatAsEnglish: Boolean = false): Option[FruField] = readField(treatAsEnglish)

  /**
    * Reads the next field on the assumption that there will be another field.
    *
    * Throws [[IllegalStateException]] if no more fields
    *
    * @param treatAsEnglish whether to ignore the language code and treat as 'English'
    *                       (by which the spec means 8-bit ASCII + Latin)
    */
  def next(treatAsEnglish: Boolean = false): FruField = nextOpt(treatAsEnglish).get

  /**
    * Reads the remaining fields.
    */
  def toSeq: Seq[FruField] = {
    val list = ListBuffer.empty[FruField]
    var done = false

    while (!done) {
      val optField = readField()

      optField match {
        case Some(field) => list += field
        case None        => done = true
      }
    }

    list.toList
  }

  private def readField(treatAsEnglish: Boolean = false): Option[FruField] =
    for {
      prefix <- is.readByteOptional.as[FruFieldPrefix]
      field <- prefix match {
        case FruFieldPrefix.FixedLengthField(tpe, len) =>
          Some(tpe.decode(is.read(len), treatAsEnglish))
        case FruFieldPrefix.EmptyField   => Some(NullField)
        case FruFieldPrefix.NoMoreFields => None
      }
    } yield field
}
