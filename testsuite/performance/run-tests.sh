#!/bin/bash

if [ -z $SKIP_DATA_CREATION ]; then
    if [ -z $DATASET ]; then 
        if [ -z $DATASET_PROPERTIES ]; then echo Please specify either DATASET or DATASET_PROPERTIES env variables.; exit 1; fi
        mvn -f tests/pom.xml exec:java -Dexec.mainClass=org.keycloak.performance.RealmsConfigurationBuilder $DATASET_PROPERTIES
    else
        mvn -f tests/pom.xml exec:java -Dexec.mainClass=org.keycloak.performance.RealmsConfigurationBuilder $DATASET_PROPERTIES -Ddataset.propertyfile=datasets/$DATASET.properties 
    fi

    mvn compile
    mvn -f tests/pom.xml exec:java -Dexec.mainClass=org.keycloak.performance.RealmsConfigurationLoader -DnumOfWorkers=5 -Dexec.args=benchmark-realms.json > target/perf-output.txt

fi

# run gatling test here
