#!/bin/bash
if [ -z $M2_HOME ]; then mvn=mvn; else mvn=$M2_HOME/bin/mvn; fi

# Sets and exports variable KEYCLOAK_VERSION based on Maven \${project.version}

echo Evaluating Maven \${project.version}:
export KEYCLOAK_VERSION=$($mvn help:evaluate -B -Dexpression=project.version | grep -v "^\[")
echo Exported: KEYCLOAK_VERSION=$KEYCLOAK_VERSION
