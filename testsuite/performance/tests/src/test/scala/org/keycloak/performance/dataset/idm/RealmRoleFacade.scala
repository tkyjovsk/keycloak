package org.keycloak.performance.dataset.idm

import io.gatling.core.session._
import io.gatling.core.session._
import org.keycloak.performance.dataset.EntityFacade

object RealmRoleFacade {
  import RealmFacade._
  def getRealmRoleId(realmRole:RealmRole) = if (realmRole.getId==null) throw new IllegalStateException(realmRole+".id is null") else realmRole.getId
  def realmRolesEndpoint(realmRole:RealmRole) = realmEndpoint(realmRole.getRealm) + "/roles"
  def realmRoleEndpoint(realmRole:RealmRole) = realmEndpoint(realmRole.getRealm) + "/roles-by-id/" + getRealmRoleId(realmRole)
  def realmRolesEndpointName = realmEndpointName + "/roles"
  def realmRoleEndpointName = realmEndpointName + "/roles-by-id/*"
}

class RealmRoleFacade extends EntityFacade[RealmRole] {
  import RealmRoleFacade._

  override def listEndpoint(s:Session) = realmRolesEndpoint(entity(s))
  override def instanceEndpoint(s:Session) = realmRoleEndpoint(entity(s))

  override def listEndpointName(s:Session) = realmRolesEndpointName
  override def instanceEndpointName(s:Session) = realmRoleEndpointName

  override def searchParameterName = SEARCH_BY_PATH
  override def searchParameterValue(s:Session) = entity(s).getName
  
  override def void_entity_id = true
  
}
