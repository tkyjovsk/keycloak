#!/bin/bash

function run {
    echo "$1"
    eval "$1"
}

cd "$(dirname "$0")/.."

export OPERATION="${OPERATION:-provision}"
export SERVICE_STACK="${SERVICE_STACK:-singlenode}"
export SYS_PROPS=provisioned-system.properties

echo "SERVICE_STACK: $SERVICE_STACK"

case "$SERVICE_STACK" in

    singlenode)
        DOCKER_COMPOSE_FILE=docker-compose.yml
        BASE_SERVICES="mariadb"
        KEYCLOAK_SERVICES="keycloak"
        SERVICES="$BASE_SERVICES $KEYCLOAK_SERVICES"
    ;;

    cluster)
        DOCKER_COMPOSE_FILE=docker-compose-cluster.yml
        KEYCLOAK_SCALE="${KEYCLOAK_SCALE:-1}"
        echo "KEYCLOAK_SCALE: ${KEYCLOAK_SCALE}"
        BASE_SERVICES="mariadb keycloak_lb"
        KEYCLOAK_SERVICES=""
        for ((i=1; i<=$KEYCLOAK_SCALE; i++)) ; do
            KEYCLOAK_SERVICES="$KEYCLOAK_SERVICES keycloak_$i"
        done
        SERVICES="$BASE_SERVICES $KEYCLOAK_SERVICES"
    ;;

    crossdc)
        DOCKER_COMPOSE_FILE=docker-compose-crossdc.yml 

        KEYCLOAK_DC1_SCALE="${KEYCLOAK_DC1_SCALE:-1}"
        KEYCLOAK_DC2_SCALE="${KEYCLOAK_DC2_SCALE:-1}"

        echo "KEYCLOAK_DC1_SCALE: ${KEYCLOAK_DC1_SCALE}"
        echo "KEYCLOAK_DC2_SCALE: ${KEYCLOAK_DC2_SCALE}"

        BASE_SERVICES="mariadb_dc1 mariadb_dc2 infinispan_dc1 infinispan_dc2 keycloak_lb_dc1 keycloak_lb_dc2"
        KEYCLOAK_SERVICES=""
        for ((i=1; i<=$KEYCLOAK_DC1_SCALE; i++)) ; do
            KEYCLOAK_SERVICES="$KEYCLOAK_SERVICES keycloak_dc1_$i"
        done
        for ((i=1; i<=$KEYCLOAK_DC2_SCALE; i++)) ; do
            KEYCLOAK_SERVICES="$KEYCLOAK_SERVICES keycloak_dc2_$i"
        done
        SERVICES="$BASE_SERVICES $KEYCLOAK_SERVICES"
    ;;

    monitoring)
        DOCKER_COMPOSE_FILE=docker-compose-monitoring.yml 
        SERVICES=""
        DELETE_DATA="${DELETE_MONITORING_DATA:-false}"
    ;;

    *)
        echo "Service stack '$SERVICE_STACK' not supported by provisioner '$PROVISIONER'."
        exit 1
    ;;

esac


echo "OPERATION: $OPERATION"

case "$OPERATION" in

    provision)
        case "$SERVICE_STACK" in
            cluster|crossdc) docker-compose/$SERVICE_STACK/generate-docker-compose-file.sh ;;
        esac
        echo "SERVICES: $SERVICES"
        run "docker-compose -f $DOCKER_COMPOSE_FILE up -d --build $SERVICES"
        case "$SERVICE_STACK" in
            singlenode)
                KC_PORT="$( docker inspect --format='{{(index (index .NetworkSettings.Ports "8080/tcp") 0).HostPort}}' performance_keycloak_1 )"
                echo "keycloak.server.urls=http://localhost:$KC_PORT/auth" > $SYS_PROPS
            ;;
            cluster)
                KC_PORT="$( docker inspect --format='{{(index (index .NetworkSettings.Ports "8080/tcp") 0).HostPort}}' performance_keycloak_lb_1 )"
                echo "keycloak.server.urls=http://localhost:$KC_PORT/auth" > $SYS_PROPS
            ;;
            crossdc) 
                KC_DC1_PORT="$( docker inspect --format='{{(index (index .NetworkSettings.Ports "8080/tcp") 0).HostPort}}' performance_keycloak_lb_dc1_1 )"
                KC_DC2_PORT="$( docker inspect --format='{{(index (index .NetworkSettings.Ports "8080/tcp") 0).HostPort}}' performance_keycloak_lb_dc2_1 )"
                echo "keycloak.server.urls=http://localhost:$KC_DC1_PORT/auth http://localhost:$KC_DC2_PORT/auth" > $SYS_PROPS
            ;;
        esac
    ;;


    teardown)
        DELETE_DATA="${DELETE_DATA:-true}"
        echo "DELETE_DATA: $DELETE_DATA"
        if "$DELETE_DATA" ; then VOLUMES_ARG="-v"; else VOLUMES_ARG=""; fi
        run "docker-compose -f $DOCKER_COMPOSE_FILE down $VOLUMES_ARG"
        case "$SERVICE_STACK" in
            singlenode|cluster|crossdc) rm -f $SYS_PROPS ;;
        esac
    ;;


    export-dump|import-dump)

        case "$SERVICE_STACK" in
            singlenode|cluster) export DB_CONTAINER=performance_mariadb_1 ;;
            crossdc) export DB_CONTAINER=performance_mariadb_dc1_1 ;;
            *) echo "Service stack '$SERVICE_STACK' doesn't support operation '$OPERATION'." ; exit 1 ;;
        esac
        if [ -z "$DATASET" ]; then echo "Operation '$OPERATION' requires parameter DATASET."; exit 1; fi
        echo "DATASET: $DATASET"

        echo "Stopping Keycloak services: $KEYCLOAK_SERVICES"
        run "docker-compose -f $DOCKER_COMPOSE_FILE stop $KEYCLOAK_SERVICES"

        cd tests/datasets
        case "$OPERATION" in
            export-dump)
                echo "Exporting $DATASET.sql."
                if docker exec $DB_CONTAINER /usr/bin/mysqldump -u root --password=root keycloak > $DATASET.sql ; then 
                    echo "Compressing $DATASET.sql."
                    gzip $DATASET.sql
                fi
            ;;
            import-dump) 
                DUMP_DOWNLOAD_SITE=${DUMP_DOWNLOAD_SITE:-https://downloads.jboss.org/keycloak-qe}
                if [ ! -f "$DATASET.sql.gz" ]; then 
                    echo "Downloading dump file."
                    if ! curl -O $DUMP_DOWNLOAD_SITE/$DATASET.properties -O $DUMP_DOWNLOAD_SITE/$DATASET.sql.gz ; then
                        echo Download failed.
                        exit 1
                    fi
                fi
                echo "Importing $DATASET.sql.gz"
                set -o pipefail
                if ! zcat $DATASET.sql.gz | docker exec -i $DB_CONTAINER /usr/bin/mysql -u root --password=root keycloak ; then
                    echo Import failed.
                    exit 1
                fi
            ;;
        esac
        cd ../..

        echo "Starting Keycloak services: $KEYCLOAK_SERVICES"
        run "docker-compose -f $DOCKER_COMPOSE_FILE up -d $KEYCLOAK_SERVICES"
    ;;

    *)
        echo "Unsupported operation: '$OPERATION'"
        exit 1
    ;;

esac

