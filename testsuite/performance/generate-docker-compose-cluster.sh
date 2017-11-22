KEYCLOAK_CPUSETS="${KEYCLOAK_CPUSETS:-2-3}"
OUT=docker-compose-cluster.yml

echo "KEYCLOAK_CPUSETS=${KEYCLOAK_CPUSETS}"
echo "Generating $OUT"

cat scaling/cluster/docker-compose-base.yml > $OUT
I=0
for CPUSET in $KEYCLOAK_CPUSETS ; do
    I=$((I+1))
    sed -e s/%I%/$I/ -e s/%CPUSET%/$CPUSET/ scaling/cluster/docker-compose-keycloak.yml >> $OUT
done
