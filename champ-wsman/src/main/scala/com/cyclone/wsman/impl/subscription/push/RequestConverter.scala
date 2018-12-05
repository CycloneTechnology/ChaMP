package com.cyclone.wsman.impl.subscription.push

import java.nio.charset.Charset

import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.util.ByteString
import org.apache.james.mime4j.dom.field.ContentTypeField
import org.apache.james.mime4j.field.ContentTypeFieldImpl
import org.apache.james.mime4j.stream.RawField

import scala.Array.canBuildFrom

/**
  * Utilities to extract request parts from single and multi-part requests.
  *
  * Includes capabilities to fix up non-standard Microsoft multi-part mime received during push
  * event suscriptions.
  *
  * @author Jeremy.Stone
  */
object RequestConverter {
  type MultipartBody = List[DataPart]

  type Converter[-I, +O] = PartialFunction[RequestData[I], RequestData[O]]
}

trait SimpleRequestConverter[-I, +O] extends RequestConverter.Converter[I, O] {

  def apply(in: RequestData[I]): RequestData[O] = {
    val newBody = convertBody(in)

    RequestData(in.request, this :: in.appliedConversions, newBody)
  }

  def isDefinedAt(in: RequestData[I]): Boolean

  def convertBody(in: RequestData[I]): O
}

case class ParameterisedHeader(name: String, value: String) {

  val map: Map[String, String] = {
    if (value.contains(';'))
      value
        .split(';')
        .filter(_.contains('='))
        .map(_.split('=').map(_.trim))
        .map { p =>
          (p(0), p(1))
        }
        .toMap
    else
      Map.empty
  }

  def getParameter(paramName: String): Option[String] = map.get(paramName)
}

trait HeaderSource {
  def header(name: String): Option[ParameterisedHeader]
}

object RequestData {
  type UnparsedRequestData = RequestData[ByteString]
  type MultipartRequestData = RequestData[List[DataPart]]
}

case class RequestData[+T](
  request: HttpRequest,
  appliedConversions: List[RequestConverter.Converter[_, _]],
  body: T
) extends HeaderSource {

  def header(name: String): Option[ParameterisedHeader] =
    rawHeaderValue(name).map(v => ParameterisedHeader(name, v))

  private def rawHeaderValue(name: String): Option[String] = {
    val header = request.getHeader(name)

    if (header.isPresent) Some(header.get().value()) else None
  }

  lazy val multipartBoundary: Option[String] =
    contentType.map(_.getBoundary)

  lazy val contentType: Option[ContentTypeField] = {
    def contentTypeWithHeader(value: String) = {
      val parsed = ContentTypeFieldImpl.PARSER.parse(new RawField("Content-Type", value), null)

      if (parsed.getBoundary != null) Some(parsed) else None
    }

    def headerContentType =
      for {
        raw         <- rawHeaderValue("Content-Type")
        contentType <- contentTypeWithHeader(raw)
      } yield contentType

    def entityContentType =
      contentTypeWithHeader(request.entity.contentType.value)

    headerContentType.orElse(entityContentType)
  }

  def charset: Charset = {
    val charset = for (header  <- header("Content-Type");
                       charset <- header.getParameter("charset")) yield Charset.forName(charset)

    charset.getOrElse(Charset.defaultCharset())
  }

  def isMultipart: Boolean =
    request.method == HttpMethods.POST &&
    contentType.exists(_.getBody.startsWith("multipart"))
}

case class DataPart(headers: List[ParameterisedHeader], body: ByteString) extends HeaderSource {

  def header(name: String): Option[ParameterisedHeader] =
    headers.find(_.name == name)
}
