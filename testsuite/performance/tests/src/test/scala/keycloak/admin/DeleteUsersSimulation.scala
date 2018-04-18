package keycloak.admin

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.keycloak.performance.TestConfig
import keycloak.AdminConsoleScenarioBuilder._

/**
  * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
  */
class DeleteUsersSimulation extends keycloak.CommonSimulation {

  val httpProtocol = http
    .disableFollowRedirect
    .acceptHeader("application/json, text/plain, */*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:54.0) Gecko/20100101 Firefox/54.0")

  val adminSession = new keycloak.AdminConsoleScenarioBuilder()
    .login
    .deleteUsers
    .logout

  val deleteUserScenario = scenario("Delete User")
    .exec(adminSession.chainBuilder)

  setUp(deleteUserScenario.inject(adminsInjectionProfile).protocols(httpProtocol))

  after {
    TestConfig.summitDeleteUsersIterator.updateNumberOfRegisteredUsers
  }

}
