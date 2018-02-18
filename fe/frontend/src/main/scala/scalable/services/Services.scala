package scalable.services

import firebase._
import org.scalajs.dom.raw.File

import scala.scalajs.js
import scalable.models.VoteAble

object FirebaseService {

  val apiKey = "AIzaSyC8vZ20nRwOpSmuyF0TjimoHHqSxkWK4cE"
  val authDomain = "scalable-195120.firebaseapp.com"
  val databaseURL = "https://scalable-195120.firebaseio.com"
  val projectId = "scalable-195120"
  val storageBucket = ""
  val messagingSenderId = "547307244060"
  val appName = "scalable"

  def init() :Unit = {
    val config = FirebaseConfig(apiKey, authDomain, databaseURL, storageBucket, messagingSenderId)
    Firebase.initializeApp(config, appName)
  }


  def uploadPhoto(file : File, id: String, onSuccess: js.Function1[firebase.storage.UploadTaskSnapshot, Any], onRejected: js.Function1[Error, Any] ): Unit ={
    val app = Firebase.app(appName)
    val storage = Firebase.storage(app).refFromURL(s"gs://scalable-195120.appspot.com/$id/${file.name}")
    storage.put(file).then(onSuccess, onRejected)
  }

  def deletePhoto(url : String, id: String): Unit ={
    val app = Firebase.app(appName)
    val storage = Firebase.storage(app).refFromURL(url)
    storage.delete()
  }
}

object DeleteService{

  def delete(votable: VoteAble): Unit ={
      votable.voteType match {
        case "SONG" => deleteSong(votable.compId, votable.partyID)
        case "PHOTO" => deletePhoto(votable)
        case "_" =>
      }
  }

  def deleteSong(id: Long, partyId: String): Unit = {
    RestService.deleteSong (id, partyId)
  }

  def deletePhoto(votable: VoteAble): Unit ={
    RestService.deletePhoto(votable.compId, votable.partyID)

    if(votable.url.nonEmpty) FirebaseService.deletePhoto(votable.url.get, votable.partyID)
  }

}
