package org.keycloak.gatling

import java.text.SimpleDateFormat
import java.util.{Collections, Date}

import akka.actor.ActorDSL.actor
import akka.actor.ActorRef
import io.gatling.core.action.Interruptable
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.Protocols
import io.gatling.core.result.writer.DataWriterClient
import io.gatling.core.session._
import io.gatling.core.validation._
import org.jboss.logging.Logger
import org.keycloak.adapters.KeycloakDeploymentBuilder
import org.keycloak.adapters.spi.AuthOutcome
import org.keycloak.adapters.spi.HttpFacade.Cookie
import org.keycloak.common.enums.SslRequired
import org.keycloak.representations.adapters.config.AdapterConfig

import scala.collection.JavaConverters._

case class AuthorizeAttributes(
  requestName: Expression[String],
  uri: Expression[String],
  cookies: Expression[List[Cookie]],
  sslRequired: SslRequired = SslRequired.EXTERNAL,
  resource: Option[Expression[String]] = None,
  password: Option[Expression[String]] = None,
  realm: Option[String] = None,
  realmKey: Option[String] = None,
  authServerUrl: Expression[String] = _ => Failure("no server url")
) {
  def toAdapterConfig(session: Session) = {
    val adapterConfig = new AdapterConfig
    adapterConfig.setSslRequired(sslRequired.toString)
    adapterConfig.setResource( resource match {
        case Some(expr) => expr(session).get
        case None => null
    })
    adapterConfig.setCredentials( password match {
      case Some(expr) => Collections.singletonMap("secret", expr(session).get)
      case None => null
    })
    adapterConfig.setRealm(realm match {
      case Some(name) => name
      case None => null
    })
    adapterConfig.setRealmKey(realmKey match {
      case Some(key) => key
      case None => null
    })
    adapterConfig.setAuthServerUrl(authServerUrl(session).get)
    adapterConfig
  }
}

class AuthorizeActionBuilder(attributes: AuthorizeAttributes) extends ActionBuilder {
  def newInstance(attributes: AuthorizeAttributes) = new AuthorizeActionBuilder(attributes)

  def sslRequired(sslRequired: SslRequired) = newInstance(attributes.copy(sslRequired = sslRequired))
  def resource(resource: Expression[String]) = newInstance(attributes.copy(resource = Option(resource)))
  def clientCredentials(password: Expression[String]) = newInstance(attributes.copy(password = Option(password)))
  def realm(realm: String) = newInstance(attributes.copy(realm = Option(realm)))
  def realmKey(realmKey: String) = newInstance(attributes.copy(realmKey = Option(realmKey)))
  def authServerUrl(authServerUrl: Expression[String]) = newInstance(attributes.copy(authServerUrl = authServerUrl))

  override def build(next: ActorRef, protocols: Protocols): ActorRef = {
    actor(actorName("authorize"))(new AuthorizeAction(attributes, next))
  }
}

object AuthorizeAction {
  val logger = Logger.getLogger(classOf[AuthorizeAction])
}

class AuthorizeAction(
                       attributes: AuthorizeAttributes,
                       val next: ActorRef
                     ) extends Interruptable with ExitOnFailure with DataWriterClient {
  override def executeOrFail(session: Session): Validation[_] = {
    val facade = new MockHttpFacade()
    val deployment = KeycloakDeploymentBuilder.build(attributes.toAdapterConfig(session));
    facade.request.setURI(attributes.uri(session).get);
    facade.request.setCookies(attributes.cookies(session).get.map(c => (c.getName, c)).toMap.asJava)
    var nextSession = session
    val requestAuth: MockRequestAuthenticator = session(MockRequestAuthenticator.KEY).asOption[MockRequestAuthenticator] match {
      case Some(ra) => ra
      case None =>
        val tmp = new MockRequestAuthenticator(facade, deployment, new MockTokenStore, -1, session.userId)
        nextSession = session.set(MockRequestAuthenticator.KEY, tmp)
        tmp
    }

    Blocking(() => {
      AuthorizeAction.logger.debugf("%s: Authenticating %s%n", new SimpleDateFormat("HH:mm:ss,SSS").format(new Date()).asInstanceOf[Any], session("username").as[Any], Unit)
      Stopwatch(() => requestAuth.authenticate())
        .check(result => result == AuthOutcome.AUTHENTICATED, result => {
          AuthorizeAction.logger.warnf("%s: Failed auth %s%n", new SimpleDateFormat("HH:mm:ss,SSS").format(new Date()).asInstanceOf[Any], session("username").as[Any], Unit)
          result.toString
        })
        .recordAndContinue(AuthorizeAction.this, nextSession, attributes.requestName(session).get)
    })
  }
}

