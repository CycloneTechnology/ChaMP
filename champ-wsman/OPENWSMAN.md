Miscellaneous notes related to testing with openwsman on Linux.

**WARNING**: Some of these are experimental findings about what did and did not work and therefore 
should be used at your own risk.

##Installing openwsman-server
This assumes openSUSE Linux but variations on these instructions should work with other Linux flavours.

1. Install openwsman-server using the package manager (e.g. yast).
2. To create passwords for basic auth use htpasswd (htpasswd2 doesnt seem to work for it)
which can be found in the thttpd package.
3. To configure basic auth edit /etc/openwsman/openwsman.conf:
   * uncomment the ```basic_password_file``` line   
   * comment out the ```basic_authenticator_```* lines (else server won't start - 'ambiguous' authentication error)   
   * comment out the ssl and port settings in the [cim] section (to allow simpler
http access to the downstream CIM provider, sfcb)
4. Logs for openwsman server when run as a service are in ```/var/log/wsmand.log```
5. To run for debugging with logs sent to stdout, use ```sudo /usr/sbin/openwsmand -d```

## Configuring ssl with openwsman-server
1. Need to uncomment ```ssl_port``` 
2. Need to create files pointed to be ```ssl_cert_file``` and ```ssl_key_file```. 
Can either use openssl to do this (see http://www.akadia.com/services/ssh_test_certificate.html)
or use the provided ```/etc/openwsman/owsmangencert.sh``` script.
3. In order for the server to listen on the ssl port need to add the ```-S``` flag to the command line.
4. When it starts with ssl it might complain that it cannot load libssl.so. If so this might be because the 
.so file has a version number on it. Need to do:
```
cd /lib64
sudo ln -s libssl.so.1.0.0 libssl.so
```

## Running the openwsman client to connect to a device via WSMan
```
wsman identify -h hostname -u admin -p password -V –v --cacert=servercert.pem
```

```
wsman enumerate http://sblim.sf.net/wbem/wscim/1/cim-schema/2/Linux_ComputerSystem 
   -h hostname -u admin -p password -V -v --cacert=servercert.pem
```

### Also need a CIM server
1. Install the sblim-sfcb, cim-schema and a provider packages (sblim-cmpi-*)
2. Edit the ```/etc/sfcb/sfcb.conf``` file:
   * set the ```traceFile``` to ```syslog``` (so that can ```tail /var/log/messages``` to
see its progress). 
   * comment out the ```enableHttp``` and ```enableHttps``` lines (to enable the default http connectivity
   * comment out the ```doBasicAuth``` line to allow openwsman to connect in without a password.
3. Restart the sfcbd daemon. 

**NOTE**
* Note sure why but...
running the sfcbd service from the service control UI may not load providers
instead type ```/usr/sbin/sfcbd -d``` from the command line.
* Make sure Pegasus (process cimserver) is not running on the same machine at the same time.

May also need to install providers see  
https://www.suse.com/documentation/sles11/book_sle_admin/data/sec_wbem_setting.html

####Setting up external CIM providers

*NOTE*: May require setting up openwsman vendor_namespaces. From trial and error these seem to be of the form:
```classname prefix```=```uri prefix```
 
The uri prefix is typically something like http://schemas.dmtf.org/wbem/wscim/1/cim-schema/2
(which is the form defined in http://www.dmtf.org/sites/default/files/standards/documents/DSP0227_1.0.0.pdf and
http://www.dmtf.org/sites/default/files/standards/documents/DSP0230_1.1.0.pdf); but openwsman does not seem to
care what form this takes.
 
*HOWEVER* it does care about various things matching up: 
* The ```classname prefix``` needs to match the prefix for the class (e.g. CIM for CIM_ComputerSystem).
* The resource URI needs to be of the form ```uri prefix```/```classname prefix```_```rest of classname```
* The namespace (which gets used as a __cimnamespace selector in the WSMan XML) needs to contain the class
(```classname prefix```_```rest of classname```).
  
For queries requiring the all-classes ResourceURI (these are typically WQL/CQL) 
it looks like the vendor_namespaces setting is ignored
and you need to use the base resource URI as http://schemas.dmtf.org/wbem/wscim/1/*

