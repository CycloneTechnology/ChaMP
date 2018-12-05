package com.cyclone.util

import org.jmock.Mockery
import org.jmock.lib.concurrent.Synchroniser

/**
  * Mockery component with a synchronised mockery
  */
trait SynchronizedMockeryComponent extends MockeryComponent {
  lazy val mockery = new Mockery {
    setThreadingPolicy(new Synchroniser())
  }
}
