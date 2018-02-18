

package de.scalable.database.queries

import java.time.LocalDateTime

import de.scalable.database.ScalableDB
import de.scalable.database.ScalablePostgresProfile.api._
import de.scalable.database.queries.PhotoFeedQueries.insertPhotoFeedQuery
import de.scalable.model._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PhotoQueries extends ScalableDB {

  val log: Logger = LoggerFactory.getLogger("PhotoQueries")

  private def insertPhotoQuery(photo: Photo) ={
    (photoQuery returning photoQuery.map(_.id)
      into ((insertedPhoto,id) => insertedPhoto.copy(id =  id).toReturn(0,0)) += photo)
  }

  def deletePhotoQuery(photoID: Long) ={
    photoQuery.filter(_.id === photoID).delete
  }

  def insertPhoto(photoToAdd: PhotoToAdd, partyID: String): Future[PhotoReturn] = {
    val query = for {
      insertedPhoto <- insertPhotoQuery(photoToAdd.toPhoto())
      queueEntry <- insertPhotoFeedQuery(PhotoFeedEntry(0L,partyID,insertedPhoto.id,0,0))
    } yield insertedPhoto

    db.run(query)
  }


}
