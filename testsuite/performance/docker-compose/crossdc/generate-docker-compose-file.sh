#!/bin/bash
cd "$(dirname "$0")"

KEYCLOAK_CPUSETS_DC1="${KEYCLOAK_CPUSETS_DC1:-2}"
KEYCLOAK_CPUSETS_DC2="${KEYCLOAK_CPUSETS_DC2:-3}"
OUT=../../docker-compose-crossdc.yml

echo "KEYCLOAK_CPUSETS_DC1=${KEYCLOAK_CPUSETS_DC1}"
echo "KEYCLOAK_CPUSETS_DC2=${KEYCLOAK_CPUSETS_DC2}"
echo "Generating $( basename "$OUT" )"

cat docker-compose-base.yml > $OUT
I=0
for CPUSET in $KEYCLOAK_CPUSETS_DC1 ; do
    I=$((I+1))
    sed -e s/%I%/$I/ -e s/%CPUSET%/$CPUSET/ docker-compose-keycloak_dc1.yml >> $OUT
done
I=0
for CPUSET in $KEYCLOAK_CPUSETS_DC2 ; do
    I=$((I+1))
    sed -e s/%I%/$I/ -e s/%CPUSET%/$CPUSET/ docker-compose-keycloak_dc2.yml >> $OUT
done
