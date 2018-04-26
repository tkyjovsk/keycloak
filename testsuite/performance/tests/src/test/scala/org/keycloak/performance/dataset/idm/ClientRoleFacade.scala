package org.keycloak.performance.dataset.idm

import io.gatling.core.session._
import io.gatling.core.session._
import org.keycloak.performance.dataset.EntityFacade

object ClientRoleFacade {
  import ClientFacade._
  import RealmFacade._
  def getClientRoleId(clientRole:ClientRole) = if (clientRole.getId==null) throw new IllegalStateException(clientRole+".id is null") else clientRole.getId
  def clientRolesEndpoint(clientRole:ClientRole) = clientEndpoint(clientRole.getClient) + "/roles"
  def clientRoleEndpoint(clientRole:ClientRole) = realmEndpoint(clientRole.getClient.getRealm) + "/roles-by-id/" + getClientRoleId(clientRole)
  def clientRolesEndpointName = clientEndpointName + "/roles"
  def clientRoleEndpointName = realmEndpointName + "/roles-by-id/*"
}

class ClientRoleFacade extends EntityFacade[ClientRole] {
  import ClientRoleFacade._

  override def listEndpoint(s:Session) = clientRolesEndpoint(entity(s))
  override def instanceEndpoint(s:Session) = clientRoleEndpoint(entity(s))

  override def listEndpointName(s:Session) = clientRolesEndpointName
  override def instanceEndpointName(s:Session) = clientRoleEndpointName
  
  override def searchParameterName = SEARCH_BY_PATH
  override def searchParameterValue(s:Session) = entity(s).getName
  
  override def void_entity_id = true
  
}
