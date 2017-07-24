package keycloak

import io.gatling.core.Predef._
import io.gatling.http.Predef._

/**
  * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
  */
class SimpleExample1 extends Simulation {

  // Create a scenario with three steps:
  //   - first perform an HTTP GET
  //   - then pause for 10 seconds
  //   - then perform a different HTTP GET

  val scn = scenario("Simple")
    .exec(http("Home")
      .get("http://localhost:8080")
      .check(status is 200))
    .pause(10)
    .exec(http("Auth Home")
      .get("http://localhost:8080/auth")
      .check(status is 200))

  // Run the scenario with 100 parallel users, all starting at the same time
  setUp(scn.inject(atOnceUsers(100)))













  /*

  // step 2

    val scn = scenario("Simple")
      .exec(http("Home")
        .get("http://localhost:8080")
        .check(status is 200))
      .pause(10)
      .exec(http("Auth Home")
        .get("http://localhost:8080/auth")
        .check(status is 200))











  // step 3

      val scn2 = scenario("Account")
        .exec(http("Account")
          .get("http://localhost:8080/auth/realms/master/account")
          .check(status is 200))
        .pause(1)

    setUp(
      scn.inject(atOnceUsers(100)),
      scn2.inject(atOnceUsers(50))
    )














  // step 4

    val session2 = repeat(10, "i") {
      exec(http("Account ${i}")
        .get("http://localhost:8080/auth/realms/master/account")
        .check(status is 200))
        .pause(1)
    }

    val scn2 = scenario("Account")
      .exec(session2)




















  // step 5
  val httpConf = http
    .baseURL("http://computer-database.gatling.io") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")


  setUp(
      scn.inject(atOnceUsers(100)).protocols(httpConf),
      scn2.inject(atOnceUsers(50)).protocols(httpConf)
  )
  */
}
