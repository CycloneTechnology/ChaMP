package com.cyclone.wsman.impl.subscription.push

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.charset.Charset

import akka.http.scaladsl.model.HttpRequest
import akka.util.ByteString
import com.cyclone.util.XmlUtils
import com.cyclone.util.spnego.Token
import com.cyclone.wsman.impl.subscription.push.RequestConverter.MultipartBody
import com.cyclone.wsman.impl.subscription.push.RequestData._
import com.typesafe.scalalogging.LazyLogging
import org.apache.james.mime4j.field.ContentTypeFieldImpl
import org.apache.james.mime4j.parser.{AbstractContentHandler, MimeStreamParser}
import org.apache.james.mime4j.stream.{BodyDescriptor, Field, MimeConfig, RawField}

import scala.collection.{Seq, mutable}
import scala.xml.{Elem, XML}

object MimeRequestConverter extends SimpleRequestConverter[ByteString, MultipartBody]
  with LazyLogging {

  import RequestData._

  def isDefinedAt(requestData: UnparsedRequestData): Boolean = requestData.isMultipart

  def convertBody(requestData: UnparsedRequestData): MultipartBody = {
    requestData.contentType match {
      case Some(contentType) =>
        val config = new MimeConfig.Builder()
          .setHeadlessParsing(contentType.getBody)
          // Otherwise we don't get to know about problems
          .setStrictParsing(true).build()

        val parser = new MimeStreamParser(config)

        val parts = mutable.ListBuffer[DataPart]()

        val headers = mutable.ListBuffer[ParameterisedHeader]()

        parser.setContentHandler(new AbstractContentHandler {
          override def body(desc: BodyDescriptor, is: InputStream): Unit = {

            val body = ByteString(Stream.continually(is.read).takeWhile(-1 != _).map(_.toByte).toArray)

            parts += DataPart(headers.toList, body)
            headers.clear
          }

          override def field(field: Field): Unit = {
            headers += ParameterisedHeader(field.getName, field.getBody)
          }
        })

        parser.parse(new ByteArrayInputStream(requestData.body.toArray))

        parts.toList

      case None =>
        throw new IllegalStateException("expected multipart body")
    }
  }
}

object SinglePartRequestConverter extends SimpleRequestConverter[ByteString, String] {
  def isDefinedAt(requestData: UnparsedRequestData): Boolean =
    !requestData.isMultipart

  def convertBody(requestData: UnparsedRequestData) =
    new String(requestData.body.toArray, requestData.charset)
}

object MimeFixupRequestConverter extends SimpleRequestConverter[ByteString, ByteString]
  with LazyLogging {

  import com.cyclone.wsman.impl.SeqUtils._

  def isDefinedAt(requestData: UnparsedRequestData): Boolean = requiresFixup(requestData)

  def convertBody(requestData: UnparsedRequestData): ByteString = {
    logger.debug(s"Fixing up malformed mime $requestData")

    val fixed = fixup(requestData)
    logger.debug(s"Fixed up malformed mime $fixed")

    fixed
  }

  private def fixup(requestData: UnparsedRequestData): ByteString = {
    // Add crlf after last field of second (final) part which should always be "application/octet-stream" 
    val appOct = "application/octet-stream"

    val fixed = for {
      boundary <- requestData.multipartBoundary
      old <- oldBoundaryBytes(requestData).map(_.toArray)
    } yield {
      var data: Seq[Byte] = requestData.body

      // Add CRLFCRLF before second boundary (zero based)
      data = replaceIndexedOccurrence(1, data, old, bytes(requestData, "\r\n\r\n--" + boundary))

      // Add CRLF before last boundary
      data = replaceIndexedOccurrenceFromEnd(0, data, old, bytes(requestData, "\r\n--" + boundary))

      replaceIndexedOccurrenceFromEnd(0, data, bytes(requestData, appOct), bytes(requestData, appOct + "\r\n"))
    }

    ByteString(fixed.getOrElse(requestData.body): _*)
  }

  private def requiresFixup(requestData: UnparsedRequestData) = {
    if (!requestData.isMultipart)
      false
    else {
      // Good mime contains cr lf then the boundary then --
      goodEndBoundaryBytes(requestData).exists { end =>
        !requestData.body.containsSlice(end)
      }
    }
  }

  private def oldBoundaryBytes(requestData: UnparsedRequestData): Option[ByteString] =
    requestData.multipartBoundary.map { boundary =>
      bytes(requestData, "--" + boundary)
    }

  private def goodEndBoundaryBytes(requestData: UnparsedRequestData): Option[ByteString] =
    requestData.multipartBoundary.map { boundary =>
      bytes(requestData, "\r\n--" + boundary + "--")
    }

  private def bytes(requestData: UnparsedRequestData, string: String): ByteString =
    ByteString(string, requestData.charset)
}

object LastPartRequestConverter extends SimpleRequestConverter[MultipartBody, String] {
  def isDefinedAt(requestData: MultipartRequestData) = true

  def convertBody(requestData: MultipartRequestData) =
    new String(requestData.body.last.body.toArray, requestData.charset)
}

/**
  * Performs Kerberos request decryption see http://msdn.microsoft.com/en-us/library/cc251574.aspx
  *
  * @author Jeremy.Stone
  */
case class KerberosSessionDecryptRequestConverter(token: Token) extends SimpleRequestConverter[MultipartBody, String] {

  private val tokenLengthByteCount = 4

  private def contentTypeProtocol(request: HttpRequest): Option[String] = {
    val f = ContentTypeFieldImpl.PARSER.parse(new RawField("Content-Type", request.entity.contentType.value), null)
    Option(f.getParameter("protocol"))
  }

  def isDefinedAt(requestData: MultipartRequestData): Boolean =
    contentTypeProtocol(requestData.request) match {
      case Some(protocol) => protocol.contains("Kerberos-session-encrypted")
      case None           => false
    }

  def convertBody(requestData: MultipartRequestData): String = {

    val metaPart = requestData.body.head
    val dataPart = requestData.body.last

    val (_, messageAndTokenBytes) = dataPart.body.splitAt(tokenLengthByteCount)

    val decrypted = token.decrypt(messageAndTokenBytes)

    decrypted.decodeString(charset(metaPart))
  }

  private def charset(metaPart: DataPart) = {
    val charset = for (
      header <- metaPart.header("OriginalContent");
      charset <- header.getParameter("charset")
    ) yield Charset.forName(charset)

    charset.getOrElse(Charset.defaultCharset())
  }
}

case class IdentityConverter[T]() extends SimpleRequestConverter[T, T] {
  def isDefinedAt(requestData: RequestData[T]) = true

  def convertBody(requestData: RequestData[T]): T = requestData.body
}

object ToXMLConverter extends SimpleRequestConverter[String, Elem] with LazyLogging {
  def isDefinedAt(requestData: RequestData[String]) = true

  def convertBody(requestData: RequestData[String]): Elem = {
    val xml = XML.loadString(requestData.body)

    logger.debug(s"Soap packet received:\n${XmlUtils.prettyPrint(xml)}")

    xml
  }
}

object RequestConverterComponent {

  def toXmlConverterPlain: RequestConverter.Converter[ByteString, Elem] = MimeFixupRequestConverter
    .orElse(IdentityConverter[ByteString]())
    .andThen(SinglePartRequestConverter
      .orElse(MimeRequestConverter
        .andThen(LastPartRequestConverter)))
    .andThen(ToXMLConverter)

  def toXmlConverter(token: Token): RequestConverter.Converter[ByteString, Elem] = MimeFixupRequestConverter
    .orElse(IdentityConverter[ByteString]())
    .andThen(SinglePartRequestConverter
      .orElse(MimeRequestConverter
        .andThen(KerberosSessionDecryptRequestConverter(token).orElse(LastPartRequestConverter))))
    .andThen(ToXMLConverter)
}