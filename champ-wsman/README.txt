IMPORTANT NOTES RE KERBEROS CONFIGURATION

General
=======
For windows the user with which we connect to WinRM needs to be in the domain admins group.

Push event delivery
===================
Note that for Microsoft WinRM to successfully connect to the servlet
(if need to use PUSH based delivery of events), some configuration is required:

   * Any user that we connect to a WinRM service as requires a service principal name (SPN).

	Assuming a domain user of WSMan use the following command on the domain controller:
	
	ktpass -princ HTTP/js.cyclone-technology.com@CYCLONE-TECHNOLOGY.COM 
	-mapuser CTL\WSMan out http-web.keytab -pass password /ptype KRB5_NT_PRINCIPAL
	/crypto AES128-SHA1	/kvno 0

	**NOTE**: Do not use the '-pass *' option that asks for the password, 
	this will break the password of the WSMan user and prevent the wsman library from logging in to the 
	WinRM service at all.

	  - Check: Open the properties of the WSMan user and go to the Attributes Editor tab.
		
		The properties of interest are:
		servicePrincipalName with a value of HTTP/js.cyclone-technology.com
		and 
		userPrincipalName with a value of HTTP/js.cyclone-technology.com@CYCLONE-TECHNOLOGY.COM
		
		By default for a user, the userPrincipalName
		is WSMan@cyclone-technology.com and the servicePrincipalName is not set.
		
		Experimentation indicates that one of these is required to be set as above for it to work.
		
  * Update the WSMan user properties to set 'This account supports AES 128 bit encryption' (on the Account tab)	

	**NOTE**: Use of the default encryption (RC4-HMAC) hits this 'bug' (rejected by Oracle/Sun - I suspect
	it's a Microsoft thing) http://bugs.java.com/view_bug.do?bug_id=6717189
	(see also https://community.oracle.com/thread/1527733)

  *	Need to copy the http-web.keytab and use it for the software. E.g. copy to the conf directory.
  
FAQ:
Get error: Cannot find key of appropriate type to decrypt AP REP - RC4 with HMAC
Check that have correct user (configured as indicated above) in the credentials. Keytab is (e.g. type 17 AES128 SHA1)
but token received is type 23 (RC4 HMAC). 
Make take some time for this to take effect (something is cached somewhere. Where???)

Defective token detected (Mechanism level: Invalid padding on Wrap Token)
When trying to decode received data. Java bug with RC4 HMAC - hence cannot use this encryption type.

Notes for installing openwsman-server
=====================================
(1) Requires openSUSE Linux (KDE best)
(2) Install openwsman-server via Yast
(3) To create passwords for basic auth use htpasswd (htpasswd2 doesnt seem to work for it)
which can be found in the thttpd package.
(4) To configure basic auth edit /etc/openwsman/openwsman.conf:
  (i) uncomment the basic_password_file line
  (ii) comment out the basic_authenticator_* lines (else server won't start - 'ambiguous' authentication error)
  (iii) comment out the ssl and port settings in the [cim] section (to allow simpler
  http access to the downstream CIM provider, sfcb)
(5) Logs for openwsman server when run as a service are in /var/log/wsmand.log
(6) To run for debugging with logs sent to stdout, use sudo /usr/sbin/openwsmand -d

Notes for configuring ssl with openwsman-server
===============================================
(1) Need to uncomment ssl_port 
(2) Need to create files pointed to be ssl_cert_file and ssl_key_file. 
Can either use openssl to do this (see http://www.akadia.com/services/ssh_test_certificate.html)
or use the provided /etc/openwsman/owsmangencert.sh script (have not tried this).
(3) In order for the server to listen on the ssl port need to add the -S flag to the command line.
(4) When it starts with ssl it might complain that it cannot load libssl.so. If so this might be because the 
.so file has a version number on it. Need to do:
cd /lib64
sudo ln -s libssl.so.1.0.0 libssl.so

Running the openwsman client to connect to a device via WSMan
=============================================================
wsman identify 
    -h hostname -u admin -p password -V –v --cacert=servercert.pem

wsman enumerate http://sblim.sf.net/wbem/wscim/1/cim-schema/2/Linux_ComputerSystem 
    -h hostname -u admin -p password -V -v --cacert=servercert.pem

Also need a CIM server
======================
(1) Install the sblim-sfcb, cim-schema and a provider packages (sblim-cmpi-*)
(4) Edit the /etc/sfcb/sfcb.conf file:
    (i) set the traceFile to syslog (so that can tail /var/log/messages to
see its progress). 
    (ii) comment out the enableHttp and enableHttps lines (to enable the default http connectivity
    (iii) comment out the doBasicAuth line to allow openwsman to connect in without a password.
(4) Restart the sfcbd daemon. 

**NOTE**
(1) Note sure why but...
running the sfcbd service from the service control UI may not load providers
instead type /usr/sbin/sfcbd -d from the command line.
(2) Make sure pegasus (process cimserver is not running on the same machine at the same time)

May also need to install providers see  
https://www.suse.com/documentation/sles11/book_sle_admin/data/sec_wbem_setting.html

Setting up external CIM providers
=================================
  NOTE: May require setting up openwsman vendor_namespaces. From trial and error these seem to be of the form:
  <classname prefix>=<uri prefix>
 
  The uri prefix is typically something like http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2
  (which is the form defined in http://www.dmtf.org/sites/default/files/standards/documents/DSP0227_1.0.0.pdf and
  http://www.dmtf.org/sites/default/files/standards/documents/DSP0230_1.1.0.pdf); but openwsman does not seem to
  care what form this takes.
 
  HOWEVER it does care about various things matching up:
 
  - The <classname prefix> needs to match the prefix for the class (e.g. CIM for CIM_ComputerSystem).
 
  - The resource URI needs to be of the form <uri prefix>/<classname prefix>_<rest of classname>
 
  - The namespace (which gets used as a __cimnamespace selector in the WSMan XML) needs to contain the class
  (<classname prefix>_<rest of classname>).
  
  For queries requiring the all-classes ResourceURI (these are typically WQL/CQL) 
  it looks like the vendor_namespaces setting is ignored
  and you need to use the base resource URI as http://schemas.dmtf.org/wbem/wscim/1/*

