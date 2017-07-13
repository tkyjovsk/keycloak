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

### Data creation & import

### Gatling Testsuite
