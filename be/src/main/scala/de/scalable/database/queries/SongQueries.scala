

package de.scalable.database.queries


import de.scalable.database.ScalableDB
import de.scalable.database.ScalablePostgresProfile.api._
import de.scalable.model._
import org.slf4j.{Logger, LoggerFactory}


import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import java.time.LocalDateTime

import de.scalable.database.queries.PartyQueueQueries.insertQueueEntryQuery

object SongQueries extends ScalableDB {

  val log: Logger = LoggerFactory.getLogger("SongQueries")

  private def insertSongQuery(song: Song) ={
    (songQuery returning songQuery.map(_.id)
      into ((insertedSong,id) => insertedSong.copy(id =  id).toReturn(0,0, PlayStates.QUEUE)) += song)
  }
  def deleteSongQuery(songID: Long) ={
    songQuery.filter(_.id === songID).delete
  }

  def insertSong(songToAdd: SongToAdd, partyID: String): Future[SongReturn] = {
    val now = LocalDateTime.now()
    val query = for {
    insertedSong <- insertSongQuery(songToAdd.toSong())
    queueEntry <- insertQueueEntryQuery(PartyQueueEntry(0L,partyID,insertedSong.id,0,0, PlayStates.QUEUE))
    } yield insertedSong

    db.run(query)
  }



}
