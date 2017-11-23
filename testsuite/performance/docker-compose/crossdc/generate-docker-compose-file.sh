#!/bin/bash
cd "$(dirname "$0")"

KEYCLOAK_DC1_CPUSETS="${KEYCLOAK_DC1_CPUSETS:-2}"
KEYCLOAK_DC2_CPUSETS="${KEYCLOAK_DC2_CPUSETS:-3}"
OUT=../../docker-compose-crossdc.yml

echo "KEYCLOAK_DC1_CPUSETS=${KEYCLOAK_DC1_CPUSETS}"
echo "KEYCLOAK_DC2_CPUSETS=${KEYCLOAK_DC2_CPUSETS}"
echo "Generating $( basename "$OUT" )"

cat docker-compose-base.yml > $OUT
I=0
for CPUSET in $KEYCLOAK_DC1_CPUSETS ; do
    I=$((I+1))
    sed -e s/%I%/$I/ -e s/%CPUSET%/$CPUSET/ docker-compose-keycloak_dc1.yml >> $OUT
done
I=0
for CPUSET in $KEYCLOAK_DC2_CPUSETS ; do
    I=$((I+1))
    sed -e s/%I%/$I/ -e s/%CPUSET%/$CPUSET/ docker-compose-keycloak_dc2.yml >> $OUT
done
