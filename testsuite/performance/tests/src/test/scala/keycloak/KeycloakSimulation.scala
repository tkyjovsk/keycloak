package keycloak

import java.util.concurrent.atomic.AtomicInteger

import io.gatling.core.Predef._
import io.gatling.core.pause.Normal
import io.gatling.core.session._
import io.gatling.core.validation.Validation
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import org.jboss.perf.util.Util
import org.keycloak.adapters.spi.HttpFacade.Cookie
import org.keycloak.gatling.Predef._
import org.keycloak.performance.TestConfig

/**
  * @author Radim Vansa &lt;rvansa@redhat.com&gt;
  * @author Marko Strukelj &lt;mstrukel@redhat.com&gt;
  */
class KeycloakSimulation extends Simulation {

  val REALM: String = "realm_0"
  val BASE_URL: String = "/realms/" + REALM
  val LOGIN_ENDPOINT: String = BASE_URL + "/protocol/openid-connect/auth"
  val LOGOUT_ENDPOINT: String = BASE_URL + "/protocol/openid-connect/logout"



  println("Using test parameters:")
  println("  runUsers: " + TestConfig.runUsers);
  println("  rampUpPeriod: " + TestConfig.rampUpPeriod);
  println("  userThinkTime: " + TestConfig.userThinkTime)
  println("  refreshTokenPeriod: " + TestConfig.refreshTokenPeriod)
  println()
  println("Using dataset properties:\n" + TestConfig.toStringDatasetProperties)


  val httpDefault = http
    .acceptHeader("application/json")
    .disableFollowRedirect
    //.baseURL(SERVER_URI)

  // Specify defaults for http requests
  val browserConf = httpDefault
    .acceptHeader("text/html,application/xhtml+xml,application/xml")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
    .maxConnectionsPerHostLikeChrome
    .doNotTrackHeader("1")

  val adapterConf = httpDefault
    .acceptHeader("application/json")
    .disableFollowRedirect

  val users = scenario("users")
    // initialize session with host, user, client app, login failure ratio ...
    .exec(s => {
      val userInfo = TestConfig.getUsersIterator(REALM).next()
      val clientInfo = TestConfig.getConfidentialClientsIterator(REALM).next()

      s.setAll("keycloak-server" -> TestConfig.serverUrisIterator.next(),
        "state" -> Util.randomUUID(),
        "wrongPasswordCount" -> new AtomicInteger(3),
        "username" -> userInfo.username,
        "password" -> userInfo.password,
        "clientId" -> clientInfo.clientId,
        "secret" -> clientInfo.secret,
        "appUrl" -> clientInfo.appUrl
      )
    })
    .exitHereIfFailed
    .exec(browserGet("Browser to Log In Endpoint", "${keycloak-server}" + LOGIN_ENDPOINT)
      .queryParam("login", "true")
      .queryParam("response_type", "code")
      .queryParam("client_id", "${clientId}")
      .queryParam("state", "${state}")
      .queryParam("redirect_uri", "${appUrl}")
      .check(status.is(200), regex("action=\"([^\"]*)\"").saveAs("login-form-uri")))
    .exitHereIfFailed
    .pause(TestConfig.userThinkTime, Normal(TestConfig.userThinkTime * 0.2))

    .asLongAs(s => downCounterAboveZero(s, "wrongPasswordCount")) {
      exec(browserPost("Browser posts wrong credentials", "${login-form-uri}")
        .formParam("username", "${username}")
        .formParam("password", _ => Util.randomString(10))
        .formParam("login", "Log in")
        .check(status.is(200), regex("action=\"([^\"]*)\"").saveAs("login-form-uri")))
      .exitHereIfFailed
      .pause(TestConfig.userThinkTime, Normal(TestConfig.userThinkTime * 0.2))
    }

    // Successful login
    .exec(browserPost("Browser posts correct credentials", "${login-form-uri}")
      .formParam("username", "${username}")
      .formParam("password", "${password}")
      .formParam("login", "Log in")
      .check(status.is(302), header("Location").saveAs("login-redirect")))
    .exitHereIfFailed


    // Now act as client adapter - exchange code for keys
    .exec(oauth("Adapter exchanges code for tokens")
      .authorize("${login-redirect}",
        session => List(new Cookie("OAuth_Token_Request_State", session("state").as[String], 0, null, null)))
      .authServerUrl("${keycloak-server}")
      .resource("${clientId}")
      .clientCredentials("${secret}")
      .realm(REALM)
      //.realmKey(Loader.realmRepresentation.getPublicKey)
    )

    // Refresh token several times
    .pause(TestConfig.refreshTokenPeriod, Normal(TestConfig.refreshTokenPeriod * 0.2))
    .exec(oauth("Adapter refreshes token").refresh())

    // Logout
    .pause(TestConfig.refreshTokenPeriod, Normal(TestConfig.refreshTokenPeriod * 0.2))
    .exec(browserGet("Browser logout", "${keycloak-server}" + LOGOUT_ENDPOINT)
      .queryParam("redirect_uri", "${appUrl}")
      .check(status.is(302), header("Location").is("${appUrl}")))

  setUp(users.inject( {
    if (TestConfig.rampUpPeriod > 0) {
      rampUsers(TestConfig.runUsers) over TestConfig.rampUpPeriod
    } else {
      atOnceUsers(TestConfig.runUsers)
    }
  }).protocols(httpDefault))




  //
  // Function definitions
  //

  def browserGet(requestName: Expression[String], url: Expression[String]): HttpRequestBuilder = {
    addBrowserHeaders(http(requestName).get(url))
  }

  def browserPost(requestName: Expression[String], url: Expression[String]): HttpRequestBuilder = {
    addBrowserHeaders(http(requestName).post(url))
  }

  def addBrowserHeaders(request: HttpRequestBuilder): HttpRequestBuilder = {
    var req = request
    // copy headers from browserConf
    browserConf.protocol.requestPart.headers foreach {
      case (key, value) => req = req.header(key, value)
    }
    req
  }

  def downCounterAboveZero(session: Session, attrName: String): Validation[Boolean] = {
    val missCounter = session.attributes.get(attrName) match {
      case Some(result) => result.asInstanceOf[AtomicInteger]
      case None => new AtomicInteger(0)
    }
    missCounter.getAndDecrement() > 0
  }
}

/*
  val CLIENT: String = Loader.client.getClientId
  val APP_URL: String = Loader.client.getRedirectUris.stream.findFirst().get()
  val BASE_URL: String = "/auth/realms/" + Loader.realmName
  val GET_LOGIN_URL: String = BASE_URL + "/protocol/openid-connect/auth"
  val LOGOUT_URL: String = BASE_URL + "/protocol/openid-connect/logout"

  def protocolConf() = {
    http.doNotTrackHeader("1").acceptHeader("text/html").disableFollowRedirect
    //        .shareConnections
  }

  def users = scenario("users")
    .exec(s => {
      val u = Feeders.borrowUser()
      if (u == null) {
        s.markAsFailed
      } else {
        s.setAll(u()).copy(userEnd = (s => {
          Feeders.returnUser(u)
        })).set("keycloak-server", randomHost())
      }
    })
    .exitHereIfFailed
    .exec(http("user.get-login").get("http://${keycloak-server}" + GET_LOGIN_URL)
      .queryParam("login", "true").queryParam("response_type", "code").queryParam("client_id", CLIENT)
      .queryParam("state", "${state}").queryParam("redirect_uri", APP_URL)
      .check(status.is(200), regex("action=\"([^\"]*)\"").saveAs("post-login-uri")))
    .exitHereIfFailed
    .pause(Options.userResponsePeriod, Normal(Options.userResponsePeriod / 10d))
    // Unsuccessful login attempts
    .asLongAs(_ => ThreadLocalRandom.current.nextDouble() < Options.loginFailureProbability) {
    exec(http("user.post-login-wrong").post("${post-login-uri}")
      .formParam("username", "${username}")
      .formParam("password", _ => Util.randomString(Options.passwordLength)).formParam("login", "Log in")
      .check(status.is(200)))
      .exitHereIfFailed
      .pause(Options.userResponsePeriod, Normal(Options.userResponsePeriod / 10d))
  }
    // Successful login
    .exec(http("user.post-login").post("${post-login-uri}")
    .formParam("username", "${username}").formParam("password", "${password}").formParam("login", "Log in")
    .check(status.is(302), header("Location").saveAs("login-redirect")))
    .exitHereIfFailed
    // Application authorizes against Keycloak
    .exec(oauth("user.authorize").authorize("${login-redirect}",
    session => List(new Cookie("OAuth_Token_Request_State", session("state").as[String], 0, null, null)))
    .authServerUrl("http://${keycloak-server}/auth").resource(CLIENT)
    .clientCredentials(Loader.client.getSecret).realm(Loader.realmName)
    .realmKey(Loader.realmRepresentation.getPublicKey))
    // Application requests to refresh the token
    .asLongAs(_ => ThreadLocalRandom.current.nextDouble() < Options.refreshTokenProbability) {
    pause(Options.refreshTokenPeriod, Normal(Options.refreshTokenPeriod / 10d))
      .exec(oauth("user.refresh-token").refresh())
  }
    // Logout or forget & let the SSO expire
    .doIf(_ => ThreadLocalRandom.current.nextDouble() < Options.logoutProbability) {
    pause(Options.refreshTokenPeriod, Normal(Options.refreshTokenPeriod / 10d))
      .exec(http("user.logout").get("http://" + randomHost() + LOGOUT_URL).queryParam("redirect_uri", APP_URL)
        .check(status.is(302), header("Location").is(APP_URL)))
  }

  def admins(scenarioName: String) = scenario(scenarioName)
    .exec(s => {
      val connection = Loader.connection(randomHost())
      s.copy(userEnd = _ => connection.close)
        .set("keycloak", connection)
        .set("realm", connection.realm(Loader.realmName))
    })

  def adminsAdd = admins("admins-add")
    .exec(s => s.set("new-user", Util.randomString(Options.usernameLength)).set("new-password", Util.randomString(Options.passwordLength)))
    .exec(admin("admin.add-user").addUser(realm, "${new-user}").firstName("Jack").lastName("Active").password("${new-password}", false).saveAs("new-id"))
    .exec(s => {
      Feeders.addUser(s("new-user").as[String], s("new-password").as[String], s("new-id").as[String])
      s
    })

  def adminsRemove = admins("admins-remove")
    .exec(s => {
      val u = Feeders.removeUser()
      if (u == null) {
        s.markAsFailed
      } else {
        s.set("removed-user", u)
      }
    })
    .exitHereIfFailed
    .doIf(user("removed-user").map(u => u.id == null)) {
      exec(admin("admin.find-user-id").findUser(realm).username(user("removed-user").username).use((s, list) => {
        user("removed-user").id(s)(list.head.getId)
        s
      }))
    }
    .exec(admin("admin.remove-user").removeUser(realm, user("removed-user").id))

  def adminsList = admins("admins-list")
    .exec(admin("admin.find-users").findUser(realm)
      .username(_ => Util.randomString(2) + "%" )
      .use((s, list) => s)
    )

  private def user(variable: String) = new UserExpression(variable)

  def realm: Expression[RealmResource] = {
    s => s("realm").as[RealmResource]
  }

  def randomHost(): String = {
    return Options.servers(ThreadLocalRandom.current().nextInt(Options.servers.length));
  }

  def run(scenario: ScenarioBuilder, opsPerSecond: Double = Options.usersPerSecond) = scenario.inject(
    rampUsersPerSec(opsPerSecond / 10d) to opsPerSecond during Options.rampUp,
    constantUsersPerSec(opsPerSecond) during Options.duration
  ).protocols(protocolConf())

  setUp(
    run(users, Options.usersPerSecond),
    run(adminsAdd, Options.adminsPerSecond * Options.addRemoveUserProbability),
    run(adminsRemove, Options.adminsPerSecond * Options.addRemoveUserProbability),
    run(adminsList, Options.adminsPerSecond * Options.listUsersProbability)
  ).maxDuration(Options.rampUp + Options.duration + Options.rampDown)
*/
