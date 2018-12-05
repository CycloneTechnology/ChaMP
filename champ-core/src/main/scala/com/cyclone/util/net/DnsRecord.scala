package com.cyclone.util.net

import com.google.common.base.{CharMatcher, Splitter}

sealed trait DnsRecord

object DnsRecord {

  private val WhitespaceSplitter = Splitter
    .on(CharMatcher.breakingWhitespace()).trimResults().omitEmptyStrings()

  private def trimTrailingDot(value: String): String =
    if (value.endsWith("."))
      value.substring(0, value.length() - 1)
    else
      value

  case class PTR(hostName: String) extends DnsRecord

  object PTR {
    def fromRaw(raw: String): PTR =
      PTR(trimTrailingDot(raw))
  }

  case class MX(server: String, mxLevel: Int) extends DnsRecord

  object MX {
    def fromRaw(raw: String): MX = {
      val parts = WhitespaceSplitter.splitToList(raw)

      MX(trimTrailingDot(parts.get(1)), parts.get(0).toInt)
    }
  }

}