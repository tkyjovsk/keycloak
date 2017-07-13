#!/bin/bash

case "$1" in

    ""|singlenode) 
        DOCKER_COMPOSE_FILE=docker-compose.yml
        export KEYCLOAK_SERVER_URIS=http://localhost:8080/auth
        ;;

    cluster)
        DOCKER_COMPOSE_FILE=docker-compose-cluster.yml
        if [ -z $KEYCLOAK_SCALE ]; then KEYCLOAK_SCALE=1; fi
        DOCKER_COMPOSE_PARAMS="--scale keycloak=$KEYCLOAK_SCALE"
        export KEYCLOAK_SERVER_URIS=http://localhost:8080/auth
        ;;
    
    crossdc)
        echo Cross-DC setup not fully supported yet; exit 1
        DOCKER_COMPOSE_FILE=docker-compose-crossdc.yml
        if [ -z $KEYCLOAK_DC1_SCALE ]; then KEYCLOAK_DC1_SCALE=1; fi
        if [ -z $KEYCLOAK_DC2_SCALE ]; then KEYCLOAK_DC2_SCALE=1; fi
        DOCKER_COMPOSE_PARAMS="--scale keycloak_dc1=$KEYCLOAK_DC1_SCALE --scale keycloak_dc2=$KEYCLOAK_DC2_SCALE"
        export KEYCLOAK_SERVER_URIS="http://localhost:8081/auth http://localhost:8082/auth"
        ;;
    
    *) echo "Invalid parameter. Usage: provision.sh [singlenode|cluster|crossdc]"; exit 1

esac

export DB_URL=jdbc:mariadb://keycloak:keycloak@localhost:3306/keycloak


function isKeycloakHealthy {
    echo Checking instance $1
    CODE=`curl -s -o /dev/null -w "%{http_code}" $1/realms/master`
    [ "$CODE" -eq "200" ]
}

function waitUntilSystemHealthy {
    echo Waiting until all Keycloak instances are healthy...
    if [ -z $1 ]; then ITERATIONS=30; else ITERATIONS=$1; fi
    C=0
    SYSTEM_HEALTHY=false
    while ! $SYSTEM_HEALTHY; do 
        C=$((C+1))
        if [ $C -gt $ITERATIONS ]; then
            echo System healthcheck failed.
            exit 1
        fi
        sleep 2
        SYSTEM_HEALTHY=true
        for URI in $KEYCLOAK_SERVER_URIS ; do
            if ! isKeycloakHealthy $URI; then 
                echo Instance is not healthy.
                SYSTEM_HEALTHY=false
            fi
        done
    done
    echo System is healthy.
}


# unpack latest keycloak-server-dist
if [ -z $M2_HOME ]; then mvn=mvn; else mvn=$M2_HOME/bin/mvn; fi
$mvn -v
$mvn process-resources

# set KEYCLOAK_VERSION based on version in pom.xml
. ./evaluate-project-version.sh

# build images & start containers/services
DOCKER_COMPOSE_COMMAND="docker-compose -f $DOCKER_COMPOSE_FILE up -d --build $DOCKER_COMPOSE_PARAMS"
echo "Executing $DOCKER_COMPOSE_COMMAND"
$DOCKER_COMPOSE_COMMAND

waitUntilSystemHealthy
