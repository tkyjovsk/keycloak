package org.keycloak.performance

//import java.util.concurrent.TimeUnit._
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.keycloak.performance.dataset._
import org.keycloak.performance.iteration._
import scala.collection.JavaConversions._

class DeleteDatasetSimulation extends Simulation with AdminCLI {
  
  setUp(
    
    scenario("Delete Dataset")
    .exec( 
      Auth.init, 
      Auth.login, 
      
      Realms.iterator(dataset.realms().iterator),
      Realms.deleteEntities, // delete the dataset by deleting whole realms
      
      Auth.logout 
    )
    .inject( adminInjectionProfile )
    
  )
  .protocols(adminCLIHttpConf)
  .assertions(global.failedRequests.count.is(0))
  
}
