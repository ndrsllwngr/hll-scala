package de.scalable.database.queries


import de.scalable.model.{PartyQueueEntry, PlayStates, SongReturn}
import org.slf4j.{Logger, LoggerFactory}
import de.scalable.database.ScalableDB
import de.scalable.database.ScalablePostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scala.concurrent.Future

object PartyQueueQueries extends ScalableDB {

  val log: Logger = LoggerFactory.getLogger("PartyQueueQueries")

  def insertQueueEntryQuery(entry: PartyQueueEntry) =
    (partyQueueQuery returning partyQueueQuery) += entry

  def deleteSongFromQueueQuery(songID: Long, partyID: String) ={
    partyQueueQuery.filter(x => x.songID === songID && x.partyID === partyID).delete
  }

  def getEntriesForPartyQuery(partyID: String) = {
    for {
      entries <- partyQueueQuery.filter(_.partyID === partyID).result
      songs <- songQuery.filter(_.id inSet entries.map(_.songID)).result
    } yield (entries, songs)
  }

  def getSongsForParty(partyID: String): Future[Seq[SongReturn]] ={
    db.run(getEntriesForPartyQuery(partyID).asTry) flatMap {
      case Success((entries, songs)) => {
        val entryMap = entries.map(e => (e.songID, e)).toMap
        val result = songs.filter(x => entryMap.keySet.contains(x.id)).map(s =>{
          val e = entryMap.get(s.id).get
          s.toReturn(e.upvotes,e.downvotes,e.playState)
        }).sortWith(_.voteDiff() > _.voteDiff())
        Future.successful(result)
      }
      case Failure(exception) => {
        exception.printStackTrace()
        Future.failed(new Exception(s"Get party queue entries for party failed ${exception.getMessage}"))
      }
    }
  }

  def updateSongPlayState(songID: Long, partyID: String, playState: String):Future[Int] = {
    val q = for { e <- partyQueueQuery if (e.songID === songID &&  e.partyID === partyID)} yield e.playState
    playState match {
      case PlayStates.QUEUE | PlayStates.PLAYED => db.run(q.update(playState))
      case PlayStates.PLAYING =>{
        val setAllPlayingToPlayedQuery = for { e <- partyQueueQuery if (e.playState === PlayStates.PLAYING)} yield e.playState
        db.run(DBIO.seq(
          setAllPlayingToPlayedQuery.update(PlayStates.PLAYED),
          q.update(playState)
        ).transactionally).map(_ => 1)
      }
      case _ => Future.failed(new IllegalArgumentException(s"Illegal playstate : ${playState}"))
    }
  }

  def deleteSongFromQueueAndSongs(songID: Long, partyID: String):Future[Unit] = {
    val combinedAction = DBIO.seq(
      deleteSongFromQueueQuery(songID,partyID),
      SongQueries.deleteSongQuery(songID)
    ).transactionally
    db.run(combinedAction)
  }

  //Incrementing not supported by slick https://github.com/slick/slick/issues/497
  def upvoteSongForParty(songID:Long, partyID:String):Future[Int] = {
    db.run(sqlu"UPDATE scalable.party_queue SET upvotes = upvotes + 1 WHERE party_id = $partyID AND song_id = $songID AND play_state <> 'PLAYED'AND play_state <> 'PLAYING';")
  }

  def downvoteSongForParty(songID:Long, partyID:String):Future[Int] = {
    db.run(sqlu"UPDATE scalable.party_queue SET downvotes = downvotes + 1 WHERE party_id = $partyID AND song_id = $songID AND play_state <> 'PLAYED'AND play_state <> 'PLAYING';")
  }

}

