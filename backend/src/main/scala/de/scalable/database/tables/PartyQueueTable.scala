

package de.scalable.database.tables

import java.time.LocalDateTime

import de.scalable.database.ScalablePostgresProfile.api._
import de.scalable.model._
import slick.lifted.{ForeignKeyQuery, ProvenShape}
import slick.sql.SqlProfile.ColumnOption.NotNull

class PartyQueueTable(tag: Tag)
  extends BaseTableLong[PartyQueueEntry](tag, "party_queue") with VotableTable[PartyQueueEntry] {

  def partyID: Rep[String] = column[String]("party_id", NotNull)
  def songID: Rep[Long] = column[Long]("song_id", NotNull)
  def playState: Rep[String] = column[String]("play_state",NotNull, O.Default(PlayStates.QUEUE))


  // scalastyle:off method.name
  def * : ProvenShape[PartyQueueEntry] =
    (id, partyID, songID, upvotes, downvotes, playState) <>
      (PartyQueueEntry.tupled, PartyQueueEntry.unapply)

  // scalastyle:on method.name
  def partyQueuePartyIDFk: ForeignKeyQuery[PartyTable, Party] = foreignKey(
    "party_queue_party_id_fk", partyID, TableQuery[PartyTable])(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  def partyQueueSongIDFk: ForeignKeyQuery[SongTable, Song] = foreignKey(
    "party_queue_song_id_fk", songID, TableQuery[SongTable])(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
}