# Keycloak Performance Testsuite

## Requirements:
- Maven 3.1.1+
- Keycloak server distribution installed in the local Maven repository. To do this run `mvn install -Pdistribution` from the root of the Keycloak project.
- docker 1.12+
- docker-compose 1.14+


## Provisioning

### Provision

Usage: `mvn verify -Pprovision[,cluster]`.

- Single node deployment: `mvn verify -Pprovision`
- Cluster deployment: `mvn verify -Pprovision,cluster [-Dkeycloak.scale=N]`. Default `N=1`.

### Teardown

Usage: `mvn verify -Pteardown[,cluster]`

- Single node deployment: `mvn verify -Pteardown`
- Cluster deployment: `mvn verify -Pteardown,cluster`

Provisioning/teardown is performed via `docker-compose` tool. More details in [README-provisioning](README-provisioning.md)


## Import Data

Usage: `mvn verify -Pimport-data[,cluster] -Ddataset=... -Dparam1=... -Dparam2=...`.


## Run Tests

Usage: `mvn verify -Ptest[,cluster] [-DrunUsers=N]`.



## Examples

### Single-node

- Provision single node of KC + DB, import data, run test, and tear down the provisioned system:

    `mvn verify -Pprovision,import-data,test,teardown -DrunUsers=200`

- All the above profiles are active by default so it's enough to run:

    `mvn verify -DrunUsers=200`

    _Note: When you specify `mvn -P` parameter all "activeByDefault" profiles are automatically deactivated by Maven. 
    In that case it's necessary to specify all profiles you want to activate._

- Provision single node of KC + DB, import data, no test, no teardown:

    `mvn verify -Pprovision,import-data`

- Run test against provisioned system, then tear it down:

    `mvn verify -Ptest,teardown`

### Cluster

- Provision a 1-node KC cluster + DB, import data, run test against the provisioned system, then tear it down:

    `mvn verify -Pprovision,cluster,import-data,test,teardown`

- Provision a 2-node KC cluster + DB, import data, run test against the provisioned system, then tear it down:

    `mvn verify -Pprovision,cluster,import-data,test,teardown -Dkeycloak.scale=2`

