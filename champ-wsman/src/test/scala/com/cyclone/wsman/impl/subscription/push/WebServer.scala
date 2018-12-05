package com.cyclone.wsman.impl.subscription.push

import java.net.InetAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.cyclone.akka.{ActorSystemComponent, MaterializerComponent}
import com.cyclone.util.net.HostAndPort
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait TestWebServer {
  def hostAndPort: HostAndPort

  def start(): Unit
}

trait TestWebServerComponent {
  def testWebServer: TestWebServer
}

trait AkkaHttpTestWebServerComponent extends TestWebServerComponent {
  self: ActorSystemComponent with MaterializerComponent =>

  def route: Route

  private val port = 80

  lazy val testWebServer = new TestWebServer {

    def start(): Unit =
      Http().bindAndHandle(route, "0.0.0.0", hostAndPort.getPortOrDefault(port)).onComplete {
        case Success(ok) => println(s"Test server bound to port $port")
        case Failure(e)  => e.printStackTrace()
      }

    def hostAndPort = HostAndPort.fromParts(InetAddress.getLocalHost.getCanonicalHostName, port)
  }
}
