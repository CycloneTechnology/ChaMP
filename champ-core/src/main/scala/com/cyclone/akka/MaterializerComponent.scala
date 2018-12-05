package com.cyclone.akka

import akka.stream.Materializer

trait MaterializerComponent {
  implicit def materializer: Materializer
}
