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
echo "Starting services from docker-compose-crossdc.yml: $SERVICES"

docker-compose -f docker-compose-crossdc.yml up -d --build $SERVICES
