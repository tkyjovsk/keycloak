package org.keycloak.performance

import java.time.format.DateTimeFormatter

trait Keycloak {
  
  val dataset = TestConfig.DATASET
  
  val DATE_FMT_RFC1123 = DateTimeFormatter.RFC_1123_DATE_TIME
  
  val ACCEPT_ALL = Map("Accept" -> "*/*")
  val AUTHORIZATION = Map("Authorization" -> "Bearer ${accessToken}")
  
}