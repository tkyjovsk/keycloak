package org.keycloak.performance.dataset.idm.authorization

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.session._
import org.keycloak.performance.dataset.EntityFacade

object ResourceFacade {
  import ResourceServerFacade._
  def resourcesEndpoint(resource:Resource) = resourceServerEndpoint(resource.getResourceServer) + "/resource"
  def resourceEndpoint(resource:Resource) = resourceServerEndpoint(resource.getResourceServer) + "/resource/" + resource.getId
  def resourcesEndpointName = resourceServerEndpointName + "/resource"
  def resourceEndpointName = resourceServerEndpointName + "/resource/*"
}

class ResourceFacade extends EntityFacade[Resource] {
  import ResourceFacade._

  override def listEndpoint(s:Session) = resourcesEndpoint(entity(s))
  override def instanceEndpoint(s:Session) = resourceEndpoint(entity(s))

  override def listEndpointName(s:Session) = resourcesEndpointName
  override def instanceEndpointName(s:Session) = resourceEndpointName
  
  override def searchParameterName = "name"
  override def searchParameterValue(s:Session) = entity(s).getName

  override def httpSearchByPath = super.httpSearchByPath.check(jsonPath("$.._id").saveAs("entity_id"))
  override def httpSearchByQueryParam = super.httpSearchByQueryParam.check(jsonPath("$.._id").saveAs("entity_id"))
  override def httpPost = super.httpPost.check(jsonPath("$._id").optional.saveAs("entity_id"))
  
//  override def create = exec { s =>
//    println("creating: "+entity(s))
//    println("JSON: "+entity(s).toJSON)
//    s
//  }.exec(super.create)
  
}
