package org.keycloak.performance.dataset.idm.authorization

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.session._
import org.keycloak.performance.dataset.EntityFacade

object ScopeFacade {
  import ResourceServerFacade._
  def scopesEndpoint(scope:Scope) = resourceServerEndpoint(scope.getResourceServer) + "/scope"
  def scopeEndpoint(scope:Scope) = resourceServerEndpoint(scope.getResourceServer) + "/scope/" + scope.getId
  def scopesEndpointName = resourceServerEndpointName + "/scope"
  def scopeEndpointName = resourceServerEndpointName + "/scope/*"
}

class ScopeFacade extends EntityFacade[Scope] {
  import ScopeFacade._

  override def listEndpoint(s:Session) = scopesEndpoint(entity(s))
  override def instanceEndpoint(s:Session) = scopeEndpoint(entity(s))

  override def listEndpointName(s:Session) = scopesEndpointName
  override def instanceEndpointName(s:Session) = scopeEndpointName
  
  override def searchParameterName = "name"
  override def searchParameterValue(s:Session) = entity(s).getName

  override def httpPost = super.httpPost.check(jsonPath("$.id").optional.saveAs("entity_id"))
  
}
