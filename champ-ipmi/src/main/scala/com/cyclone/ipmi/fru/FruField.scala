package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * An FRU field
  */
sealed trait FruField {
  def stringValue: String
}

case class BinaryField(data: ByteString) extends FruField {
  lazy val stringValue: String = data.toHexString()
}

case class StringField(stringValue: String) extends FruField

case object NullField extends FruField {
  val stringValue: String = "NULL"
}

