package de.scalable

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import de.scalable.routes.{PartyApi, ScalableApi}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App with RequestTimeout {
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher

  val config = ConfigFactory.load("application.conf")
  val host = config.getString("scalable.http.host")
  val port = config.getInt("scalable.http.port")

  val timeoutSeconds: Int = config.getInt("scalable.akka.http.server.request-timeout")
  val api = new ScalableApi(system, requestTimeout(timeoutSeconds)).routes

  implicit val materializer = ActorMaterializer()
  val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, host, port)

  val log = Logging(system.eventStream, "scalable")
  bindingFuture onComplete {
    case Success(serverBinding) =>
      log.info(s"Scalable API bound to ${serverBinding.localAddress}")
    case Failure(ex) =>
      log.error(ex, "Failed to bind to {}:{}", host, port)
      system.terminate()
  }
}

trait RequestTimeout {

  import scala.concurrent.duration._

  def requestTimeout(timeout: Int): Timeout = FiniteDuration(timeout, TimeUnit.SECONDS)

}
