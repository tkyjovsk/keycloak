package org.keycloak.performance.dataset

import io.gatling.core.session._
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

trait EntityIteration[T <: Entity] {
  import org.keycloak.performance.AdminCLI._
  import org.keycloak.performance.Admin._
  
  def iterator(iterator:Iterator[T]) = exec { s =>
    s.set("iterator", iterator)
  }
  
  def iterator(s:Session) = {
    if (s.contains("iterator")) s("iterator").as[Iterator[T]] 
    else throw new IllegalStateException("Entity iterator not initialized.")
  }
  
  def iterate = exec { s =>
    try {
      s.set("entity", iterator(s).next)
    } catch {
      case nse: NoSuchElementException => 
        println("Entity iterator is empty.")
        s.remove("entity")
    }
  }
  
  def entity(s:Session) : T = s("entity").as[T]
  def entityNotNull(s:Session) = {
    val c = s.contains("entity")
    c
  }

  def forEach(iteratedAction : ChainBuilder) = iterate
  .asLongAs(s => entityNotNull(s)) ( 
    Sync.checkGlobalStopRequest
    .exec { s =>
      s.remove("status")
      s.remove("entity_id")
    }
    .exec (
      Auth.refreshTokenIfExpired,
      iteratedAction,
      iterate
    ) 
  )
  
}
