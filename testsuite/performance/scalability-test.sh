#!/bin/bash

# provisioning parameters
CORES_START=1
CORES_INCREMENT=1
STEPS=2

# stress testing parameters
TEST_USERS_START=100
TEST_USERS_INCREMENT=20
TEST_MAX_STEPS=5

echo
echo Running Keycloak scalability test.

for (( i=0; i < $STEPS; i++)); do

    STEP=$(( i + 1))
    CORES=$(( CORES_START + CORES_INCREMENT * i ))
    CPUSET="2-$(( 1 + CORES ))"
    
    PROVISION_COMMAND="mvn verify -Pcluster,provision -Dkeycloak.scale=$STEP -Dkeycloak.docker.cpuset=$CPUSET"

    echo
    echo Scale: $STEP, Keycloak service cores: $CORES, CPUSET: $CPUSET
    echo
    echo "   $PROVISION_COMMAND"
    echo

#    eval "$PROVISION_COMMAND"

    for (( j=0; j < $TEST_MAX_STEPS; j++)); do

        TEST_USERS=$(( TEST_USERS_START + TEST_USERS_INCREMENT * j ))

        TEST_COMMAND="mvn verify -Ptest -DrunUsers=$TEST_USERS $@"

        echo
        echo "   $TEST_COMMAND"
        echo

#        eval "$TEST_COMMAND"
        
    done

done

#mvn verify -Pcluster,teardown
