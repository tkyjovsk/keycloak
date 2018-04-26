package org.keycloak.performance

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.session._
import io.gatling.core.validation.Validation
import org.keycloak.performance.iteration._
import org.keycloak.performance.dataset._
import org.keycloak.performance.dataset.idm._
import AdminCLI._

object AdminCLI {
  
  val UI_HEADERS = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1")

  val ACCEPT_ALL = Map("Accept" -> "*/*")
  val AUTHORIZATION = Map("Authorization" -> "Bearer ${accessToken}")

  val DATE_FMT = DateTimeFormatter.RFC_1123_DATE_TIME

}

trait AdminCLI extends Admin {
  
  val entitiesToCreate = new SharedTimeoutDeque[Entity](5, SECONDS)
  entitiesToCreate.add(dataset)
  
  object AdminCLI {
    
    val httpConf = http
    .disableFollowRedirect
    .acceptHeader("application/json, text/plain, */*")
    
    object Auth {
    
      def init = exec(s => {
          val serverUrl = TestConfig.serverUrisIterator.next
          s.setAll(
            "keycloakServer" -> serverUrl,
            "username" -> TestConfig.authUser,
            "password" -> TestConfig.authPassword,
            "clientId" -> "admin-cli"
          )
        }).exitHereIfFailed
    
      def login = exec(
        http("Admin Login")
        .post("${keycloakServer}/realms/master/protocol/openid-connect/token")
        .headers(ACCEPT_ALL)
        .formParam("grant_type", "password")
        .formParam("username", "${username}")
        .formParam("password", "${password}")
        .formParam("client_id", "${clientId}")
        .check(status.is(200),
               jsonPath("$.access_token").saveAs("accessToken"),
               jsonPath("$.refresh_token").saveAs("refreshToken"),
               jsonPath("$.expires_in").saveAs("expiresIn"),
               header("Date").saveAs("tokenTime")))
      .exitHereIfFailed
      .exec{s => 
        s.set("accessTokenRefreshTime", ZonedDateTime.parse(s("tokenTime").as[String], DATE_FMT).toEpochSecond * 1000)
      }
    
      def needTokenRefresh(session: Session): Boolean = {
        val lastRefresh = session("accessTokenRefreshTime").as[Long]

        // 5 seconds before expiry is time to refresh
        lastRefresh + session("expiresIn").as[String].toInt * 1000 - 5000 < System.currentTimeMillis() ||
        // or if refreshTokenPeriod is set force refresh even if not necessary
        (TestConfig.refreshTokenPeriod > 0 &&
         lastRefresh + TestConfig.refreshTokenPeriod * 1000 < System.currentTimeMillis())
      }

      def refreshTokenIfExpired = doIf(s => needTokenRefresh(s)) {
        exec{s => println("Access Token Expired. Refreshing.")
             s}
        .exec(
          http("Refresh Token")
          .post("${keycloakServer}/realms/master/protocol/openid-connect/token")
          .headers(ACCEPT_ALL)
          .formParam("grant_type", "refresh_token")
          .formParam("refresh_token", "${refreshToken}")
          .formParam("client_id", "admin-cli")
          .check(status.is(200),
                 jsonPath("$.access_token").saveAs("accessToken"),
                 jsonPath("$.refresh_token").saveAs("refreshToken"),
                 jsonPath("$.expires_in").saveAs("expiresIn"),
                 header("Date").saveAs("tokenTime"))
        ).exec{s => 
          s.set("accessTokenRefreshTime", ZonedDateTime.parse(s("tokenTime").as[String], DATE_FMT).toEpochSecond * 1000)
        }
      }

      def logout = exec (refreshTokenIfExpired).exec(
        http("Admin Logout")
        .get("${keycloakServer}/realms/master/protocol/openid-connect/logout")
        .headers(UI_HEADERS)
        .check(status.is(200))
      )
    
    }
  
    object CRUD {
    
      def setEntitiesToCreate(entitiesToCreate : SharedTimeoutDeque[Entity]) = exec {s =>
        s.set("entitiesToCreate", entitiesToCreate)
      }
    
      def pollEntity = exec {s => 
        val entitiesToCreate = s("entitiesToCreate").as[SharedTimeoutDeque[Entity]]
        val entity = entitiesToCreate.pollWithSharedTimeout
        if (entity == null) {
          println("Entity queue is empty.")
          s.remove("entity")
        } else s.set("entity", entity)
      }
    
      def entityInQueue(s:Session) : Validation[Boolean] = {
        s.contains("entity")
      }
    
      def createEntities = exec(pollEntity)
      .asLongAs(s => entityInQueue(s)) {
        exec(Auth.refreshTokenIfExpired)
        .doSwitch(s => 
          s("entity").as[Entity].getClass.getSimpleName) (
          "Dataset" -> createDataset,
          "Realm" -> createRealm,
          "RealmRole" -> createRealmRole,
          "Client" -> createClient,
          "ClientRole" -> createClientRole,
          "User" -> createUser
        ).exec(pollEntity)
      }
    
      def createDataset = exec {s=>
        val entitiesToCreate = s("entitiesToCreate").as[SharedTimeoutDeque[Entity]]
        val dataset = s("entity").as[Dataset]
        entitiesToCreate.addFirstForEachReversed(dataset.getRealms)
        s
      }
    
      def createRealm = exec {s=>
        val realm = s("entity").as[Realm]
        s.setAll(
          "realmJSON" -> realm.toJSON
        )
      }.exec{
        http("POST /admin/realms")
        .post("${keycloakServer}/admin/realms")
        .headers(AUTHORIZATION)
        .body(StringBody("""${realmJSON}""")).asJSON
        .check(status.in(201, 409))
      }.exitHereIfFailed
      .exec{s=>
        val entitiesToCreate = s("entitiesToCreate").as[SharedTimeoutDeque[Entity]]
        val realm = s("entity").as[Realm]
        entitiesToCreate.addFirstForEachReversed(realm.getRealmRoles)
        entitiesToCreate.addFirstForEachReversed(realm.getClients)
        entitiesToCreate.addFirstForEachReversed(realm.getUsers)
        s
      }
    
    
      def createRealmRole = exec {s => 
        val realmRole = s("entity").as[RealmRole]
        s.setAll(
          "realm" -> realmRole.getRealm.getRealm,
          "realmRoleJSON" -> realmRole.toJSON
        )
      }.exec{
        http("POST /admin/realms/*/roles")
        .post("${keycloakServer}/admin/realms/${realm}/roles")
        .headers(AUTHORIZATION)
        .body(StringBody("""${realmRoleJSON}""")).asJSON
        .check(status.in(201, 409))//, headerRegex("Location", "\\/([^\\/]+)$").saveAs("idOfRealm"))
      }.exitHereIfFailed
    
      def readClient_id = exec {s => 
        val client = s("entity").as[Client]
        s.setAll(
          "realm" -> client.getRealm.getRealm,
          "clientId" -> client.getClientId
        )
      }.exec{
        http("GET  /admin/realms/*/clients?clientId=*")
        .get("${keycloakServer}/admin/realms/${realm}/clients")
        .queryParam("clientId", "${clientId}")
        .headers(AUTHORIZATION)
        .check(status.is(200), jsonPath("$..id").saveAs("client_id"))
      }.exitHereIfFailed
  
      def createClient = exec {s => 
        val client = s("entity").as[Client]
        s.setAll(
          "realm" -> client.getRealm.getRealm,
          "clientJSON" -> client.toJSON
        )
      }.exec{
        http("POST /admin/realms/*/clients")
        .post("${keycloakServer}/admin/realms/${realm}/clients")
        .headers(AUTHORIZATION)
        .body(StringBody("""${clientJSON}""")).asJSON
        .check(status.in(201, 409), headerRegex("Location", "\\/([^\\/]+)$").optional.saveAs("client_id"))
      }.exitHereIfFailed
      .doIf(s => !s.contains("client_id")) { readClient_id }
      .exec{s => 
        val entitiesToCreate = s("entitiesToCreate").as[SharedTimeoutDeque[Entity]]
        val client = s("entity").as[Client]
        client.setId(s("client_id").as[String])
        entitiesToCreate.addFirstForEachReversed(client.getClientRoles)
        s
      }
  
      def createClientRole = exec {s => 
        val clientRole = s("entity").as[ClientRole]
        s.setAll(
          "realm" -> clientRole.getClient.getRealm.getRealm,
          "client" -> clientRole.getClient.getId,
          "clientRoleJSON" -> clientRole.toJSON
        )
      }.exec{
        http("POST /admin/realms/*/clients/*/roles")
        .post("${keycloakServer}/admin/realms/${realm}/clients/${client}/roles")
        .headers(AUTHORIZATION)
        .body(StringBody("""${clientRoleJSON}""")).asJSON
        .check(status.in(201, 409))//, headerRegex("Location", "\\/([^\\/]+)$").saveAs("idOfRealm"))
      }.exitHereIfFailed
  
      def createUser = exec {s => 
        val user = s("entity").as[User]
        s.setAll(
          "realm" -> user.getRealm.getRealm,
          "userJSON" -> user.toJSON
        )
      }.exec{
        http("POST /admin/realms/*/users")
        .post("${keycloakServer}/admin/realms/${realm}/users")
        .headers(AUTHORIZATION)
        .body(StringBody("""${userJSON}""")).asJSON
        .check(status.in(201, 409))//, headerRegex("Location", "\\/([^\\/]+)$").saveAs("idOfRealm"))
      }.exitHereIfFailed
    
    }
    
  }
  
}
