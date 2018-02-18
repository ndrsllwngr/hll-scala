

package de.scalable.database.tables

import de.scalable.database.ScalablePostgresProfile.api._
import de.scalable.model._
import slick.lifted.ProvenShape
import slick.sql.SqlProfile.ColumnOption.NotNull

class PhotoTable(tag: Tag)
  extends BaseTableLong[Photo](tag, "photo") {
  def url: Rep[String] = column[String]("url", NotNull)

  // scalastyle:off method.name
  def * : ProvenShape[Photo] =
    (id, url) <>
      (Photo.tupled, Photo.unapply)
  // scalastyle:on method.name
}