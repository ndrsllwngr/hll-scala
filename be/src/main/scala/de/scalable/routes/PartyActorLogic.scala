

package de.scalable.routes


import java.time.LocalDateTime

import de.scalable.database.queries._
import de.scalable.model._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future
import scala.util.Random

object PartyActorLogic {


  val partyPasswords = Seq("lion", "giraffe", "wolf", "caterpillar", "elephant", "snake", "tiger")
  val randomNumber = new Random

  private val log: Logger = LoggerFactory.getLogger("ScalableActorLogic")

  def echo(echo: String) = {
    Future.successful(echo)
  }

  def addSong(song: SongToAdd, partyKey:String) = {
    SongQueries.insertSong(song,partyKey)
  }

  def addPhoto(photo: PhotoToAdd, partyKey:String) = {
    PhotoQueries.insertPhoto(photo,partyKey)
  }

  def createParty(name:String) ={
    val now = LocalDateTime.now()
    val key = generateKey(6).toUpperCase
    val password = partyPasswords(randomNumber.nextInt(partyPasswords.length))
    val party = Party(key,name,password,now)

    PartyQueries.insertParty(party)
  }
  def checkIfPartyExists(name:String) ={
    PartyQueries.checkIfPartyExists(name)
  }

  def loginToParty(id:String,pw:String) ={
    PartyQueries.login(id,pw)
  }

  def getSongsForParty(partyID:String) = {
    PartyQueueQueries.getSongsForParty(partyID)
  }

  def getPhotosForParty(partyID:String) = {
    PhotoFeedQueries.getPhotosForParty(partyID)
  }

  def voteForSong(vote:Vote) = {
    vote match {
      case Vote(key,songID,true,_)   => PartyQueueQueries.upvoteSongForParty(songID,key)
      case Vote(key,songID,false,_)  => PartyQueueQueries.downvoteSongForParty(songID,key)
    }
  }
  def voteForPhoto(vote:Vote) = {
    vote match {
      case Vote(key,songID,true,_)   => PhotoFeedQueries.upvotePhotoForParty(songID,key)
      case Vote(key,songID,false,_)  => PhotoFeedQueries.downvotePhotoForParty(songID,key)
    }
  }
  def setSongPlayed(songID:Long, partyKey:String, playState:String) = {
    PartyQueueQueries.updateSongPlayState(songID, partyKey, playState)
  }

  def deleteSong(songID:Long, partyKey:String) = {
    PartyQueueQueries.deleteSongFromQueueAndSongs(songID, partyKey)
  }

  def deletePhoto(photoID:Long, partyKey:String) = {
    PhotoFeedQueries.deletePhotoFromPhotoFeed(photoID, partyKey)
  }

  private def generateKey(length: Int): String = {
    val randomAlphanumeric = Random.alphanumeric
    randomAlphanumeric take length mkString
  }



}

