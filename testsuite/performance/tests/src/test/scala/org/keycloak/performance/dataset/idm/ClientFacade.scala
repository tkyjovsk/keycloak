package org.keycloak.performance.dataset.idm

import io.gatling.core.session._
import io.gatling.core.session._
import org.keycloak.performance.dataset.EntityFacade

object ClientFacade {
  import RealmFacade._
  def getClientId(client:Client) = if (client.getId == null) throw new IllegalStateException(client+".id is null") else client.getId
  def clientsEndpoint(client:Client) = realmEndpoint(client.getRealm) + "/clients"
  def clientEndpoint(client:Client) = realmEndpoint(client.getRealm) + "/clients" + "/" + getClientId(client)
  def clientsEndpointName = realmEndpointName + "/clients"
  def clientEndpointName = realmEndpointName + "/clients/*"
}

class ClientFacade extends EntityFacade[Client] {
  import ClientFacade._

  override def listEndpoint(s:Session) = clientsEndpoint(entity(s))
  override def instanceEndpoint(s:Session) = clientEndpoint(entity(s))

  override def listEndpointName(s:Session) = clientsEndpointName
  override def instanceEndpointName(s:Session) = clientEndpointName
  
  override def searchParameterName = "clientId"
  override def searchParameterValue(s:Session) = entity(s).getClientId
  
}
