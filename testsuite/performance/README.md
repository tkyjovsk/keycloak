# Keycloak Performance Testsuite

## Requirements:
- Maven 3.1.1+
- Keycloak server distribution installed in the local Maven repository. To do this run `mvn install -Pdistribution` from the root of the Keycloak project.
- docker 1.13+
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


## Testing

### Import Data

Usage: `mvn verify -Pimport-data[,cluster] [-Ddataset=DATASET] [-D<dataset.property>=<value>]`.

Dataset properties are loaded from `datasets/${dataset}.properties` file. Individual properties can be overriden by specifying `-D` params.

Dataset data is first generated as a .json file, and then imported into Keycloak via Admin Client REST API.

#### Examples:
- `mvn verify -Pimport-data` - import default dataset
- `mvn verify -Pimport-data -DusersPerRealm=5` - import default dataset, override the `usersPerRealm` property
- `mvn verify -Pimport-data -Ddataset=100users` - import `100users` dataset
- `mvn verify -Pimport-data -Ddataset=100realms/default` - import dataset from `datasets/100realms/default.properties`

The data can also be exported from the database, and stored locally as `datasets/${dataset}.sql.gz`
`DATASET=100users ./prepare-dump.sh`

If there is a data dump file available then -Pimport-dump can be used to import the data directly into the database, 
by-passing Keycloak server completely.

Usage: `mvn verify -Pimport-dump [-Ddataset=DATASET]`

#### Example:
- `mvn verify -Pimport-dump -Ddataset=100users` - import `datasets/100users.sql.gz` dump file created using `prepare-dump.sh`.


### Run Tests

Usage: `mvn verify -Ptest[,cluster] [-DrunUsers=N] [-DrampUpPeriod=SECONDS] [-Ddataset=DATASET] [-D<dataset.property>=<value>]`.

_*Note:* The same dataset properties which were used for data import should be supplied to the `test` phase._


## Examples

### Single-node

- Provision single node of KC + DB, import data, run test, and tear down the provisioned system:

    `mvn verify -Pprovision,import-data,test,teardown -Ddataset=100users -DrunUsers=100`

- Provision single node of KC + DB, import data, no test, no teardown:

    `mvn verify -Pprovision,import-data -Ddataset=100users`

- Run test against provisioned system using 100 concurrent users ramped up over 10 seconds, then tear it down:

    `mvn verify -Ptest,teardown -Ddataset=100users -DrunUsers=100 -DrampUpPeriod=10`

### Cluster

- Provision a 1-node KC cluster + DB, import data, run test against the provisioned system, then tear it down:

    `mvn verify -Pprovision,cluster,import-data,test,teardown -Ddataset=100users -DrunUsers=100`

- Provision a 2-node KC cluster + DB, import data, run test against the provisioned system, then tear it down:

    `mvn verify -Pprovision,cluster,import-data,test,teardown -Dkeycloak.scale=2 -DusersPerRealm=200 -DrunUsers=200`
