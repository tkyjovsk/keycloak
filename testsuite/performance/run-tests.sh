#!/bin/bash
DIRNAME=`dirname "$0"`
GATLING_HOME=$DIRNAME/tests

if [ -z "$SKIP_DATA_CREATION" ]; then
    . ./prepare-data.sh
fi

#CLASSNAME="$1"
#if [ "x$CLASSNAME" == "x" ]; then
#  echo "CLASSNAME argument not specified. Using 'keycloak.KeycloakSimulation'"
#  echo "(Usage: $0 [CLASSNAME])"
#  CLASSNAME="keycloak.KeycloakSimulation"
#fi

# run gatling test here
export GATLING_HOME
mvn gatling:execute -f $GATLING_HOME/pom.xml -Dgatling.simulationClass=keycloak.KeycloakSimulation $@