package org.keycloak.performance.dataset.idm

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.session._
import org.keycloak.performance.dataset.EntityFacade

object RealmFacade {
  def getRealm(realm:Realm) = if (realm.getRealm == null) throw new IllegalStateException(realm + ".realm is null") else realm.getRealm
  def realmsEndpoint(realm:Realm) = "realms"
  def realmEndpoint(realm:Realm) =  "realms/" + getRealm(realm)
  def realmsEndpointName = "realms"
  def realmEndpointName = "realms/*"
}

class RealmFacade extends EntityFacade[Realm] {
  import RealmFacade._
  
  override def listEndpoint(s:Session) = realmsEndpoint(entity(s))
  override def instanceEndpoint(s:Session) = realmEndpoint(entity(s))
  
  override def listEndpointName(s:Session) = realmsEndpointName
  override def instanceEndpointName(s:Session) = realmEndpointName
  
  def searchParameterName = SEARCH_BY_PATH
  def searchParameterValue(s:Session) = entity(s).getRealm
  
}
