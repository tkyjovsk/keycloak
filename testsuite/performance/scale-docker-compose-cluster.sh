./generate-docker-compose-cluster.sh

KEYCLOAK_SCALE="${KEYCLOAK_SCALE:-1}"
SERVICES="mariadb keycloak_lb "
for ((i=1; i<=$KEYCLOAK_SCALE; i++)) ; do
    SERVICES="$SERVICES keycloak_$i"
done

echo "KEYCLOAK_SCALE=${KEYCLOAK_SCALE}"
echo "Starting services from docker-compose-cluster.yml: $SERVICES"

docker-compose -f docker-compose-cluster.yml up -d --build $SERVICES