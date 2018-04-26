package org.keycloak.performance.dataset.idm

import java.lang.UnsupportedOperationException
import io.gatling.core.Predef._
import io.gatling.core.session._
import org.keycloak.performance.dataset.EntityFacade

object RealmRoleMappingsFacade {
  import UserFacade._
  
  def roleMapperEndpoint(roleMapper:RoleMapper): String = roleMapper match {
    case u: User => userEndpoint(u)
    case g: Group => throw new UnsupportedOperationException
    case _ => throw new UnsupportedOperationException
  }
  
  def mappingsEndpoint(mappings:RealmRoleMappings) = {
//    waitForIds(mappings.getRoles) // all roles must have id to avoid 404 from mapper endpoint
    roleMapperEndpoint(mappings.getRoleMapper) + "/role-mappings/realm"
  }
  
  def roleMapperEndpointName(roleMapper:RoleMapper): String = roleMapper match {
    case u: User => userEndpointName
    case g: Group => throw new UnsupportedOperationException
    case _ => throw new UnsupportedOperationException
  }
  
  def mappingsEndpointName(mappings:RealmRoleMappings) = roleMapperEndpointName(mappings.getRoleMapper) + "/role-mappings/realm"
  
}

class RealmRoleMappingsFacade extends EntityFacade[RealmRoleMappings] {
  import RealmRoleMappingsFacade._

  override def listEndpoint(s:Session) = mappingsEndpoint(entity(s))
  override def instanceEndpoint(s:Session) = listEndpoint(s)

  override def listEndpointName(s:Session) = mappingsEndpointName(entity(s))
  override def instanceEndpointName(s:Session) = listEndpointName(s)

  override def searchParameterName = SEARCH_NOT_SUPPORTED
  override def searchParameterValue(s:Session) = ""
  
}
