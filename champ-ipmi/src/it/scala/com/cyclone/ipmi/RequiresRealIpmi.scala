package com.cyclone.ipmi

import org.scalatest.Tag

/**
  * The docker ipmi (vaporio/ipmi-simulator) does not support many commands - in particular any sensor commands it seems
  * so we need to exclude these by tagging then with this.
  */
object RequiresRealIpmi extends Tag("com.cyclone.ipmi.RequiresRealIpmi")
