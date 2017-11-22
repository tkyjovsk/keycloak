TOPOLOGY="${TOPOLOGY:-singlenode}"

case "$TOPOLOGY" in

    singlenode)
        DOCKER_COMPOSE_FILE=docker-compose.yml
        SERVICES=""
    ;;

    cluster)
        DOCKER_COMPOSE_FILE=docker-compose-cluster.yml
        ./generate-docker-compose-cluster.sh

        KEYCLOAK_SCALE="${KEYCLOAK_SCALE:-1}"
        SERVICES="mariadb keycloak_lb "
        for ((i=1; i<=$KEYCLOAK_SCALE; i++)) ; do
            SERVICES="$SERVICES keycloak_$i"
        done
        echo "KEYCLOAK_SCALE=${KEYCLOAK_SCALE}"
    ;;

    crossdc)
        DOCKER_COMPOSE_FILE=docker-compose-crossdc.yml 
        ./generate-docker-compose-crossdc.sh

        KEYCLOAK_DC1_SCALE="${KEYCLOAK_DC1_SCALE:-1}"
        KEYCLOAK_DC2_SCALE="${KEYCLOAK_DC2_SCALE:-1}"
        SERVICES="mariadb_dc1 mariadb_dc2 infinispan_dc1 infinispan_dc2 keycloak_lb_dc1 keycloak_lb_dc2 "
        for ((i=1; i<=$KEYCLOAK_DC1_SCALE; i++)) ; do
            SERVICES="$SERVICES keycloak_dc1_$i"
        done
        for ((i=1; i<=$KEYCLOAK_DC2_SCALE; i++)) ; do
            SERVICES="$SERVICES keycloak_dc2_$i"
        done
        echo "KEYCLOAK_DC1_SCALE=${KEYCLOAK_DC1_SCALE}"
        echo "KEYCLOAK_DC2_SCALE=${KEYCLOAK_DC2_SCALE}"
    ;;

    *)
        echo "Provisioner $PROVISIONER doesn't support topology: $TOPOLOGY."
        exit 1
    ;;

esac

DOCKER_COMPOSE_COMMAND="docker-compose -f $DOCKER_COMPOSE_FILE up -d --build $SERVICES"

echo "$DOCKER_COMPOSE_COMMAND"
eval "$DOCKER_COMPOSE_COMMAND"
