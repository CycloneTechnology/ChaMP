package com.cyclone.wsman

import java.io.File
import java.net.InetAddress

import com.cyclone.util.PasswordCredentials
import com.cyclone.util.net.{AuthenticationMethod, HostAndPort, PasswordSecurityContext}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable.ListBuffer

trait WSManTestProperties {

  val config = ConfigFactory.load()

  val host = config.getString("wsman.host")
  val domain = config.getString("wsman.domain")
  val user = config.getString("wsman.username")
  val password = config.getString("wsman.password")

  val hostAddress: String = InetAddress.getByName(host).getHostAddress

  def credentials = PasswordCredentials.fromStrings(user, password)

  val hostAndPort = HostAndPort.fromString(host)

  val kerberosSecurityContext =
    PasswordSecurityContext(PasswordCredentials.fromStrings(user, password), AuthenticationMethod.Kerberos)

  val basicAuthSecurityContext = PasswordSecurityContext(credentials, AuthenticationMethod.Basic)
}

trait WSManTestFileCreation {
  self: WSManTestProperties =>

  // NOTE host needs wsmantest share writable by us
  val directoryPart = "wsmantest"
  val directory = "c:\\" + directoryPart

  val tempDir = "\\\\" + host + "\\" + directoryPart
  val tempDirWMI = "\\\\" + directoryPart + "\\\\"

  def createTempFiles(num: Int): List[File] = {

    val files = ListBuffer[File]()
    for (i <- 0 until num) {
      files += createTempFile("wsmantest_" + i + "_")
    }

    files.toList
  }

  def createTempFile(prefix: String) = {
    val file = File.createTempFile(prefix, ".tmp", new File(tempDir))
    file.deleteOnExit()
    file
  }

  def createTempFile: File = createTempFile("wsmantest")
}
