package keycloak

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import keycloak.CommonScenarioBuilder._
import keycloak.BasicOIDCScenarioBuilder._

import org.keycloak.performance.TestConfig._


/**
  * @author Radim Vansa &lt;rvansa@redhat.com&gt;
  * @author Marko Strukelj &lt;mstrukel@redhat.com&gt;
  */
class BasicOIDCSimulation extends CommonSimulation {

  override def printSpecificTestParameters {
    println("  refreshTokenCount: " + refreshTokenCount)
    println("  badLoginAttempts: " + badLoginAttempts)
  }

  val httpDefault = http
    .acceptHeader("application/json")
    .disableFollowRedirect
    .inferHtmlResources

  val userSession = new BasicOIDCScenarioBuilder()

      .browserOpensLoginPage()

      .thinkPause()
      .browserPostsWrongCredentials()
      .browserPostsCorrectCredentials()

      // Act as client adapter - exchange code for keys
      .adapterExchangesCodeForTokens()

      .refreshTokenSeveralTimes()

      .thinkPause()
      .logout()

      .thinkPause()


  val usersScenario = scenario("users").exec(userSession.chainBuilder)

  setUp(usersScenario.inject(

      rampUsersPerSec(0.00001) to usersPerSec during(rampUpPeriod),

      constantUsersPerSec(usersPerSec) during(warmUpPeriod),

      rampUsersPerSec(usersPerSec) to spikePeakUsersPerSec during(spikeRampUpPeriod),
      rampUsersPerSec(spikePeakUsersPerSec) to usersPerSec during(spikeRampDownPeriod),

      constantUsersPerSec(usersPerSec) during(measurementPeriod) 

    ).protocols(httpDefault))
  
}
