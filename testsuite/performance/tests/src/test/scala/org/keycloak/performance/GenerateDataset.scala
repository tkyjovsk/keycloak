package org.keycloak.performance

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation

class GenerateDataset extends Simulation with AdminCLI {

  val createDatasetScenario = scenario("Generate Dataset")
  .exec(
    AdminCLI.Auth.init,
    AdminCLI.Auth.login,
    AdminCLI.CRUD.setEntitiesToCreate(entitiesToCreate),
    AdminCLI.CRUD.createEntities,
    AdminCLI.Auth.logout
  )

  setUp(createDatasetScenario.inject(Admin.injectionProfile))
  .protocols(AdminCLI.httpConf)
  
}
