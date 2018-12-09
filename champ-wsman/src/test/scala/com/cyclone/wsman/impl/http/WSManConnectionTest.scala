package com.cyclone.wsman.impl.http

import java.io.{ByteArrayInputStream, IOException}
import java.net.ConnectException
import java.nio.charset.UnsupportedCharsetException
import java.util.concurrent.{ExecutionException, Executor, TimeUnit, TimeoutException}

import com.cyclone.util.net._
import com.cyclone.util.{PasswordCredentials, SynchronizedClassBasedMockeryComponent}
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman._
import com.cyclone.wsman.impl.WSManAvailability
import com.cyclone.wsman.impl.http.settings.HttpSettings
import com.cyclone.wsman.impl.xml.RequestXMLWithNoDeadline
import com.google.common.base.Charsets
import com.ning.http.client.listenable.AbstractListenableFuture
import com.ning.http.client.{Request, Response}
import org.jmock.AbstractExpectations._
import org.jmock.Expectations
import org.scalatest._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import scalaz.-\/

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.xml.Elem

/**
  * Tests for [[WSManConnection]]
  */
class WSManConnectionTest
    extends WordSpec
    with OneInstancePerTest
    with Matchers
    with ScalaFutures
    with Inside
    with IntegrationPatience {

  val address = "http://someAddress/wsman"

  val defaultSecurityContext =
    PasswordSecurityContext(PasswordCredentials.fromStrings("user", "pwd"), AuthenticationMethod.Basic)

  val faultXML: Elem =
    <Envelope>
      <Header/>
      <Body>
        <Fault>
          <Code>
            <Value>s:Receiver</Value>
            <Subcode>
              <Value>wsen:InvalidEnumerationContext</Value>
            </Subcode>
          </Code>
          <Reason>
            <Text>The supplied enumeration context is invalid.</Text>
          </Reason>
        </Fault>
      </Body>
    </Envelope>

  val goodXML: Elem =
    <Envelope>
      <Header/>
      <Body>
        <PullResponse>
          <etc/>
        </PullResponse>
      </Body>
    </Envelope>

  val someXML: RequestXMLWithNoDeadline = new RequestXMLWithNoDeadline {
    def xml(toAddress: String): Elem = <someXmlNode/>
  }

  class Fixture(securityContext: SecurityContext = defaultSecurityContext) extends MockAsyncHttpClientComponent
    with JavaNamingDnsLookupComponent
    with SynchronizedClassBasedMockeryComponent{

    lazy val httpSettings  = HttpSettings(
      connectTimeout = 1.second,
    defaultRequestTimeout = 1.second,
    minimumRequestTimeout = 1.second)

    val connection = new DefaultWSManConnection(HttpUrl.fromString(address), securityContext, asyncHttpClient, dnsLookup, httpSettings)


      def willRequest(response: Response): Unit = {
        mockery.checking(new Expectations {
          e =>
          oneOf(asyncHttpClient).executeRequest(`with`(aNonNull(classOf[Request])))
          will(returnValue(listenableFuture(response)))
        })
      }

      def willRequest(t: Throwable): Unit = {
        mockery.checking(new Expectations {
          e =>
          oneOf(asyncHttpClient).executeRequest(`with`(aNonNull(classOf[Request])))
          will(returnValue(listenableFuture(t)))
        })
      }

      def executeRequest: Future[WSManErrorOr[Elem]] =connection. executeSoapRequest(someXML)

      def doDetermineAvailability: Future[WSManAvailability] = connection.determineAvailability(httpSettings.defaultRequestTimeout)

       def response(statusCode: Int, text: String) = {
        val resp = mockery.mock(classOf[Response])

        mockery.checking(new Expectations {
          allowing(resp).getStatusCode
          will(returnValue(statusCode))

          allowing(resp).getResponseBody()
          will(returnValue(text))
        })

        resp
      }

       def response(statusCode: Int, headers: Map[String, Seq[String]]) = {
        val resp = mockery.mock(classOf[Response])

        mockery.checking(new Expectations {
          allowing(resp).getStatusCode
          will(returnValue(statusCode))

          for ((name, headers) <- headers) {
            allowing(resp).getHeaders(name)
            val list = headers.asJava
            will(returnValue(list))
          }

        })

        resp
      }


       def response(statusCode: Int, xml: Elem) = {
        def stream(xml: Elem) =
          new ByteArrayInputStream(xml.toString().getBytes(Charsets.UTF_8))

        val resp = mockery.mock(classOf[Response])
        val notRetrieved = "RESP_NOT_RETRIEVED"
        val retrieved = "RESP_RETRIEVED"

        val respRetrieved = mockery.states("responseRetrieved").startsAs(notRetrieved)

        mockery.checking(new Expectations {
          allowing(resp).getStatusCode
          will(returnValue(statusCode))

          allowing(resp).getResponseBody()
          when(respRetrieved.is(notRetrieved))
          will(returnValue(xml.toString))
          `then`(respRetrieved.is(retrieved))

          allowing(resp).getResponseBodyAsStream
          when(respRetrieved.is(notRetrieved))
          will(returnValue(stream(xml)))
          `then`(respRetrieved.is(retrieved))
        })

        resp
      }
  }

  "WSManConnection" must {
    "makes request - success" in new Fixture {
        willRequest(response(200, goodXML))

        executeRequest
      mockery.assertIsSatisfied()
    }

    // NP-2378
    "makes request - examines xml for fault even if 200 response" in new Fixture {
        willRequest(response(200, faultXML))

        inside(executeRequest.futureValue) {
          case -\/(e) => e shouldBe a[WSManQueryError]
        }
      }


    "makes request - WSManRequestException for 400 response when non-XML response" in new Fixture {
        willRequest(response(400, "someText"))

        inside(executeRequest.futureValue) {
          case -\/(e) => e shouldBe a[WSManRequestError]
        }
      }

    "makes request - WSManQueryException for 400 response when XML response" in new Fixture {
        willRequest(response(400, faultXML))

        inside(executeRequest.futureValue) {
          case -\/(e) => e shouldBe a[WSManQueryError]
        }
      }

    "makes request - WSManAuthenticationException for 401 response" in new Fixture {
        willRequest(response(401, "someText"))

        inside(executeRequest.futureValue) {
          case -\/(e) => e shouldBe a[WSManAuthenticationError]
        }
      }

    "fails to make request - WSManIOException" in new Fixture {
        willRequest(new IOException())

        inside(executeRequest.futureValue) {
          case -\/(e) => e shouldBe a[WSManIOError]
        }
      }

    // NP-2414
    "request times out - WSManIOException" in new Fixture {
        willRequest(new TimeoutException())

        inside(executeRequest.futureValue) {
          case -\/(e) => e shouldBe RequestTimeout
        }
      }

    // NP-2414
    "handles bad response body nicely" in new Fixture {
        val resp: Response = mockery.mock(classOf[Response])

        mockery.checking(new Expectations {
          allowing(resp).getStatusCode
          will(returnValue(500))

          allowing(resp).getResponseBody()
          will(throwException(new UnsupportedCharsetException("plain")))
        })

        willRequest(resp)

        inside(executeRequest.futureValue) {
          case -\/(e) => e shouldBe a[WSManRequestError]
        }
      }

    "availability - not connectable" in new Fixture {
        willRequest(new ConnectException)

        doDetermineAvailability.futureValue shouldBe WSManAvailability.NotListening
      }

    "availability - no url path" in new Fixture {
        willRequest(response(404, "ignored"))

        doDetermineAvailability.futureValue shouldBe WSManAvailability.PathNotFound
      }

    "availability - timeout" in new Fixture {
        willRequest(new TimeoutException)

        doDetermineAvailability.futureValue shouldBe WSManAvailability.Timeout
      }

    "availability - no authentication wrong scheme - using basic; challenge negotiate" in new Fixture(
        PasswordSecurityContext(PasswordCredentials.fromStrings("ignored", "ignored"), AuthenticationMethod.Basic)
      ) {
        willRequest(response(401, Map("WWW-Authenticate" -> List("Negotiate"))))

        doDetermineAvailability.futureValue shouldBe WSManAvailability.NoAuthWrongScheme
      }

    "availability - no authentication wrong scheme - using basic; challenge kerberos" in new Fixture  (
        PasswordSecurityContext(PasswordCredentials.fromStrings("ignored", "ignored"), AuthenticationMethod.Basic)
      ) {
        willRequest(response(401, Map("WWW-Authenticate" -> List("Kerberos"))))

        doDetermineAvailability.futureValue shouldBe WSManAvailability.NoAuthWrongScheme
      }

    "availability - no authentication wrong scheme - using kerberos; challenge basic" in new Fixture  (
        PasswordSecurityContext(PasswordCredentials.fromStrings("ignored", "ignored"), AuthenticationMethod.Kerberos)
      ) {
        willRequest(response(401, Map("WWW-Authenticate" -> List("Basic realm=blahblah"))))

        doDetermineAvailability.futureValue shouldBe WSManAvailability.NoAuthWrongScheme
      }

    "availability - no authentication right scheme - using basic; challenge basic" in new Fixture (
        PasswordSecurityContext(PasswordCredentials.fromStrings("ignored", "ignored"), AuthenticationMethod.Basic)
      ) {
        willRequest(response(401, Map("WWW-Authenticate" -> List("Basic realm=blahblah"))))

        doDetermineAvailability.futureValue shouldBe WSManAvailability.BadCredentials
      }

    "availability - no authentication right scheme - using kerberos; challenge kerberos" in new Fixture (
        PasswordSecurityContext(PasswordCredentials.fromStrings("ignored", "ignored"), AuthenticationMethod.Kerberos)
      ) {
        willRequest(response(401, Map("WWW-Authenticate" -> List("Kerberos"))))

        doDetermineAvailability.futureValue shouldBe WSManAvailability.BadCredentials
      }

    "availability - no authentication right scheme - using kerberos; challenge negotiate" in new Fixture  (
        PasswordSecurityContext(PasswordCredentials.fromStrings("ignored", "ignored"), AuthenticationMethod.Kerberos)
      ) {
        willRequest(response(401, Map("WWW-Authenticate" -> List("Negotiate"))))

        doDetermineAvailability.futureValue shouldBe WSManAvailability.BadCredentials
      }

    "availability - other response code" in new Fixture  (
        PasswordSecurityContext(PasswordCredentials.fromStrings("ignored", "ignored"), AuthenticationMethod.Basic)
      ) {
        willRequest(response(501, "ignored"))

        doDetermineAvailability.futureValue shouldBe WSManAvailability.OtherStatusCode(501, possibilyAvailable = true)
      }

    "availability - other exception" in new Fixture {
        val e = new IOException
        willRequest(e)

        doDetermineAvailability.futureValue shouldBe WSManAvailability.OtherException(e, possibilyAvailable = true)
      }
  }



  trait TestAsyncHttpClientFuture extends AbstractListenableFuture[Response] {
    override def addListener(listener: Runnable, exec: Executor): AbstractListenableFuture[Response] = {
      listener.run()
      this
    }

    def get(timeout: Long, unit: TimeUnit): Response = get

    def cancel(v: Boolean) = false

    def isCancelled = false

    def isDone = true

    def done(): Unit = throw new UnsupportedOperationException

    def abort(t: Throwable): Unit = throw new UnsupportedOperationException

    def content(v: Response) = throw new UnsupportedOperationException

    def touch(): Unit = throw new UnsupportedOperationException

    def getAndSetWriteHeaders(writeHeader: Boolean) = throw new UnsupportedOperationException

    def getAndSetWriteBody(writeBody: Boolean) = throw new UnsupportedOperationException
  }

  def listenableFuture(resp: Response): TestAsyncHttpClientFuture = new TestAsyncHttpClientFuture {
    def get: Response = resp
  }

  def listenableFuture(t: Throwable): TestAsyncHttpClientFuture = new TestAsyncHttpClientFuture {
    def get = throw new ExecutionException(t)
  }
}
