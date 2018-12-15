# ChaMP - Version 0.9.0
[![Build Status](https://travis-ci.org/CycloneTechnology/ChaMP.svg?branch=master)](https://travis-ci.org/CycloneTechnology/ChaMP)
[![Test Coverage](https://api.codeclimate.com/v1/badges/ddf930e663192487d22c/test_coverage)](https://codeclimate.com/github/CycloneTechnology/ChaMP/test_coverage)
[![Maintainability](https://api.codeclimate.com/v1/badges/ddf930e663192487d22c/maintainability)](https://codeclimate.com/github/CycloneTechnology/ChaMP/maintainability)
[![GitHub license](https://img.shields.io/github/license/CycloneTechnology/ChaMP.svg)](https://github.com/CycloneTechnology/ChaMP/blob/master/LICENSE)


Open Source libraries for Channel Management Protocols

| Project       | Description | Release |
| ------------- | ----------- | ------ |
| champ-core    | Core utilities and common code for use by all ChaMP libraries | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-core_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-core_2.12) |
| [champ-ipmi](champ-ipmi/README.md)    | IPMI communications | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-ipmi_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-ipmi_2.12) |
| [champ-wsman](champ-wsman/README.md)   | WS-Management communication | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-wsman_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-wsman_2.12) |
| champ-snmp    | SNMP communication | No Timescale Yet |
| champ-icmp    | ICMP communication | No Timescale Yet |
| champ-ssh     | SSH communication | No Timescale Yet |

These libraries provide consistent, uncomplicated APIs for some of the most popular network management protocols.

Most API methods are command based and used in Cyclone Technology's [netPrefect](http://netPrefect.com) commercial 
network management offerings.

In order to minimise thread and memory usage when managing a large number of devices, 
IO is non-blocking and API methods return results as Scala Futures or as reactive streams (Akka Streams Sources).

Documentation with example usage is maintained individually for each protocol.
