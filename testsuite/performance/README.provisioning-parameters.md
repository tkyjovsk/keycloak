# Keycloak Performance Testsuite - Provisioning Parameters

## Overview of Provisioned Services

### Testing

| Deployment      | Keycloak Server                          | Database           | Load Balancer      | Infinispan Server  |
|-----------------|------------------------------------------|--------------------|--------------------|--------------------|
| *Singlenode*    | 1 instance                               | 1 instance         | -                  | -                  |
| *Cluster*       | N instances                              | 1 instance         | 1                  | -                  |
| *Cross-DC*      | K instances in DC1 + L instances in DC2  | 1 instance per DC  | 1 instance per DC  | 1 instance per DC  |

### Monitoring

| Deployment      | CAdvisor    | Influx DB   | Grafana     |
|-----------------|-------------|-------------|-------------|
| *Monitoring*    | 1 instance  | 1 instance  | 1 instance  |


## Service Parameters

### Keycloak Server

| Category    | Setting                       | Property                           | Default Value                                                      |
|-------------|-------------------------------|------------------------------------|--------------------------------------------------------------------|
| Scaling     | Scale* for cluster            | `keycloak.scale`                   | `2`                                                                |
|             | Scale for DC1                 | `keycloak.dc1.scale`               | `1`                                                                |
|             | Scale for DC2                 | `keycloak.dc1.scale`               | `1`                                                                |
| Docker      | Allocated CPUs                | `keycloak.docker.cpuset`           | `2-3`                                                              |
|             | Allocated CPUs for cluster    | `keycloak.docker.cpusets`          | `2 3` (allows for a 2-node cluster, 1 CPU each)                    |
|             | Allocated CPUs for DC1        | `keycloak.dc1.docker.cpusets`      | `2`                                                                |
|             | Allocated CPUs for DC2        | `keycloak.dc2.docker.cpusets`      | `3`                                                                |
|             | Available memory              | `keycloak.docker.memlimit`         | `2500m`                                                            |
| JVM         | Memory settings               | `keycloak.jvm.memory`              | `-Xms64m -Xmx2g -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=256m`   |
| Undertow    | HTTP Listener max connections | `keycloak.http.max-connections`    | `50000`                                                            |
|             | AJP Listener max connections  | `keycloak.ajp.max-connections`     | `50000`                                                            |
| IO          | Worker IO thread pool         | `keycloak.worker.io-threads`       | `2`                                                                |
|             | Worker Task thread pool       | `keycloak.worker.task-max-threads` | `16`                                                               |
| Datasources | Connection pool min size      | `keycloak.ds.min-pool-size`        | `10`                                                               |
|             | Connection pool max size      | `keycloak.ds.max-pool-size`        | `100`                                                              |
|             | Connection pool prefill       | `keycloak.ds.pool-prefill`         | `true`                                                             |
|             | Prepared statement cache size | `keycloak.ds.ps-cache-size`        | `100`                                                              |

(*) A number of service instances. In case of Docker provisioning this value must be lower or equal to the number of provided cpusets.

### Database

| Category    | Setting                       | Property                           | Default Value                                                      |
|-------------|-------------------------------|------------------------------------|--------------------------------------------------------------------|
| Docker      | Allocated CPUs                | `db.docker.cpuset`                 | `1`                                                                |
|             | Allocated CPUs for DC1        | `db.dc1.docker.cpuset`             | `1`                                                                |
|             | Allocated CPUs for DC2        | `db.dc2.docker.cpuset`             | `1`                                                                |
|             | Available memory              | `db.docker.memlimit`               | `2g`                                                               |

### Load Balancer

| Category    | Setting                       | Property                     | Default Value                                                      |
|-------------|-------------------------------|------------------------------|--------------------------------------------------------------------|
| Docker      | Allocated CPUs                | `lb.docker.cpuset`           | `1`                                                                |
|             | Allocated CPUs for DC1        | `lb.dc1.docker.cpuset`       | `1`                                                                |
|             | Allocated CPUs for DC2        | `lb.dc2.docker.cpuset`       | `1`                                                                |
|             | Available memory              | `lb.docker.memlimit`         | `1g`                                                               |
| JVM         | Memory settings               | `lb.jvm.memory`              | `-Xms64m -Xmx512m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=256m` |
| Undertow    | HTTP Listener max connections | `lb.http.max-connections`    | `50000`                                                            |
| IO          | Worker IO thread pool         | `lb.worker.io-threads`       | `2`                                                                |
|             | Worker Task thread pool       | `lb.worker.task-max-threads` | `16`                                                               |

### Infinispan Server

| Category    | Setting                       | Property                       | Default Value                                                                             |
|-------------|-------------------------------|--------------------------------|-------------------------------------------------------------------------------------------|
| Docker      | Allocated CPUs                | `infinispan.docker.cpuset`     | `1`                                                                                       |
|             | Allocated CPUs for DC1        | `infinispan.dc1.docker.cpuset` | `1`                                                                                       |
|             | Allocated CPUs for DC2        | `infinispan.dc2.docker.cpuset` | `1`                                                                                       |
|             | Available memory              | `infinispan.docker.memlimit`   | `1g`                                                                                      |
| JVM         | Memory settings               | `infinispan.jvm.memory`        | `-Xms64m -Xmx512m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=256m -XX:+DisableExplicitGC` |


### Monitoring

| Category    | Setting                       | Property                   | Default Value   |
|-------------|-------------------------------|----------------------------|-----------------|
| Docker      | Allocated CPUs                | `monitoring.docker.cpuset` | `0`             |


## Note on Docker settings

By default, there are 4 CPU cores allocated: core 0 for monitoring, core 1 for database (MariaDB), and cores 2 and 3 for Keycloak server.
Default memory limits for database and Keycloak server are 2g. The `cpuset` and `memlimit` parameters set here are set to `cpuset` and
`mem_limit` parameters of docker-compose configuration. See docker-compose documentation for meaning of the values. How to set the parameters
correctly depends on number of factors - number of cpu cores, NUMA, available memory etc., hence it is out of scope of this document.

### Example CPU Settings for a "Fat Box" Docker Host

#### Cluster

| Setting                            | Example Value       |
|------------------------------------|---------------------|
| `keycloak.docker.cpusets`          | 4-5 6-7 8-9 ...     |
| `db.docker.cpuset`                 | 1                   |
| `lb.docker.cpuset`                 | 2                   |
| `infinispan.docker.cpuset`         | 3                   |                                                                                      |
| `monitoring.docker.cpuset`         | 0                   |

#### Cross-DC

| Setting                            | Example Value       |
|------------------------------------|---------------------|
| `keycloak.dc1.docker.cpusets`      | 7-8 9-10 ... 19-20  |
| `keycloak.dc1.docker.cpusets`      | 21-22 23-24 ...     |
| `db.dc1.docker.cpuset`             | 1                   |
| `db.dc2.docker.cpuset`             | 2                   |
| `lb.dc1.docker.cpuset`             | 3                   |
| `lb.dc2.docker.cpuset`             | 4                   |
| `infinispan.dc1.docker.cpuset`     | 5                   |                                                                                      |
| `infinispan.dc2.docker.cpuset`     | 6                   |                                                                                      |
| `monitoring.docker.cpuset`         | 0                   |
