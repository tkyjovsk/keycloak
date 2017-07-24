#!/bin/bash
DIRNAME=`dirname "$0"`
GATLING_HOME=$DIRNAME/tests

mvn -f $GATLING_HOME/pom.xml exec:java -Dexec.mainClass=org.keycloak.performance.RealmsConfigurationBuilder $@