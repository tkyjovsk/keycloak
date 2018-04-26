package org.keycloak.performance.dataset.idm.authorization

import io.gatling.core.session._
import io.gatling.core.session._
import org.keycloak.performance.dataset.idm.ClientFacade
import org.keycloak.performance.dataset.EntityFacade

object ResourceServerFacade {
  import ClientFacade._
  def resourceServerEndpoint(resourceServer:ResourceServer) = clientEndpoint(resourceServer.getClient) + "/authz/resource-server"
  def resourceServerEndpointName = clientEndpointName + "/authz/resource-server"
}

class ResourceServerFacade extends EntityFacade[ResourceServer] {
  import ResourceServerFacade._

  override def listEndpoint(s:Session) = null
  override def instanceEndpoint(s:Session) = resourceServerEndpoint(entity(s))

  override def listEndpointName(s:Session) = null
  override def instanceEndpointName(s:Session) = resourceServerEndpointName
  
  override def searchParameterName = SEARCH_BY_PATH
  override def searchParameterValue(s:Session) = null
  
  
  
}
