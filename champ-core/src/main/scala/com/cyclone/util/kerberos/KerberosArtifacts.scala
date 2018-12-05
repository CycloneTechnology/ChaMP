package com.cyclone.util.kerberos

import scala.collection.JavaConverters._
import akka.NotUsed
import akka.stream.scaladsl.{Source, StreamConverters}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

/**
  * Defines Kerberos related artifact to deploy (e.g. as files) via a [[KerberosDeployer]]
  *
  * @param kerb5ConfContent     the content of the kerb5.conf file
  * @param loginConfContent     the content of the login.conf file
  * @param servicePrincipalName the service principal name for inbound connections
  * @param keyTabContent        the content of the keytab for inbound connections
  */
case class KerberosArtifacts(
  kerb5ConfContent: Source[ByteString, NotUsed],
  loginConfContent: Source[ByteString, NotUsed],
  servicePrincipalName: String,
  keyTabContent: Source[ByteString, NotUsed]
)

object KerberosArtifacts {

  /**
    * Gets a source from a resource file on the classpath
    */
  def resourceSource(resourceName: String): Source[ByteString, NotUsed] =
    StreamConverters
      .fromInputStream(() => getClass.getResourceAsStream(resourceName))
      .mapMaterializedValue(_ => NotUsed)

  /**
    * Utility to create a simple kerb5.conf configuration for a single realm
    *
    * @param realm      used for the default realm in the [libdefaults] section
    *                   and a single entry in the [realm] section
    * @param kdcHosts   host names or addresses (with optional ports separated by colons)
    *                   of key distribution centres (domain controllers in Microsoft terminology)
    * @param realmHosts names of hosts or domains in the realm (a . prefix indicates an entire domain)
    */
  def singleRealmKrb5Config(
    realm: String,
    kdcHosts: Seq[String],
    realmHosts: Seq[String]
  ): String = {

    val ucRealm = realm.toUpperCase
    val lcHosts = realmHosts.map(_.toLowerCase)

    s"""[libdefaults]
       |   default_realm = $ucRealm
       |
       |   dns_lookup_realm = false
       |   dns_lookup_kdc = false
       |
       |   allow_weak_crypto = yes
       |
       |   default_tkt_enctypes = aes128-cts des3-cbc-sha1 rc4-hmac des-cbc-md5 des-cbc-crc
       |   default_tgs_enctypes = aes128-cts des3-cbc-sha1 rc4-hmac des-cbc-md5 des-cbc-crc
       |   permitted_enctypes = aes128-cts des3-cbc-sha1 rc4-hmac des-cbc-md5 des-cbc-crc
       |
       |[realms]
       |   $ucRealm = {
       |#     Set your kerberos Key Distrib Centers here
       |      ${kdcHosts.map(kdc => s"kdc = $kdc").mkString("\n")}
       |
       |      udp_preference_limit=1
       |      kdc_timeout=5000
       |      max_retries=3
       |   }
       |
       |[domain_realm]
       |#  Map host names to realms here
       |   ${lcHosts.map(host => s"$host = $ucRealm").mkString("\n")}
       |""".stripMargin
  }

  /**
    * Creates a single realm config from application.conf file
    */
  def simpleFromConfig: KerberosArtifacts = {
    val config = ConfigFactory.load()

    val realm = config.getString("cyclone.kerberos.realm")
    val kdcHosts = config.getStringList("cyclone.kerberos.kdcHosts").asScala
    val realmHosts = config.getStringList("cyclone.kerberos.realmHosts").asScala

    KerberosArtifacts(
      kerb5ConfContent = Source.single(ByteString(singleRealmKrb5Config(realm, kdcHosts, realmHosts))),
      loginConfContent = resourceSource("/login.conf"),
      // FIXME next params reqd for push subscriptions
      "",
      keyTabContent = Source.empty
    )
  }

}
