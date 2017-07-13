#!/bin/bash

case "$1" in

    ""|singlenode) 
        DOCKER_COMPOSE_FILE=docker-compose.yml
        ;;

    cluster)
        DOCKER_COMPOSE_FILE=docker-compose-cluster.yml
        ;;
    
    crossdc)
        DOCKER_COMPOSE_FILE=docker-compose-crossdc.yml
        ;;
    
    *) echo "Invalid parameter. Usage: teardown.sh [singlenode|cluster|crossdc]"; exit 1

esac


docker-compose -f $DOCKER_COMPOSE_FILE down -v

unset KEYCLOAK_SERVER_URIS DB_URL
