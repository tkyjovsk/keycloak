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
if [ -z "$DATASET" ]; then
    if [ -z "$DATASET_PROPERTIES" ]; then echo "Please specify either DATASET or DATASET_PROPERTIES env variables."; exit 1; fi
    mvn gatling:execute -f $GATLING_HOME/pom.xml -Dgatling.simulationClass=keycloak.KeycloakSimulation $DATASET_PROPERTIES $@
else
    mvn gatling:execute -f $GATLING_HOME/pom.xml -Dgatling.simulationClass=keycloak.KeycloakSimulation  $DATASET_PROPERTIES -Ddataset.propertyfile=$DIRNAME/datasets/$DATASET.properties $@
fi
