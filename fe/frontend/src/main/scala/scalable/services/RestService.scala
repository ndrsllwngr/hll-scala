package scalable.services

import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalable.config.Config
import scalable.models._


object RestService {

  val host: String = Config.AppConfig.apiHost

  def createParty(partyName: String): Future[PartyCreateResponse] = {
    val content = partyName.asInstanceOf[Ajax.InputData]
    Ajax.put(
      url = s"$host/party",
      data = content,
      headers = Map("Content-Type" -> "text/plain")
    ).map { res =>
      val option = decode[PartyCreateResponse](res.responseText)
      option match {
        case Left(_) => PartyCreateResponse("", "", "", "")
        case Right(partyCreateResponse) => partyCreateResponse
      }
    }
  }

  def joinParty(partyId: String): Future[Boolean] = {
    Ajax.get(
      url = s"$host/party/$partyId"
    ).map { res =>
      val option = decode[Boolean](res.responseText)
      option match {
        case Left(_) => false
        case Right(exists) => exists
      }
    }
  }

  def joinPartyAsAdmin(partyId: String, password: String): Future[Boolean] = {
    val content = PartyLoginRequest(partyId, password).asJson.asInstanceOf[Ajax.InputData]
    Ajax.post(
      url = s"$host/party/login",
      data = content,
      headers = Map("Content-Type" -> "application/json")
    ).map { res =>
      val option = decode[Boolean](res.responseText)
      option match {
        case Left(_) => false
        case Right(bool) => bool
      }
    }
  }

  def addSongToParty(partyID: String, videoresponse: VideoResponse): Future[Song] = {
    val content = buildSendSongFromVideoRespone(videoresponse).asJson.asInstanceOf[Ajax.InputData]
    Ajax.put(
      url = s"$host/party/song/$partyID",
      data = content,
      headers = Map("Content-Type" -> "application/json")
    ).map { res =>
      val option = decode[Song](res.responseText)
      option match {
        case Left(_) => Song("", "", "", 0, 0, 0,"", "",  "", "")
        case Right(song) => song
      }
    }
  }

  def getSongs(partyID: String): Future[List[Song]] = {
    Ajax.get(
      url = s"$host/party/song/$partyID"
    ).map { res =>
      val option = decode[List[Song]](res.responseText)
      option match {
        case Left(_) => List.empty[Song]
        case Right(songList) => songList
      }
    }
  }

  def addPartyVote(partyID: String, id: Long, positive: Boolean, voteType: String): Future[Int] = {
    val content = PartyVote(partyID, id, positive, voteType).asJson.asInstanceOf[Ajax.InputData]
    Ajax.post(
      url = s"$host/vote",
      data = content,
      headers = Map("Content-Type" -> "application/json")
    ).map { res =>
      val option = decode[Int](res.responseText)
      option match {
        case Left(_) => -1
        case Right(int) => int
      }
    }
  }

  def setSongPlayed(songID: Long, partyID: String): Future[Int] = {
    val content = SongPlayStateUpdate(songID, partyID, "PLAYED").asJson.asInstanceOf[Ajax.InputData]
    Ajax.post(
      url = s"$host/party/song",
      data = content,
      headers = Map("Content-Type" -> "application/json")
    ).map { res =>
      val option = decode[Int](res.responseText)
      option match {
        case Left(_) => -1
        case Right(int) => int
      }
    }
  }

  def setSongPlaying(songID: Long, partyID: String): Future[Int] = {
    val content = SongPlayStateUpdate(songID, partyID, "PLAYING").asJson.asInstanceOf[Ajax.InputData]
    Ajax.post(
      url = s"$host/party/song",
      data = content,
      headers = Map("Content-Type" -> "application/json")
    ).map { res =>
      val option = decode[Int](res.responseText)
      option match {
        case Left(_) => -1
        case Right(int) => int
      }
    }
  }

    def deleteSong(songID: Long, partyID: String): Future[Int] = {
      val content = DeleteSong(songID, partyID).asJson.asInstanceOf[Ajax.InputData]
      Ajax.delete(
        url = s"$host/party/song",
        data = content,
        headers = Map("Content-Type" -> "application/json")
      ).map { res =>
        val option = decode[Int](res.responseText)
        option match {
          case Left(_) => -1
          case Right(int) => int
        }
      }
    }

  def deletePhoto(photoID: Long, partyID: String): Future[String] = {
    val content = PhotoToDelete(photoID, partyID).asJson.asInstanceOf[Ajax.InputData]
    Ajax.delete(
      url = s"$host/party/photo",
      data = content,
      headers = Map("Content-Type" -> "application/json")
    ).map { res =>
      val option = decode[String](res.responseText)
      option match {
        case Left(_) => ""
        case Right(response) => response
      }
    }
  }


  def addPhoto(downloadUrl: String, partyID: String): Future[Int] ={
    val content = AddPhotosToParty(downloadUrl).asJson.asInstanceOf[Ajax.InputData]
    Ajax.put(
      url = s"$host/party/photo/$partyID",
      data = content,
      headers = Map("Content-Type" -> "application/json")
    ).map { res =>
      val option = decode[Int](res.responseText)
      option match {
        case Left(_) => -1
        case Right(int) => int
      }
    }
  }

  def getPhotos(partyID: String): Future[List[PhotoReturn]] = {
    Ajax.get(
      url = s"$host/party/photo/$partyID"
    ).map { res =>
      val option = decode[List[PhotoReturn]](res.responseText)
      option match {
        case Left(_) => List.empty[PhotoReturn]
        case Right(photoFeed) => photoFeed
      }
    }
  }

  def buildSendSongFromVideoRespone(videoResponse: VideoResponse): SendSong = {
    val streamingServiceID = videoResponse.id.videoId
    val name = videoResponse.snippet.title
    val artist = videoResponse.snippet.channelTitle
    val album = ""
    val albumCoverUrl = videoResponse.snippet.thumbnails("high").url

    SendSong(streamingServiceID, name, artist, album, albumCoverUrl, "YOUTUBE")
  }
}
