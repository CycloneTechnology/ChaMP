package com.cyclone.wsman.impl.subscription.push

import java.io.{InputStream, StringWriter}

import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.Multipart.FormData.BodyPart
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.util.ByteString
import com.cyclone.akka.{ActorMaterializerComponent, ActorSystemShutdown, TestKitSupport}
import com.cyclone.wsman.impl.subscription.push.RequestConverterComponent._
import org.jmock.Mockery
import org.junit.Test
import org.scalacheck.Arbitrary._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitSuiteLike
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.xml.XML

class RequestConverterTest
    extends TestKitSupport
    with JUnitSuiteLike
    with ScalaFutures
    with GeneratorDrivenPropertyChecks
    with ActorMaterializerComponent
    with ActorSystemShutdown {

  val mockery = new Mockery

  val xml = <a attr="value">some text</a>

  val xmlString = {
    val sw = new StringWriter
    XML.write(sw, xml, "UTF-8", xmlDecl = false, null)

    sw.toString
  }

  val xmlBytes = xmlString.getBytes("UTF-8")

  @Test
  def singleRequestData_toXML(): Unit = {
    val request =
      HttpRequest(method = HttpMethods.POST, entity = HttpEntity(ContentType(`application/octet-stream`), xmlBytes))

    val RequestData(_, _, body) = toXmlConverterPlain(requestData(request))

    assert(body === xml)
  }

  @Test
  def multiPartData_singlePart_toXML(): Unit = {
    val request: HttpRequest = multipartRequest(xmlBytes)

    val RequestData(_, _, body) = toXmlConverterPlain(requestData(request))

    assert(body === xml)
  }

  @Test
  def multiPartData_getsLastPart_toXML(): Unit = {

    val data = Multipart
      .FormData(
        BodyPart.Strict("0", HttpEntity(ContentType(`application/octet-stream`), ByteString(1, 2, 3, 4, 5))),
        BodyPart.Strict("1", HttpEntity(ContentType(`application/octet-stream`), xmlBytes))
      )
      .toEntity("Boundary")

    val request =
      HttpRequest(method = HttpMethods.POST, headers = List(`Content-Type`(`multipart/form-data`)), entity = data)

    val RequestData(_, _, body) = toXmlConverterPlain(requestData(request))

    assert(body === xml)
  }

  @Test
  def multiplePartsRequiringFixup_toXML(): Unit = {
    val request = HttpRequest(
      method = HttpMethods.POST,
      headers = List(RawHeader("Content-Type", "multipart/form-data; boundary=\"Boundary\"")),
      entity = HttpEntity(readClassPathFile("multipartReq.dat"))
    )

    val RequestData(_, _, body) = toXmlConverterPlain(requestData(request))

    assert(body === xml)
  }

  @Test
  def checkToMime(): Unit =
    forAll { array: Array[Byte] =>
      whenever(array.nonEmpty) {
        val RequestData(_, _, List(DataPart(_, partData))) =
          convertMimeWithBody(array)

        assert(partData.toArray === array)
      }
    }

  @Test
  def checkToMime_bigArray(): Unit = {
    val array = Array.fill[Byte](8000)(123)

    val RequestData(_, _, List(DataPart(_, partData))) =
      convertMimeWithBody(array)

    assert(partData.toArray === array)
  }

  @Test
  def storesAppliedConversions_singleRequestData(): Unit = {
    val request =
      HttpRequest(method = HttpMethods.POST, entity = HttpEntity(ContentType(`application/octet-stream`), xmlBytes))

    val RequestData(_, convs, _) = toXmlConverterPlain(requestData(request))

    assert(convs === List(ToXMLConverter, SinglePartRequestConverter, IdentityConverter()))
  }

  @Test
  def storesAppliedConversions_multipartsRequiringFixup(): Unit = {
    val request = HttpRequest(
      method = HttpMethods.POST,
      headers = List(RawHeader("Content-Type", "multipart/form-data; boundary=\"Boundary\"")),
      entity = HttpEntity(readClassPathFile("multipartReq.dat"))
    )

    val RequestData(_, convs, _) = toXmlConverterPlain(requestData(request))

    assert(convs === List(ToXMLConverter, LastPartRequestConverter, MimeRequestConverter, MimeFixupRequestConverter))
  }

  private def convertMimeWithBody(array: Array[Byte]) = {
    val request: HttpRequest = multipartRequest(array)

    MimeRequestConverter(requestData(request))
  }

  private def multipartRequest(array: Array[Byte]) = {
    val data = Multipart
      .FormData(
        BodyPart.Strict("1", HttpEntity(ContentType(`application/octet-stream`), array))
      )
      .toEntity("Boundary")

    val request =
      HttpRequest(method = HttpMethods.POST, headers = List(`Content-Type`(`multipart/form-data`)), entity = data)
    request
  }

  private def readClassPathFile(name: String) = read(getClass.getClassLoader.getResourceAsStream(name))

  private def read(is: InputStream) = {
    assert(is != null, "Null input stream")
    Stream.continually(is.read).takeWhile(-1 != _).map(_.toByte).toArray
  }

  private def requestData(request: HttpRequest): RequestData[ByteString] =
    request.entity match {
      case HttpEntity.Strict(_, bs) => RequestData(request, Nil, bs)
      case _                        => throw new IllegalArgumentException
    }

}
