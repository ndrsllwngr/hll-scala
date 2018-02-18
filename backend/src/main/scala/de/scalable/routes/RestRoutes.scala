package de.scalable.routes

import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.model.HttpMethods.{GET, HEAD, OPTIONS, POST, PUT, DELETE}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{HttpOrigin, HttpOriginRange}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import org.slf4j.{Logger, LoggerFactory}
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings

import scala.collection.immutable.Seq

trait RestRoutes extends PartyRoute {

  override val log: Logger = LoggerFactory.getLogger("RestRoutes")

  def routes: Route = {
    import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
    import Directives._


    // Your CORS settings
    val corsSettings = CorsSettings.defaultSettings.copy(
      allowedOrigins = HttpOriginRange.*,
      allowedMethods = Seq(GET, POST, HEAD, OPTIONS, PUT, DELETE)

    )

    // Your rejection handler
    val rejectionHandler = corsRejectionHandler withFallback RejectionHandler.default

    // Your exception handler
    val exceptionHandler = ExceptionHandler {
      case e: NoSuchElementException => complete(StatusCodes.NotFound -> e.getMessage)
    }

    // Combining the two handlers only for convenience
    val handleErrors = handleRejections(rejectionHandler) & handleExceptions(exceptionHandler)
    handleErrors{
    cors(corsSettings) {
      handleErrors{
      healthCheck ~
        partyRoute //~
    }}}
  }

  def healthCheck: Route =
    pathPrefix("health") {
      pathEndOrSingleSlash {
        get {
          complete {
            "Server is Healthy!"
          }
        }
      }
    }
}
