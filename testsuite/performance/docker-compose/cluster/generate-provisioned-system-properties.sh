#!/bin/bash
cd "$(dirname "$0")"

OUT=../../provisioned-system.properties

KC_PORT="$( docker inspect --format='{{(index (index .NetworkSettings.Ports "8080/tcp") 0).HostPort}}' performance_keycloak_lb_1 )"
DB_PORT="$( docker inspect --format='{{(index (index .NetworkSettings.Ports "3306/tcp") 0).HostPort}}' performance_mariadb_1 )"

echo "# Service stack: $SERVICE_STACK" > $OUT
echo "keycloak.url=http://localhost:$KC_PORT/auth" >> $OUT
echo "db.jdbc_url=jdbc:mariadb://keycloak:keycloak@localhost:$DB_PORT/keycloak" >> $OUT
