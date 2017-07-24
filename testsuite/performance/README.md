# Keycloak Performance Testsuite

## Provisioning

### Docker

#### Requirements:
- Keycloak server distribution installed in the local Maven repository. To do this run `mvn install -Pdistribution` from the root of the Keycloak project.
- Maven 3.1.1+
- docker 1.12+
- docker-compose 1.14+

#### Provision

Usage: `provision.sh [singlenode|cluster|crossdc]`. Defaults to `singlenode` if no parameter is provided.

The script blocks until the whole system is up and running.

Following information is provided in exported variables:
- `KEYCLOAK_SERVER_URIS` - space-separated list of Keycloak server URIs
- `DB_URL` - JDBC URL for accessing the DB

More details about provisioning: [README-provisioning](README-provisioning.md)

#### Teardown

`teardown.sh [singlenode|cluster|crossdc]`. Defaults to `singlenode` if no parameter is provided.

## Tests

For the impatient - here is an example test run from start to finish:

````
# build Keycloak distribution
cd $KEYCLOAK_PROJECT_ROOT
mvn clean install -DskipTests -Pdistribution

cd testsuite/performance

# make sure your docker daemon is up and running
. ./provision.sh

# check the running servers
docker ps | grep "CONTAINER ID\|keycloak"

# make sure we have the Keycloak Server URIs
echo "KEYCLOAK_SERVER_URIS: $KEYCLOAK_SERVER_URIS"

# specify dataset parameters
export "DATASET_PROPERTIES=-DnumOfRealms=1 -DusersPerRealm=10 -DclientsPerRealm=5 -DrealmRoles=5 -DrealmRolesPerUser=2 -DclientRolesPerUser=2 -DclientRolesPerClient=2"

# generate and upload test data and execute KeycloakSimulation test with 200 concurrent users
./run-tests.sh -DrunUsers=200
````

If you want to repeat the test using the existing test data use:
````
SKIP_DATA_CREATION=1 ./run-tests.sh -DrunUsers=200
````

### Data creation & import



### Gatling Testsuite
