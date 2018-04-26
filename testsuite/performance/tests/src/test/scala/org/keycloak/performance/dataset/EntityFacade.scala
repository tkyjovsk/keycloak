package org.keycloak.performance.dataset

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.session._
import io.gatling.core.structure.ChainBuilder
import org.keycloak.performance.iteration._

trait EntityFacade[T <: Entity] extends EntityIteration[T] {
  import org.keycloak.performance.Admin._

  def listEndpoint(s:Session) : String
  def instanceEndpoint(s:Session) : String

  def listEndpointName(s:Session) : String
  def instanceEndpointName(s:Session) : String

  def searchParameterName : String
  def searchParameterValue(s:Session) : String
  
  // request names for Gatling Report
  def postRequestName(s:Session) = "POST " + listEndpointName(s)
  def getRequestName(s:Session) = "GET  " + listEndpointName(s)
  def putRequestName(s:Session) = "PUT  " + instanceEndpointName(s)
  def deleteRequestName(s:Session) = "DEL  " + instanceEndpointName(s)
  def searchRequestName(s:Session) = getRequestName(s) + (if (searchParameterName == SEARCH_BY_PATH) "/*" else "?" + searchParameterName + "=*")
  
  val SEARCH_BY_PATH = "_search_by_path_"
  val SEARCH_NOT_SUPPORTED = "_search_not_supported_"
  
  def void_entity_id = false // override if endpoint doesn't return entity id in Location header

  
  def httpSearchByPath = http("${searchRequestName}")
  .get("${keycloakServer}/admin/${listEndpoint}/${searchParameterValue}")
  .headers(AUTHORIZATION)
  .check(status.saveAs("status"), status.is(200), jsonPath("$..id").saveAs("entity_id"))
      
  def httpSearchByQueryParam = http("${searchRequestName}")
  .get("${keycloakServer}/admin/${listEndpoint}?")
  .queryParam(searchParameterName, "${searchParameterValue}")
  .headers(AUTHORIZATION)
  .check(status.saveAs("status"), status.is(200), jsonPath("$..id").saveAs("entity_id"))
  
  def search : ChainBuilder = search(false)
  def silentSearch : ChainBuilder = search(true)
  
  def search(silent:Boolean) = exec { s => 
//    println("search "+entity(s))
//    println("search "+listEndpoint(s))
    s.setAll(
      "searchParameterValue" -> searchParameterValue(s),
      "listEndpoint" -> listEndpoint(s),
      "searchRequestName" -> searchRequestName(s)
    )
  }.doSwitchOrElse (s => searchParameterName) ( 
    SEARCH_NOT_SUPPORTED -> exec (),
    SEARCH_BY_PATH -> doIfOrElse (s => silent) (exec(httpSearchByPath.silent)) (exec(httpSearchByPath))
  ) ( 
    doIfOrElse (s => silent) (exec(httpSearchByQueryParam.silent)) (exec(httpSearchByQueryParam)) 
  )
  .doIfOrElse(s => s("status").as[Int] == 200) (
    exec { s => 
      entity(s).setId(s("entity_id").as[String])
      s
    }
  ) (
    doIf( s => !silent ) (Sync.requestGlobalStop)
  )
  
  

  def create = exec { s =>
//    println("create "+entity(s))
//    println("create "+listEndpoint(s)+" ---> "+entity(s))
    s.setAll(
      "entityJSON" -> entity(s).toJSON,
      "listEndpoint" -> listEndpoint(s),
      "postRequestName" -> postRequestName(s)
    )
  }.exec (
    http("${postRequestName}")
    .post("${keycloakServer}/admin/${listEndpoint}")
    .headers(AUTHORIZATION)
    .body(StringBody("""${entityJSON}""")).asJSON
    .check(
      headerRegex("Location", "\\/([^\\/]+)$").optional.saveAs("entity_id"),
      status.saveAs("status"),
      status.in(201, 204, 409)
    )
  )
  .doSwitchOrElse(s => s("status").as[Int]) (
    201 -> doIfOrElse( s => void_entity_id ) ( // Location header of some endpoints returns name instead of id, force search to get id
      search
    ) (
      exec { s => 
        entity(s).setId(s("entity_id").as[String])
        s
      }
    ),
    204 -> exec(),
    409 -> search.exec(update)
  ) (
    exec { s => 
      println("Creating entity '"+entity(s)+"' failed with status: "+s("status").as[Int])
      s
    }.exec(Sync.requestGlobalStop)
  )
  
  
  def update = exec { s =>
//    println("update "+entity(s))
//    println("update "+instanceEndpoint(s))
    s.setAll(
      "instanceEndpoint" -> instanceEndpoint(s),
      "putRequestName" -> putRequestName(s)
    )
  }.exec (
    http("${putRequestName}")
    .put("${keycloakServer}/admin/${instanceEndpoint}")
    .headers(AUTHORIZATION)
    .body(StringBody("""${entityJSON}""")).asJSON
    .check(
      headerRegex("Location", "\\/([^\\/]+)$").optional.saveAs("entity_id"),
      status.saveAs("status"),
      status.is(204)
    )
  ).doIf(s => s("status").as[Int] != 204) (Sync.requestGlobalStop)
  
  
  def delete = doIf ( s => !s.contains("entity_id") ) ( silentSearch )
  .doIfOrElse ( s => s.contains("entity_id") ) ( 
    exec { s =>
      println("delete "+instanceEndpoint(s))
      s.setAll(
        "instanceEndpoint" -> instanceEndpoint(s),
        "deleteRequestName" -> deleteRequestName(s)
      )
    }.exec (
      http("${deleteRequestName}")
      .delete("${keycloakServer}/admin/${instanceEndpoint}")
      .headers(AUTHORIZATION)
      .check(status.saveAs("status"), status.is(204))
    )
    .doIf ( s => s("status").as[Int] != 204) ( Sync.requestGlobalStop )
  ) (
    exec { s =>
      println("Entity '"+entity(s)+"' not found. Considering as successful deletion.")
      s
    }
  )
  
  
  def createEntities:ChainBuilder = forEach(create)
  def deleteEntities:ChainBuilder = forEach(delete)

}
