package org.keycloak.performance.dataset.idm.authorization

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.session._
import org.keycloak.performance.dataset.EntityFacade

object PolicyFacade {
  import ResourceServerFacade._
  def policiesEndpoint(policy:Policy) = resourceServerEndpoint(policy.getResourceServer) + "/policy/" + policy.getType
  def policiesEndpointName = resourceServerEndpointName + "/policy"
}

abstract class PolicyFacade[P <: Policy] extends EntityFacade[P] {
  import PolicyFacade._

  override def searchParameterName = "name"
  override def searchParameterValue(s:Session) = entity(s).getName

  override def httpPost = super.httpPost.check(jsonPath("$.id").optional.saveAs("entity_id"))
  
}
