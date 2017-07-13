# Keycloak Performance Testsuite - Provisioning

## Prerequisities

### Keycloak Server Distribution

Building `keycloak/Dockerfile` requires unpacked Keycloak Server Distribution within `keycloak/target/keycloak` folder.
For automated unpacking run `mvn process-resources`.

(This step requires `keycloak-server-dist-${project.version}.zip` artifact to be installed in local Maven repository.
To install it run `mvn install -DskipTests -Pdistribution` from the project root.)


## Docker

### Requirements:
- docker 1.12+
- docker-compose 1.14+

### Deployments

#### Singlenode Deployment
- Build / rebuild: `docker-compose build`
- Start services: `docker-compose up -d`
- Stop services: `docker-compose down -v`. If you wish to keep the container volumes skip the `-v` option.

#### Keycloak Cluster Deployment
- Build / rebuild: `docker-compose -f docker-compose-cluster.yml build`
- Start services: `docker-compose -f docker-compose-cluster.yml up -d`
- Scaling KC nodes: `docker-compose -f docker-compose-cluster.yml up -d --scale keycloak=2`
- Stop services: `docker-compose -f docker-compose-cluster.yml down -v`. If you wish to keep the container volumes skip the `-v` option.

#### Cross-DC Deployment
- Build / rebuild: `docker-compose -f docker-compose-crossdc.yml build`
- Start services: `docker-compose -f docker-compose-crossdc.yml up -d`
- Scaling KC nodes: `docker-compose -f docker-compose-crossdc.yml up -d --scale keycloak_dc1=2 --scale keycloak_dc2=3`
- Stop services: `docker-compose -f docker-compose-crossdc.yml down -v`. If you wish to keep the container volumes skip the `-v` option.

### Debugging docker containers:
- List started containers: `docker ps`. It's useful to watch continuously: `watch docker ps`.
  To list compose-only containers use: `docker-compose ps`, but this doesn't show container health status.
- Watch logs of a specific container: `docker logs -f crossdc_mariadb_dc1_1`.
- Watch logs of all containers managed by docker-compose: `docker-compose logs -f`.
- List networks: `docker network ls`
- Inspect network: `docker network inspect NETWORK_NAME`. Shows network configuration and currently connected containers.

### Network Addresses
#### KC
`10.i.1.0/24` One network per DC. For single-DC deployments `i = 0`, for cross-DC deployment `i ∈ ℕ` is an index of particular DC.
#### Load Balancing
`10.0.2.0/24` Network spans all DCs.
#### DB Replication
`10.0.3.0/24` Network spans all DCs.
#### ISPN Replication
`10.0.4.0/24` Network spans all DCs.
