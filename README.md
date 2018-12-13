# ChaMP - Version 0.9.0
[![Build Status](https://travis-ci.org/CycloneTechnology/ChaMP.svg?branch=master)](https://travis-ci.org/CycloneTechnology/ChaMP)
[![Test Coverage](https://api.codeclimate.com/v1/badges/ddf930e663192487d22c/test_coverage)](https://codeclimate.com/github/CycloneTechnology/ChaMP/test_coverage)
[![Maintainability](https://api.codeclimate.com/v1/badges/ddf930e663192487d22c/maintainability)](https://codeclimate.com/github/CycloneTechnology/ChaMP/maintainability)
[![GitHub license](https://img.shields.io/github/license/CycloneTechnology/ChaMP.svg)](https://github.com/CycloneTechnology/ChaMP/blob/master/LICENSE)


Open Source libraries for Channel Management Protocols

| Project       | Description | Release |
| ------------- | ----------- | ------ |
| champ-core    | Core utilities and common code for use by all ChaMP libraries | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-core_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-core_2.12) |
| champ-ipmi    | IPMI communications | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-ipmi_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-ipmi_2.12) |
| champ-wsman   | WS-Management communication | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-wsman_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cyclone-technology/champ-wsman_2.12) |
| champ-icmp    | ICMP communication | No Timescale Yet |
| champ-ssh     | SSH communication | No Timescale Yet |
| champ-snmp    | SNMP communication | No Timescale Yet |

# Maven

Published artifacts are available from Maven Central. The following dependency can be used to obtain them:

    <dependency>
      <groupId>com.cyclone-technology</groupId>
      <artifactId>{project}_2.12</artifactId>
      <version>{release_version}</version>
    </dependency>
