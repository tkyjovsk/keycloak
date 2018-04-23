package keycloak

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import keycloak.OIDCScenarioBuilder._

import org.keycloak.performance.TestConfig


class OIDCEmailRegistrationSimulation extends CommonSimulation {

  override def printSpecificTestParameters {
    println("  refreshTokenCount: " + TestConfig.refreshTokenCount)
    println("  badLoginAttempts: " + TestConfig.badLoginAttempts)
  }

  val usersScenario = scenario("Registering Users").exec(registerViaEmailScenario.chainBuilder)

  setUp(usersScenario.inject(defaultInjectionProfile).protocols(httpDefault))
  
  after {
    TestConfig.summitRegistrationUsersIterator.updateNumberOfRegisteredUsers
  }

}
