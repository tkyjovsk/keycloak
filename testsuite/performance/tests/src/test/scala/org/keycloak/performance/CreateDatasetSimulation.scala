package org.keycloak.performance

import java.util.concurrent.TimeUnit._
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.keycloak.performance.dataset._
import org.keycloak.performance.iteration._
import scala.collection.JavaConversions._

class CreateDatasetSimulation extends Simulation  with AdminCLI  {
  import Admin._
  
  setUp(
    
    scenario("Create Dataset")
    .exec( 
      Auth.init, 
      Auth.login, 
      
      Realms.iterator(dataset.realms().iterator),
      Realms.createEntities,
      Sync.waitForOthers("realms"), // block until all users are finished creating realms
      
      RealmRoles.iterator(dataset.realmRoles().iterator),
      RealmRoles.createEntities,
      Sync.waitForOthers("realm roles"),
      
      Clients.iterator(dataset.clients().iterator),
      Clients.createEntities, 
      Sync.waitForOthers("clients"),
      
      ClientRoles.iterator(dataset.clientRoles().iterator),
      ClientRoles.createEntities, 
      Sync.waitForOthers("client roles"),
      
      Users.iterator(dataset.users().iterator),
      Users.createEntities, 
      Sync.waitForOthers("users"),
      
      Credentials.iterator(dataset.credentials().iterator),
      Credentials.createEntities, 
      Sync.waitForOthers("credentials"),
      
      RealmRoleMappings.iterator(dataset.userRealmRoleMappings().iterator),
      RealmRoleMappings.createEntities, 
      Sync.waitForOthers("realm role mappings"),
      
      ClientRoleMappings.iterator(dataset.userClientRoleMappings().iterator),
      ClientRoleMappings.createEntities, 
      
      Auth.logout 
    )
    .inject(adminInjectionProfile)
    
  )
  .protocols(adminCLIHttpConf)
  .assertions(global.failedRequests.count.is(0))
  
}
