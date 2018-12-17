# Setting up Kerberos
In order to work against hosts in Windows domains, Kerberos typically needs to be configured.
(*Note* for Windows the user with which we connect to WinRM also needs to be in the ```WinRMRemoteWMIUsers__``` group or
else the domain admins group.)

To do this the following files are required:
* ```login.conf``` - to configure Java login configuration (JAAS). The location of this file should be set in the 
```java.security.auth.login.config``` system property.
* ```krb5.conf``` - to configure Kerberos realms and KDC hosts. The location of this file should be set in the 
```java.security.krb5.conf```
* (for push-based event subscriptions) a key tab file.

Also the ```javax.security.auth.useSubjectCredsOnly``` system property must be set to ```"false"```.

##Automatic configuration
A facility ```KerberosDeployer``` is provided to perform these configurations. Create a ```KerberosDeployer``` using 
the ```create``` method on its companion object. Then call ```deploy``` with a ```KerberosArtifacts``` 
that describes the contents of the various files and settings required. In simple cases, the contents of the 
files can be described in an ```application.conf``` using the ```KerberosArtifacts.simpleFromConfig``` method. 

```KerberosDeployer.deploy``` writes the required contents 
to random temporary files and setting the required system properties. It does this asynchronously and returns a 
Scala ```Future```.  

```scala
  val kerberosDeployment = KerberosDeployer.create.deploy(KerberosArtifacts.simpleFromConfig)
```  

This typically needs to be done only once at application startup.

# Other Kerberos-related notes

## Push event delivery
Note that for Microsoft WinRM to successfully connect to the web server receiving pushed events,
some configuration is required:

* Any user that we connect to a WinRM service as requires a service principal name (SPN).

  Assuming a domain user of ```WSMan``` and a host ```host.domain.com``` 
running the HTTP service listening for events
use the following command on the domain controller:
	
```
ktpass -princ HTTP/host.domain.com@DOMAIN.COM
	-mapuser WSMan@domain.com out http-web.keytab -pass password /ptype KRB5_NT_PRINCIPAL
	/crypto AES128-SHA1	/kvno 0
```

**NOTE**: Do not use the ```-pass *``` option that asks for the password. 
This will break the password of the WSMan user and prevent the WS-Management library from logging in to the 
WinRM service at all.

* Open the properties of the WSMan user and go to the Attributes Editor tab:
   * ```servicePrincipalName``` should, in this example, have a value of ```HTTP/host.domain.com```
   * ```userPrincipalName``` should have a value of ```HTTP/host.domain.com@DOMAIN.COM```

  By default for a user, the ```userPrincipalName```
is ```WSMan@domain.com``` and the ```servicePrincipalName``` is not set.
		
* Update the WSMan user properties to set 'This account supports AES 128 bit encryption' (on the Account tab)	

  **NOTE**: Use of the default encryption (RC4-HMAC) hits a 'bug' (rejected by Oracle/Sun - maybe
it's a Microsoft thing?) http://bugs.java.com/view_bug.do?bug_id=6717189
(see also https://community.oracle.com/thread/1527733)

* Need to copy the http-web.keytab and make it available for use within the WS-Management library.