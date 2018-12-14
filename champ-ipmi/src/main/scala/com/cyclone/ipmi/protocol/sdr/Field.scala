package com.cyclone.ipmi.protocol.sdr

/**
  * A fixed length field.
  *
  * See section 43.15. Similar to FruField
  */
sealed trait Field {
  def stringValue: String
}

case class BcdField(stringValue: String) extends Field

case class StringField(stringValue: String) extends Field

case object NullField extends Field {
  val stringValue: String = "NULL"
}
