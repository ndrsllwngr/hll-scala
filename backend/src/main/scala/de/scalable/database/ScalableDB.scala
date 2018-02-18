

package de.scalable.database

import com.github.tminglei.slickpg.{ExPostgresProfile, PgDate2Support}
import de.scalable.database.tables._
import org.slf4j.LoggerFactory
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery
import slick.util.SlickLogger


trait ScalablePostgresProfile
  extends ExPostgresProfile
    with PgDate2Support {

  override val api: ScalablePostgresApi.type = ScalablePostgresApi
  override lazy val logger: SlickLogger = new SlickLogger(LoggerFactory.getLogger(getClass.getSimpleName))

  object ScalablePostgresApi
    extends API
      with DateTimeImplicits

}

object ScalablePostgresProfile extends ScalablePostgresProfile


/**
  * Trait to get a connection to the de.scalable.database
  */
trait ScalableDB {
  private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("scalable.postgres")

  val db: JdbcProfile#Backend#Database = dbConfig.db

  val songQuery       = TableQuery[SongTable]
  val photoQuery      = TableQuery[PhotoTable]
  val partyQuery      = TableQuery[PartyTable]
  val partyQueueQuery = TableQuery[PartyQueueTable]
  val photoFeedQuery  = TableQuery[PhotoFeedTable]
}

