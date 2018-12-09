package com.cyclone.util

import org.jmock.Mockery
import org.jmock.lib.concurrent.Synchroniser
import org.jmock.lib.legacy.ClassImposteriser

/**
  * Mockery component with a synchronised mockery that uses a [[ClassImposteriser]]
  */
trait SynchronizedClassBasedMockeryComponent extends MockeryComponent {
  lazy val mockery = new Mockery {
    setImposteriser(ClassImposteriser.INSTANCE)
    setThreadingPolicy(new Synchroniser())
  }
}
