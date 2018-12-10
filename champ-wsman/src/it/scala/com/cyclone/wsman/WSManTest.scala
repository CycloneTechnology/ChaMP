package com.cyclone.wsman

import java.io.InputStreamReader
import java.net.InetAddress
import java.text.SimpleDateFormat

import com.cyclone.akka.{ActorMaterializerComponent, ActorSystemComponent, ActorSystemShutdown, TestKitSupport}
import com.cyclone.command._
import com.cyclone.util.kerberos.TestKerberosDeployment
import com.cyclone.util.net.{AuthenticationMethod, HostAndPort, JavaNamingDnsLookupComponent, PasswordSecurityContext}
import com.cyclone.util.shell.ShellOutputStream
import com.cyclone.util.{Base64Utils, Password, PasswordCredentials}
import com.cyclone.wsman.command._
import com.cyclone.wsman.impl.WSManAvailability
import com.cyclone.wsman.impl.subscription.push.GuavaKerberosTokenCacheComponent
import com.google.common.base.Charsets
import com.google.common.io.CharStreams
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Inside, Matchers, WordSpecLike}
import scalaz.Scalaz._
import scalaz.{-\/, \/-}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class WSManTest
    extends TestKitSupport
    with WordSpecLike
    with Matchers
    with ScalaFutures
    with Inside
    with IntegrationPatience
    with WSManTestProperties
    with TestWSManComponent
    with GuavaKerberosTokenCacheComponent
    with ActorSystemComponent
    with ActorMaterializerComponent
    with TestKerberosDeployment
    with WSManTestFileCreation
    with JavaNamingDnsLookupComponent
    with ActorSystemShutdown {

  val directoryResultMatcher = include("Directory of") and include("bytes free")

  val ssl = false
  val httpUrl = WSMan.httpUrlFor(hostAndPort, ssl)
  val securityContext = kerberosSecurityContext

  val target = WSManTarget(httpUrl, securityContext)

  private def domainCredentials: PasswordCredentials = PasswordCredentials(user, Password(password), Some(domain))

  private def sleepCommandQuery(sleepSecs: Int) =
    WSManRunShellCommand("powershell", "-Command", "Start-Sleep -s " + sleepSecs)

  "WSMan" when {
    implicit val timeoutContext: TimeoutContext =
      TimeoutContext(deadline = OperationDeadline.reusableTimeout(10.seconds))

    "test connectability" must {
      "indicate possible when scheme wrong" in {
        inside(
          wsman
            .testConnection(
              WSManTarget(
                httpUrl,
                PasswordSecurityContext(
                  PasswordCredentials.fromStrings("someUser", "somePassword"),
                  AuthenticationMethod.Kerberos
                )
              )
            )
            .futureValue
        ) {
          case -\/(e) => e shouldBe a[WSManAuthenticationError]
        }
      }

      "indicate not possible when device not listening on port" in {
        wsman
          .testConnection(
            WSManTarget(
              WSMan.httpUrlFor(HostAndPort.fromParts(hostAndPort.host, 123), ssl),
              securityContext
            )
          )
          .futureValue shouldBe WSManAvailabilityTestError(WSManAvailability.NotListening).left
      }

      "indicate not possible if device listening but wrong path" in {
        wsman
          .testConnection(
            WSManTarget(WSMan.httpUrlFor(HostAndPort.fromString("dev:8090"), ssl), securityContext)
          )
          .futureValue shouldBe WSManAvailabilityTestError(WSManAvailability.PathNotFound).left
      }

      "indicate possible when can connect" in {
        wsman.testConnection(target).futureValue shouldBe ().right
      }
    }

    "indicate error when not connectable" in {
      inside(
        wsman
          .executeCommandOrError(
            WSManTarget(WSMan.httpUrlFor(HostAndPort.fromString("npbuild"), ssl), securityContext),
            Identify
          )
          .futureValue
      ) {
        case -\/(e) =>
          // Would like WSManIOError but kerberos gives GSSException because host not known to the KDC
          assert(e.message != null, "Null message")
          assert(e.message != "", "No message")
      }
    }

    "ok when using domain credentials" in {
      val query = EnumerateBySelector.fromClassName("Win32_OperatingSystem")

      inside(
        wsman
          .executeCommandOrError(
            WSManTarget(
              httpUrl,
              PasswordSecurityContext(domainCredentials, AuthenticationMethod.Kerberos)
            ),
            query
          )
          .futureValue
      ) {
        case \/-(result) =>
          result.instances should have size 1

          result.allPropertyNames should contain("Version")
      }
    }

    "works with resolvable IP addresses" in {
      val query = EnumerateBySelector.fromClassName("Win32_OperatingSystem")

      inside(
        wsman
          .executeCommandOrError(
            WSManTarget(
              WSMan.httpUrlFor(HostAndPort.fromString(hostAddress), ssl),
              securityContext
            ),
            query
          )
          .futureValue
      ) {
        case \/-(result) =>
          result.instances should have size 1

          result.allPropertyNames should contain("Version")
      }
    }

    "extract dates as strings" in {
      val query = EnumerateBySelector.fromClassName(
        "Win32_OperatingSystem",
        propertyRestriction = PropertyRestriction.restrictedTo("LastBootUpTime")
      )

      inside(wsman.executeCommandOrError(target, query).futureValue) {
        case \/-(result) =>
          val instance = result.instances.head
          val cimdate = instance.stringProperty("LastBootUpTime").get

          new SimpleDateFormat("YYYY-MM-dd").parse(cimdate.substring(0, 10))
      }
    }

    /**
      * Get 401 response when run against localhost and have set up service principle for that host.
      * This caused a hang. Check that it is fixed...
      */
    "does not hang when run against localhost" in {
      val query = EnumerateBySelector.fromClassName("Win32_Service")

      inside(
        wsman
          .executeCommandOrError(
            WSManTarget(
              WSMan.httpUrlFor(HostAndPort.fromString(InetAddress.getLocalHost.getHostName), ssl),
              securityContext
            ),
            query
          )
          .futureValue
      ) {
        case -\/(e) => e shouldBe a[WSManError]
      }
    }

    /**
      * NP-2352
      */
    "returns error when connection refused" in {
      val query = EnumerateBySelector.fromClassName("Win32_Service")

      inside(
        wsman
          .executeCommandOrError(
            WSManTarget(
              WSMan.httpUrlFor(
                HostAndPort.fromString(InetAddress.getLocalHost.getHostName).withDefaultPort(1234),
                ssl
              ),
              securityContext
            ),
            query
          )
          .futureValue
      ) {
        case -\/(e) => e shouldBe a[WSManIOError]
      }
    }

    "using identify" must {
      "work" in {
        inside(wsman.executeCommandOrError(target, Identify).futureValue) {
          case \/-(result) =>
            result.productVendor shouldBe Some("Microsoft Corporation")
            assert(
              result.securityProfileNames.contains(
                "http://schemas.dmtf.org/wbem/wsman/1/wsman/secprofile/http/spnego-kerberos"
              )
            )
        }
      }

      "return authentication exception when incorrect security context type" in {
        inside(
          wsman
            .executeCommandOrError(
              WSManTarget(
                httpUrl,
                PasswordSecurityContext(credentials, AuthenticationMethod.Basic)
              ),
              Identify
            )
            .futureValue
        ) {
          case -\/(e) => e shouldBe a[WSManAuthenticationError]
        }
      }
    }

    "enumerating" must {
      "work with no selector" in {
        val query = EnumerateBySelector.fromClassName("Win32_Service")

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances should not be empty

            result.allPropertyNames should contain allOf ("Name", "PathName")
        }
      }

      "work with no selector with specified property restriction" in {
        val query = EnumerateBySelector.fromClassName(
          "Win32_Service",
          propertyRestriction = PropertyRestriction.restrictedTo("Name", "PathName")
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances should not be empty

            result.allPropertyNames should contain only ("Name", "PathName")
        }
      }

      "ignore unknown properties in restrictions" in {
        val query = EnumerateBySelector.fromClassName(
          "Win32_Service",
          propertyRestriction = PropertyRestriction.restrictedTo("Win32_Service", "Name", "NO_SUCH_PROPERTY")
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances should not be empty

            result.allPropertyNames should contain("Name")
        }
      }

      "work for single result" in {
        val query = EnumerateBySelector.fromClassName(
          "Win32_Service",
          selectorClause = SelectorClause(Set(Selector("Name", "WinRM")))
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances should have size 1
            result.instances.head.stringProperty("Name").get shouldBe "WinRM"
        }
      }

      "return multiple results" in {
        val query = EnumerateBySelector.fromClassName(
          "Win32_Service",
          propertyRestriction = PropertyRestriction.restrictedTo("Name", "PathName"),
          selectorClause = SelectorClause(Set(Selector("StartMode", "Auto")))
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances.size should be > 1

            result.allPropertyNames should contain only ("Name", "PathName")
        }
      }

      "return empty when no matching instance" in {
        val query = EnumerateBySelector.fromClassName(
          "Win32_Service",
          selectorClause = SelectorClause(Set(Selector("Name", "NOSUCHSERVICE")))
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances shouldBe empty
        }
      }

      "select case insensitively" in {
        val query = EnumerateBySelector.fromClassName(
          "Win32_Service",
          selectorClause = SelectorClause(Set(Selector("name", "winrm")))
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances should have size 1

            result.instances.head.stringProperty("Name").get shouldBe "WinRM"
        }
      }

      "restrict properties case insensitively" in {
        val query = EnumerateBySelector.fromClassName(
          "Win32_Service",
          propertyRestriction = PropertyRestriction.restrictedTo("name", "pathname"),
          selectorClause = SelectorClause(Set(Selector("Name", "WinRM")))
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances should have size 1

            result.allPropertyNames should contain only ("Name", "PathName")
            result.instances.head.stringProperty("Name").get shouldBe "WinRM"
        }
      }

      "work with selector and property restriction" in {
        val query = EnumerateBySelector.fromClassName(
          "Win32_Service",
          propertyRestriction = PropertyRestriction.restrictedTo("Name", "PathName"),
          selectorClause = SelectorClause(Set(Selector("Name", "WinRM")))
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances should have size 1

            result.allPropertyNames should contain only ("Name", "PathName")
            result.instances.head.stringProperty("Name").get shouldBe "WinRM"
        }
      }

      "not resolve by default" in {
        val query = EnumerateBySelector.fromClassName("Win32_DiskDriveToDiskPartition")

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            val instance = result.instances.head

            result.instances.head
              .properties("Antecedent") shouldBe a[WSManPropertyValue.ForReference]
            result.instances.head
              .properties("Dependent") shouldBe a[WSManPropertyValue.ForReference]
        }
      }

      "resolve if required" in {
        val query = EnumerateBySelector.fromClassName(
          "Win32_DiskDriveToDiskPartition",
          propertyRestriction = PropertyRestriction.restrictedTo("Antecedent", "Dependent"),
          resolveReferences = true
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances.head
              .properties("Antecedent") shouldBe a[WSManPropertyValue.ForInstance]
            result.instances.head.properties("Dependent") shouldBe a[WSManPropertyValue.ForInstance]

            val WSManPropertyValue.ForInstance(inst) =
              result.instances.head.properties("Antecedent")
            inst.stringProperty("Description").get shouldBe "Disk drive"
        }
      }

      "return error when selector properties don't exist" in {
        val query = EnumerateBySelector.fromClassName(
          "Win32_Service",
          propertyRestriction = PropertyRestriction.restrictedTo("Name", "PathName"),
          selectorClause = SelectorClause(Set(Selector("Blah", "123")))
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case -\/(e) => e shouldBe a[WSManQueryError]
        }
      }
    }

    "enumerating by wql" must {
      "work" in {
        val query = EnumerateByWQL("SELECT Name, PathName FROM Win32_Service where Name=\"WinRM\"")

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances should have size 1
            result.instances.head.stringProperty("Name").get shouldBe "WinRM"
        }
      }

      "resolve references if reqd" in {
        val query =
          EnumerateByWQL("SELECT * FROM Win32_DiskDriveToDiskPartition", resolveReferences = true)

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            val WSManPropertyValue.ForInstance(inst) =
              result.instances.head.properties("Antecedent")
            inst.stringProperty("Description").get shouldBe "Disk drive"
        }
      }
    }

    "getting singleton" must {
      "work with selector" in {
        val query = Get.fromClassName(
          "Win32_Service",
          selectorClause = SelectorClause(Set(Selector("Name", "WinRM")))
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances should have size 1
            result.instances.head.stringProperty("Name").get shouldBe "WinRM"
        }
      }

      "select case insensitively" in {
        val query = Get.fromClassName(
          "Win32_Service",
          selectorClause = SelectorClause(Set(Selector("name", "winrm")))
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances should have size 1
            result.instances.head.stringProperty("Name").get shouldBe "winrm"
        }
      }

      "work with no selector" in {
        val query = Get.fromClassName("Win32_OperatingSystem")

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.instances should have size 1

            result.allPropertyNames should contain("Version")
        }
      }

      "return error when invalid url" in {
        val query = Get(ResourceUri("http://unknown/SOME_UNKNOWN_CLASS"))

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case -\/(e) => e shouldBe a[WSManQueryError]
        }
      }

      "return error when invalid query class" in {
        val query = Get.fromClassName("SOME_UNKNOWN_CLASS")

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case -\/(e) => e shouldBe a[WSManQueryError]
        }
      }

      "return error when no matching instance" in {
        val query = Get.fromClassName(
          "Win32_Service",
          selectorClause = SelectorClause(Set(Selector("Name", "NOSUCHSERVICE")))
        )

        try {
          wsman.executeCommandOrError(target, query).futureValue
        } catch {
          case e: Exception => e.getMessage should include("cannot find the resource")
        }
      }

      "return error when selector properties don't exist" in {
        val query = Get.fromClassName(
          "Win32_Service",
          propertyRestriction = PropertyRestriction.restrictedTo("Name", "PathName"),
          selectorClause = SelectorClause(Set(Selector("Blah", "123")))
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case -\/(e) => e shouldBe a[WSManQueryError]
        }
      }
    }

    "run remote command" in {
      val query = WSManRunShellCommand("dir", directory)
      inside(wsman.executeCommandOrError(target, query).futureValue) {
        case \/-(result) =>
          result.exitCode shouldBe 0
          result.filterFor(ShellOutputStream.STDOUT) should directoryResultMatcher
      }
    }

    "run remote command" must {
      "work" in {
        val file = createTempFile

        val query = WSManRunShellCommand("powershell", "-Command", "dir " + directory)

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.filterFor(ShellOutputStream.STDOUT) should include(file.getName)
        }
      }

      "detect error code" in {
        val query = WSManRunShellCommand("blobble", "blibble")

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.exitCode shouldBe 1
            result.filterFor(ShellOutputStream.STDERR) should
            startWith("'blobble' is not recognized as an internal or external command")
        }
      }

      "return multiple response parts" in {
        val query = WSManRunShellCommand("dir", "c:\\windows\\system32")

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.filterFor(ShellOutputStream.STDOUT) should directoryResultMatcher
        }
      }

      "allow encoded powershell commands" in {
        val file = createTempFile

        val query = WSManRunShellCommand(
          "powershell",
          "-EncodedCommand",
          Base64Utils.encodeBase64(("dir " + directory).getBytes(Charsets.UTF_16LE))
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.filterFor(ShellOutputStream.STDOUT) should include(file.getName)
        }
      }

      "return error for empty command" in {
        val query = WSManRunShellCommand("", "abcde")

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case -\/(e) => e shouldBe a[WSManQueryError]
        }
      }

      "allow running from external script resource" in {
        val is = getClass.getResourceAsStream("./installedsoftware.txt")
        val r = new InputStreamReader(is, Charsets.UTF_8)
        val sb: Appendable = new java.lang.StringBuilder()
        CharStreams.copy(r, sb)

        val query = WSManRunShellCommand(
          "powershell",
          "-EncodedCommand",
          Base64Utils.encodeBase64(sb.toString.getBytes(Charsets.UTF_16LE))
        )

        inside(wsman.executeCommandOrError(target, query).futureValue) {
          case \/-(result) =>
            result.filterFor(ShellOutputStream.STDOUT) should include(
              "Publisher      : Microsoft Corporation"
            )
        }
      }
    }
  }

  "WSMan" must {
    "timeout if command runs too long" in {
      implicit val timeoutContext: TimeoutContext =
        TimeoutContext(OperationDeadline.fromNow(4.seconds))

      val query = sleepCommandQuery(5)

      inside(wsman.executeCommandOrError(target, query).futureValue) {
        case -\/(e) => e shouldBe a[WSManQueryError]
      }
    }

    "not timeout if completes in time" in {
      implicit val timeoutContext: TimeoutContext =
        TimeoutContext(OperationDeadline.fromNow(7.seconds))

      val query = sleepCommandQuery(5)

      inside(wsman.executeCommandOrError(target, query).futureValue) {
        case \/-(result) =>
          result.exitCode shouldBe 0
      }
    }
  }
}
