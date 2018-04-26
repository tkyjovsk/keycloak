package org.keycloak.performance.dataset.idm

import scala.collection.JavaConversions._
import io.gatling.core.session._
import org.keycloak.performance.dataset.EntityFacade

object UserFacade {
  import RealmFacade._
  def getUserId(user:User) = if (user.getId == null) throw new IllegalStateException(user+".id is null") else user.getId
  def usersEndpoint(user:User) = realmEndpoint(user.getRealm) + "/users"
  def userEndpoint(user:User) = realmEndpoint(user.getRealm) + "/users/" + getUserId(user)
  def usersEndpointName = realmEndpointName + "/users"
  def userEndpointName = realmEndpointName + "/users/*"
}

class UserFacade extends EntityFacade[User] {
  import UserFacade._

  override def listEndpoint(s:Session) = usersEndpoint(entity(s))
  override def instanceEndpoint(s:Session) = userEndpoint(entity(s))

  override def listEndpointName(s:Session) = usersEndpointName
  override def instanceEndpointName(s:Session) = userEndpointName
  
  override def searchParameterName = "username"
  override def searchParameterValue(s:Session) = entity(s).getUsername
  
}
