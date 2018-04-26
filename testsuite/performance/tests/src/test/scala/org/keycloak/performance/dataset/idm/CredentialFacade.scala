package org.keycloak.performance.dataset.idm

import scala.collection.JavaConversions._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.session._
import org.keycloak.performance.dataset.EntityFacade

object CredentialFacade {
  import UserFacade._
  def credentialsEndpoint(credential:Credential) = userEndpoint(credential.getUser) + "/reset-password"
  def credentialsEndpointName = userEndpointName + "/reset-password"
}

class CredentialFacade extends EntityFacade[Credential] {
  import CredentialFacade._
  import org.keycloak.performance.Admin._

  override def listEndpoint(s:Session) = credentialsEndpoint(entity(s))
  override def instanceEndpoint(s:Session) = null

  override def listEndpointName(s:Session) = credentialsEndpointName
  override def instanceEndpointName(s:Session) = null
  
  override def searchParameterName = SEARCH_NOT_SUPPORTED
  override def searchParameterValue(s:Session) = null
  
  override def create = exec { s =>
//    println("reset password "+listEndpoint(s))
    s.setAll(
      "entityJSON" -> entity(s).toJSON,
      "listEndpoint" -> listEndpoint(s),
      "putRequestName" -> ("PUT  " + listEndpointName(s))
    )
  }.exec(http("${putRequestName}")
    .put("${keycloakServer}/admin/${listEndpoint}")
    .headers(AUTHORIZATION)
    .body(StringBody("""${entityJSON}""")).asJSON
    .check(
      status.saveAs("status"),
      status.is(204)
    )
  )
  .doIf(s => s("status").as[Int] != 204) (Sync.requestGlobalStop)
  
}
