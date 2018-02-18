

package de.scalable.database.tables

import java.time.LocalDateTime

import de.scalable.database.ScalablePostgresProfile.api._
import de.scalable.model._
import slick.lifted.ProvenShape
import slick.sql.SqlProfile.ColumnOption.NotNull

class PartyTable(tag: Tag)
  extends BaseTableString[Party](tag, "party") {

  def name: Rep[String] = column[String]("name", NotNull)
  def password : Rep[String] = column[String]("password", NotNull)
  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at", NotNull)

  // scalastyle:off method.name
  def * : ProvenShape[Party] =
    (id, name, password, createdAt) <>
      (Party.tupled, Party.unapply)

  // scalastyle:on method.name
}