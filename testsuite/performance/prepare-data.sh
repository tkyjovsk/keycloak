#!/bin/bash
DIRNAME=`dirname "$0"`
GATLING_HOME=$DIRNAME/tests

if [ -z "$DATASET" ]; then
    if [ -z "$DATASET_PROPERTIES" ]; then echo "Please specify either DATASET or DATASET_PROPERTIES env variables."; exit 1; fi
    echo "Generating benchmark-realms.json using properties: [$DATASET_PROPERTIES]"
    ./generate-data.sh $DATASET_PROPERTIES
else
    echo "Generating benchmark-realms.json using DATASET [$DATASET] and override properties: [$DATASET_PROPERTIES]"
    ./generate-data.sh $DATASET_PROPERTIES -Ddataset.propertyfile=$DIRNAME/datasets/$DATASET.properties
fi

if [ -z "$SKIPLOAD" ]; then
    echo "Loading benchmark-realms.json into Keycloak Server (log file: $GATLING_HOME/target/load-data-output.txt)"
    ./load-data.sh -DnumOfWorkers=1 -Dexec.args=benchmark-realms.json > $GATLING_HOME/target/load-data-output.txt
else
    echo "Skipped loading the data."
fi