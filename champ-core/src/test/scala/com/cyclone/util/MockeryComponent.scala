package com.cyclone.util

import org.jmock.Mockery

// TODO remove in v4 and use standard one
/**
  * Component that exposes a mockery
  */
trait MockeryComponent {
  def mockery: Mockery
}
