# Keycloak Performance Testsuite Datasets

## Generating Datasets via Keycloak Java Admin Client

### Generating a set of datasets for multiple realms

The first dataset is small and is created quickly. Building of each subsequent dataset continues on top
of the previous dataset.

Datasets are created with a specific released server version (rather than a snapshot) in order to be
usable with later releases - newer server version should be able to migrate schema from any previous release.

We use 10 concurrent threads, which is enough to saturate a 
dual core machine. For quad-core you can try to double the number of workers.

```
cd testsuite/performance

mvn clean install -Dserver.version=4.0.0.Beta1

mvn verify -Pteardown
mvn verify -Pprovision
mvn verify -Pgenerate-data -Ddataset=10r100u1c -DnumOfWorkers=10
mvn verify -Pexport-dump -Ddataset=10r100u1c

mvn verify -Pgenerate-data -Ddataset=20r100u1c -DstartAtRealmIdx=10 -DnumOfWorkers=10
mvn verify -Pexport-dump -Ddataset=20r100u1c

mvn verify -Pgenerate-data -Ddataset=50r100u1c -DstartAtRealmIdx=20 -DnumOfWorkers=10
mvn verify -Pexport-dump -Ddataset=50r100u1c

mvn verify -Pgenerate-data -Ddataset=200r100u1c -DstartAtRealmIdx=50 -DnumOfWorkers=10
mvn verify -Pexport-dump -Ddataset=200r100u1c

mvn verify -Pgenerate-data -Ddataset=500r100u1c -DstartAtRealmIdx=200 -DnumOfWorkers=10
mvn verify -Pexport-dump -Ddataset=500r100u1c
```

If the dataset dump file is not available locally but it's known that the dataset for specific version exists on the server
it can be retrieved by specifying a proper server version again. For example:
```
mvn verify -Pteardown
mvn clean install
mvn verify -Pprovision
mvn verify -Pimport-dump -Ddataset=20r100u1c -Dserver.version=4.0.0.Beta1

```



## Generating Datasets via Gatling simulation

There is another approach to generating datasets which simulaneously allows to measure performance of admin CRUD operations,
and is more easily extensible in terms of adding more entity types and attributes.

### Dataset Model

The dataset model is a layer of abstraction on top of the standard Keycloak model
which allows to work with numbered lists of entities and their nested sub-entities,
and generate their attributes with FreeMarker templates.

Parameters of the model are split in to two separate parts: size definition and templating parameters.
This way different sizes can be combined with different templating options.

#### Sizing Parameters

| Property | Default Value |
| --- | --- | 
| `realms` | `2` |
| `clientsPerRealm` |`2` |
| `usersPerRealm` | `2` |
| `realmRolesPerRealm` | `2` |
| `clientRolesPerClient` | `2` |
| `attributesPerUser` | `2` |
| `realmRolesPerUser` | `2` |
| `clientRolesPerUser` | `2` |

#### Templating

Templating works based on an "index" of a particular entity inside its "parent" entity.
At the root of the hierarchy there is an abstract entity "Dataset" which contains the realms.

First attribute which is set for each entity is the `index`. 
Remaining attributes are then processed one-by-one in a specific order (currently determined by entity classes).
Each subsequent template can work with all attributes that have already been processed,
and it can also access attributes of the parent entity.

```
# REALM
realm.realm=realm_${index}
realm.displayName=Realm ${index}
realm.enabled=true
realm.registrationAllowed=true
realm.accessTokenLifeSpan=60
realm.passwordPolicy=hashIterations(27500)

# USER
user.username=user_${index}_of_${realm.realm}
user.enabled=true
user.password=password_of_${username}
user.email=${username}@email.test
user.emailVerified=true
user.firstName=User_${index}
user.lastName=O'Realm_${realm.index}
```

##### Additional methods

It is possible to use any FreeMarker templating functions.

Additionally each "indexed" entity has a `hashcode()` and `equals()` methods based on its class, index 
and its parent's hashcode/equals methods, providing a unique identification within the dataset.

Each nested entity additionally offers methods for generating pseudo-random numbers 
based on its `index` within its parent entity:
- `int indexBasedRandomInt(int bound)`
- `boolean indexBasedRandomBool(int truePercentage)`
- `boolean indexBasedRandomBool() { returns indexedBasedRandomBool(50); }`

For example using the following template:
```
user.enabled=${indexBasedRandomBool(70)?c}
```
will produce ~70 % of users with `enabled=true`.
The pseudo-random sequence is unique for each parent entity instance + nested entity class type.


#### Supported entities

| Entity | Attribute | 
| --- | --- | 
| Realm | `realm`, `displayName`, `enabled`, `registrationAllowed`, `accessTokenLifeSpan`, `passwordPolicy` | 
| Realm Role | `name`, `description` |
| Client | `clientId`, `name`, `description`, `rootUrl`, `adminUrl`, `baseUrl`, `enabled`, `secret`, `redirectUris`, `webOrigins`, `authorizationServicesEnabled`, `serviceAccountsEnabled` |
| Client Role | `name`, `description` |
| User | `username`, `enabled`, `password`, `email`, `emailVerified`, `firstName`, `lastName` |
| Credential | Based on `user.password`. |
| User Realm Role Mappings | Randomly selected realm roles based on random seed of `user.hashcode()`. |
| User Client Role Mappings | Randomly selected client roles of all clients in realm, based on random seed of `user.hashcode()` |


#### Creating Dataset

Assuming the Keycloak server has been provisioned the dataset can be generated by running the `org.keycloak.performance.CreateDatasetSimulation`.

`mvn verify -P test -D test.properties=create-dataset [-DnumOfWorkers=<N>] [-Ddataset.size=<DATASET_SIZE>] [-Ddataset.templating=<DATASET_TEMPLATING>]`

The `dataset.size` parameter can be any name from within `src/test/resources/dataset/size` path. The `.properties` suffix will be added automatically.
As an alternative it is possible to override the parameter by specifying an exact file name via `-Ddataset.size.properties.file=<FULL_FILE_NAME>`.

The `dataset.templating` parameter can be any name from within `src/test/resources/dataset/templating` path. The `.properties` suffix will be added automatically.
As an alternative it is possible to override the parameter by specifying an exact file name via `-Ddataset.templating.properties.file=<FULL_FILE_NAME>`.

The entities will be created in phases based on their types. The ordering is determined dependencies between these types.
For example User's Client Role Mappings can be added only after all required users, clients and client roles have already been created.

If particular entity already exists in the database it will be updated based on the used templating.

It is possible to speed up the process by increasing the number of admin users with parameter `numOfWorkers`.
However, increasing the number too much can cause problems at the server side in some cases (e.g. creating many realms in parallel).
Recommended number is up to 5 workers.

#### Deleting Dataset

Deleting dataset is done via running the `org.keycloak.performance.DeleteDatasetSimulation`.

`mvn verify -P test -D test.properties=delete-dataset <same-parameters-as-create-dataset>`

The dataset is deleted by deleting individual realms. Deleting by deeper-nested entities is not supported at the moment.
