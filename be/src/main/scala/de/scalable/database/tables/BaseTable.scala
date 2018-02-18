package de.scalable.database.tables


import de.scalable.database.ScalablePostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.NotNull


/** Abstraction over our database tables for tables with ids of type Long
  *
  * @constructor Takes the tag and name of the table in the database and sets a default schema
  * @param tag  marks a specific row represented by an AbstractTable instance
  * @param name of the table in the database
  */
abstract class BaseTableLong[T](tag: Tag, name: String)
  extends Table[T](tag, Some("scalable"), name) {
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
}

abstract class BaseTableString[T](tag: Tag, name: String)
  extends Table[T](tag, Some("scalable"), name) {
  def id: Rep[String] = column[String]("id", O.PrimaryKey)
}

trait VotableTable[T] extends Table[T]{
  def upvotes: Rep[Int] = column[Int]("upvotes", NotNull, O.Default(0))
  def downvotes: Rep[Int] = column[Int]("downvotes",NotNull, O.Default(0))
}