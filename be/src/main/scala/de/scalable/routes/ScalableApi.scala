package de.scalable.routes

import akka.actor._
import akka.util.Timeout

import scala.concurrent.ExecutionContextExecutor

/** API Actor, entry point for every request
  *
  * @constructor create a new api for scalable with an ActorSystem
  * @param system Our ActorSystem
  */
class ScalableApi(system: ActorSystem, timeout: Timeout) extends RestRoutes {
  implicit val requestTimeout: Timeout = timeout

  implicit def executionContext: ExecutionContextExecutor = system.dispatcher

  override def createPartyActor(): ActorRef =
    system.actorOf(PartyMessages.props, PartyMessages.name)

}
