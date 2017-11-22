#!/bin/bash
cd "$(dirname "$0")"

KEYCLOAK_CPUSETS="${KEYCLOAK_CPUSETS:-2-3}"
OUT=../../docker-compose-cluster.yml

echo "KEYCLOAK_CPUSETS=${KEYCLOAK_CPUSETS}"
echo "Generating $( basename "$OUT" )"

cat docker-compose-base.yml > $OUT
I=0
for CPUSET in $KEYCLOAK_CPUSETS ; do
    I=$((I+1))
    sed -e s/%I%/$I/ -e s/%CPUSET%/$CPUSET/ docker-compose-keycloak.yml >> $OUT
done
