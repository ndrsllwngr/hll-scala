

package de.scalable.routes

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives.{pathPrefix, _}
import akka.http.scaladsl.server.Route
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import de.scalable.json.ModelJsonSupport
import de.scalable.model._
import org.slf4j.{Logger, LoggerFactory}
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait PartyRoute extends PartyApi with ModelJsonSupport {

  val log: Logger = LoggerFactory.getLogger("PartyRoute")

  val token: String = ConfigFactory.load().getString("scalable.token")

  def partyRoute: Route =
    pathPrefix("echo") {
      path(Remaining) { stringToEcho =>
        get {
          onComplete(echo(stringToEcho)) {
            case Success(echoReturn) => complete((echoReturn))
            case Failure(e) =>
              e.printStackTrace()
              complete((InternalServerError, e.toString))

          }
        }
      }
    } ~
  pathPrefix("party") {
    pathEndOrSingleSlash {
      put {
        entity(as[String]) { partyName =>
          onComplete(createParty(partyName)) {
            case Success(result) => complete(result.toJson)
            case Failure(e) =>
              e.printStackTrace()
              complete((InternalServerError, e.toString))
          }
        }
      }
    }~
    pathPrefix("song") {
      post {
        entity(as[SongPlayStateUpdate]) { songPlayStateUpdate =>
          onComplete(updateSongPlayState(songPlayStateUpdate.id,songPlayStateUpdate.partyID,songPlayStateUpdate.playState)) {
            case Success(result) => complete(s"Song ${songPlayStateUpdate.id} for party ${songPlayStateUpdate.partyID} was set to ${songPlayStateUpdate.playState}")
            case Failure(e) =>
              e.printStackTrace()
              complete((InternalServerError, e.toString))

          }
        }
      }~
        delete {
          entity(as[SongToDelete]) { songToDelete =>
            onComplete(deleteSong(songToDelete.id,songToDelete.partyID)) {
              case Success(result) => complete(s"Song ${songToDelete.id} for party ${songToDelete.partyID} deleted")
              case Failure(e) =>
                e.printStackTrace()
                complete((InternalServerError, e.toString))

            }
          }
        }~
      path(Remaining) { partyKey =>
        put {
          entity(as[SongToAdd]) { songToAdd =>
            onComplete(addSong(songToAdd, partyKey)) {
              case Success(result) => complete(result.toJson)
              case Failure(e) =>
                e.printStackTrace()
                complete((InternalServerError, e.toString))
            }
          }
        } ~
        get {
          onComplete(getSongsForParty(partyKey)) {
            case Success(result) => complete(result)
            case Failure(e) =>
              e.printStackTrace()
              complete((InternalServerError, e.toString))

          }
        }
      }
    } ~
    pathPrefix("photo") {
        pathEndOrSingleSlash{
          delete {
            entity(as[PhotoToDelete]) { photoToDelete =>
              onComplete(deletePhoto(photoToDelete.id,photoToDelete.partyID)) {
                case Success(result) => complete(s"Photo ${photoToDelete.id} for party ${photoToDelete.partyID} deleted")
                case Failure(e) =>
                  e.printStackTrace()
                  complete((InternalServerError, e.toString))

              }
            }
          }
        } ~
        path(Remaining) { partyKey =>
          put {
            entity(as[PhotoToAdd]) { photoToAdd =>
              onComplete(addPhoto(photoToAdd, partyKey)) {
                case Success(result) => complete(result.toJson)
                case Failure(e) =>
                  e.printStackTrace()
                  complete((InternalServerError, e.toString))
              }
            }
          } ~
          get {
            onComplete(getPhotosForParty(partyKey)) {
              case Success(result) => complete(result)
              case Failure(e) =>
                e.printStackTrace()
                complete((InternalServerError, e.toString))

            }
          }
        }
    }~ pathPrefix("login") {
      pathEndOrSingleSlash{
        post {
          entity(as[PartyLoginRequest]) { request =>
            onComplete(loginToParty(request.id,request.password)) {
              case Success(result) => complete(result.toJson)
              case Failure(e) =>
                e.printStackTrace()
                complete((InternalServerError, e.toString))
            }
          }
        }
      }
    }~
      path(Remaining) { partyKey =>
        get {
          onComplete(checkIfPartyExists(partyKey)) {
            case Success(result) => complete(result.toJson)
            case Failure(e) =>
              e.printStackTrace()
              complete((InternalServerError, e.toString))
          }
        }
      }
  } ~ pathPrefix("vote") {
      pathEndOrSingleSlash{
        post {
          entity(as[Vote]) { vote =>
            vote.voteType match{
              case VoteTypes.PHOTO => onComplete(voteForPhoto(vote)) {
                case Success(result) => complete(result.toJson)
                case Failure(e) =>
                  e.printStackTrace()
                  complete((InternalServerError, e.toString))
              }
              case VoteTypes.SONG => onComplete(voteForSong(vote)) {
                case Success(result) => complete(result.toJson)
                case Failure(e) =>
                  e.printStackTrace()
                  complete((InternalServerError, e.toString))
              }
              case _ => complete((BadRequest, "Desired vote type does not exist!"))
            }
          }
        }
      }
    }
}

class PartyActor(implicit timeout: Timeout) extends Actor {

  import PartyMessages._

  val log: Logger = LoggerFactory.getLogger("PartyActor")

  val config: Config = ConfigFactory.load()

  override def receive: PartialFunction[Any, Unit] = {

    case Echo(echo) =>
      pipe(
        PartyActorLogic.echo(echo)
      ) to sender()

    case AddSong(songToAdd,partyID) =>
      pipe(
        PartyActorLogic.addSong(songToAdd,partyID)
      ) to sender()

    case AddPhoto(photoToAdd,partyID) =>
      pipe(
        PartyActorLogic.addPhoto(photoToAdd,partyID)
      ) to sender()

    case CreateParty(name) =>
      pipe(
        PartyActorLogic.createParty(name)
      ) to sender()

    case CheckIfPartyExists(name) =>
      pipe(
        PartyActorLogic.checkIfPartyExists(name)
      ) to sender()

    case LoginToParty(id,pw) =>
      pipe(
        PartyActorLogic.loginToParty(id,pw)
      ) to sender()

    case GetSongsForParty(partyKey) =>
      pipe(
        PartyActorLogic.getSongsForParty(partyKey)
      ) to sender()

    case GetPhotosForParty(partyKey) =>
      pipe(
        PartyActorLogic.getPhotosForParty(partyKey)
      ) to sender()

    case VoteForSong(vote) =>
      pipe(
        PartyActorLogic.voteForSong(vote)
      ) to sender()

    case VoteForPhoto(vote) =>
      pipe(
        PartyActorLogic.voteForPhoto(vote)
      ) to sender()

    case SetSongPlayed(songID, partyKey,playState) =>
      pipe(
        PartyActorLogic.setSongPlayed(songID, partyKey,playState)
      ) to sender()

    case DeleteSong(songID, partyKey) =>
      pipe(
        PartyActorLogic.deleteSong(songID, partyKey)
      ) to sender()
    case DeletePhoto(photoId, partyKey) =>
      pipe(
        PartyActorLogic.deletePhoto(photoId, partyKey)
      ) to sender()
  }
}

object PartyMessages {
  def props(implicit timeout: Timeout): Props = Props(new PartyActor)

  def name: String = "scalable_party" //TODO rename

  case class Echo(echo: String)
  case class AddSong(song: SongToAdd, partyKey: String)
  case class DeleteSong(songID:Long, partyKey: String)
  case class AddPhoto(song: PhotoToAdd, partyKey: String)
  case class DeletePhoto(photoId:Long, partyKey: String)
  case class CreateParty(name:String)
  case class CheckIfPartyExists(name:String)
  case class LoginToParty(id: String, pw: String)
  case class GetSongsForParty(partyKey: String)
  case class GetPhotosForParty(partyKey: String)
  case class VoteForSong(vote: Vote)
  case class VoteForPhoto(vote: Vote)
  case class SetSongPlayed(songID: Long, partyKey: String, playState: String)
}

trait PartyApi {

  import PartyMessages._

  def createPartyActor(): ActorRef

  implicit def executionContext: ExecutionContext

  implicit def requestTimeout: Timeout

  lazy val partyActor: ActorRef = createPartyActor()

  def echo(echo: String): Future[String] =
    (partyActor ? Echo(echo)).mapTo[String]

  def addSong(song: SongToAdd, partyKey:String): Future[SongReturn] =
    (partyActor ? AddSong(song,partyKey)).mapTo[SongReturn]

  def addPhoto(photo: PhotoToAdd, partyKey:String): Future[PhotoReturn] =
    (partyActor ? AddPhoto(photo,partyKey)).mapTo[PhotoReturn]

  def createParty(name:String): Future[Party] ={
    (partyActor ? CreateParty(name)).mapTo[Party]
  }
  def checkIfPartyExists(name:String): Future[Boolean] ={
    (partyActor ? CheckIfPartyExists(name)).mapTo[Boolean]
  }
  def loginToParty(id:String, password:String): Future[Boolean] ={
    (partyActor ? LoginToParty(id, password)).mapTo[Boolean]
  }

  def getSongsForParty(partyKey: String): Future[Seq[SongReturn]] =
    (partyActor ? GetSongsForParty(partyKey)).mapTo[Seq[SongReturn]]

  def getPhotosForParty(partyKey: String): Future[Seq[PhotoReturn]] =
    (partyActor ? GetPhotosForParty(partyKey)).mapTo[Seq[PhotoReturn]]

  def voteForSong (vote: Vote): Future[Int] ={
    (partyActor ? VoteForSong(vote)).mapTo[Int]
  }
  def voteForPhoto (vote: Vote): Future[Int] ={
    (partyActor ? VoteForPhoto(vote)).mapTo[Int]
  }

  def updateSongPlayState(songID:Long, partyKey:String, playState: String): Future[Int] ={
    (partyActor ? SetSongPlayed(songID,partyKey,playState)).mapTo[Int]
  }

  def deleteSong(songID:Long, partyKey:String): Future[Unit] ={
    (partyActor ? DeleteSong(songID,partyKey)).mapTo[Unit]
  }
  def deletePhoto(photoID:Long, partyKey:String): Future[Unit] ={
    (partyActor ? DeletePhoto(photoID,partyKey)).mapTo[Unit]
  }
}

