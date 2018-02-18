package de.scalable.database.queries

import de.scalable.database.ScalableDB
import de.scalable.database.ScalablePostgresProfile.api._
import de.scalable.model.{PhotoFeedEntry, PartyQueueEntry, PhotoReturn}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object PhotoFeedQueries extends ScalableDB {

  val log: Logger = LoggerFactory.getLogger("PhotoFeedQueries")

  def insertPhotoFeedQuery(entry: PhotoFeedEntry) =
    (photoFeedQuery returning photoFeedQuery) += entry

  def deletePhotoFromFeedQuery(photoID: Long, partyID: String) ={
    photoFeedQuery.filter(x => x.photoID === photoID && x.partyID === partyID).delete
  }

  def getEntriesForPartyQuery(partyID: String) = {
    for {
      entries <- photoFeedQuery.filter(_.partyID === partyID).result
      photos <- photoQuery.filter(_.id inSet entries.map(_.photoID)).result
    } yield (entries, photos)
  }

  def getPhotosForParty(partyID: String): Future[Seq[PhotoReturn]] = {
    db.run(getEntriesForPartyQuery(partyID).asTry) flatMap {
      case Success((entries, photos)) => {
        val entryMap = entries.map(e => (e.photoID, e)).toMap
        val result = photos.filter(x => entryMap.keySet.contains(x.id)).map(s => {
          val e = entryMap.get(s.id).get
          s.toReturn(e.upvotes, e.downvotes)
        }).sortWith(_.voteDiff() > _.voteDiff())
        Future.successful(result)
      }
      case Failure(exception) => {
        exception.printStackTrace()
        Future.failed(new Exception(s"Get photo feed entries for party failed ${exception.getMessage}"))
      }
    }
  }

  def deletePhotoFromPhotoFeed(photoID: Long, partyID: String):Future[Unit] = {
    val combinedAction = DBIO.seq(
      deletePhotoFromFeedQuery(photoID,partyID),
      PhotoQueries.deletePhotoQuery(photoID)
    ).transactionally
    db.run(combinedAction)
  }

  //Incrementing not supported by slick https://github.com/slick/slick/issues/497
  def upvotePhotoForParty(photoID:Long, partyID:String):Future[Int] = {
    db.run(sqlu"UPDATE scalable.photo_feed SET upvotes = upvotes + 1 WHERE party_id = $partyID AND photo_id = $photoID;")
  }

  def downvotePhotoForParty(photoID:Long, partyID:String):Future[Int] = {
    db.run(sqlu"UPDATE scalable.photo_feed SET downvotes = downvotes + 1 WHERE party_id = $partyID AND photo_id = $photoID;")
  }

}

