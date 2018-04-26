package org.keycloak.performance

import io.gatling.core.Predef._

trait Admin extends OIDC {
  
  object Admin {
    val injectionProfile = atOnceUsers(TestConfig.numOfWorkers)
  }
  
}
