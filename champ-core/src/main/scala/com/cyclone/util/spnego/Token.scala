package com.cyclone.util.spnego

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets.UTF_8
import java.security.{MessageDigest, PrivilegedExceptionAction}
import java.util
import javax.security.auth.Subject
import javax.security.auth.kerberos.KerberosPrincipal

import akka.http.scaladsl.model.HttpHeader
import akka.util.ByteString
import org.apache.commons.codec.binary.Base64
import org.ietf.jgss.{GSSContext, MessageProp}

case class TokenParseException(message: String) extends Exception(message)

class Tokens(tokenValidity: Long, signatureSecret: Array[Byte]) {
  private def newExpiration: Long = System.currentTimeMillis + tokenValidity

  private[spnego] def sign(token: Token): String = {
    val md = MessageDigest.getInstance("SHA")
    md.update(token.principal.getBytes(UTF_8))
    val bb = ByteBuffer.allocate(8)
    bb.putLong(token.expiration)
    md.update(bb.array)
    md.update(signatureSecret)
    new Base64(0).encodeToString(md.digest)
  }

  def create(
    principal: String,
    maybeServerToken: Option[Array[Byte]],
    gssContext: GSSContext
  ): Token = Token(principal, newExpiration, maybeServerToken, gssContext)

  def serialize(token: Token): String =
    List(token.principal, token.expiration, sign(token)).mkString("&")
}

case class Token private[spnego] (
  principal: String,
  expiration: Long,
  maybeServerToken: Option[Array[Byte]],
  context: GSSContext
) {

  private lazy val subject: Subject = {
    val principals = new util.HashSet[KerberosPrincipal]
    principals.add(new KerberosPrincipal(principal))

    new Subject(false, principals, new util.HashSet[Object], new util.HashSet[Object])
  }

  def expired: Boolean = System.currentTimeMillis > expiration

  def encrypt(data: ByteString): ByteString = {
    val bytes = Subject.doAs(subject, new PrivilegedExceptionAction[Array[Byte]] {
      def run: Array[Byte] = {
        context.wrap(data.toArray[Byte], 0, data.length, new MessageProp(true))
      }
    })

    ByteString(bytes)
  }

  def challengeHeader: HttpHeader =
    SpnegoAuthenticator.challengeHeader(maybeServerToken)

  def decrypt(data: ByteString): ByteString = {
    val bytes = Subject.doAs(subject, new PrivilegedExceptionAction[Array[Byte]] {
      def run: Array[Byte] = {
        context.unwrap(data.toArray[Byte], 0, data.length, new MessageProp(true))
      }
    })

    ByteString(bytes)
  }

  def dispose(): Unit = context.dispose()
}
