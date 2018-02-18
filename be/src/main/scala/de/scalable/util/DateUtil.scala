

package de.scalable.util

import java.sql.Timestamp
import java.time.LocalDateTime


object DateUtil {

  def timestamp(timestampSeconds: Long): LocalDateTime = new Timestamp(timestampSeconds * 1000).toLocalDateTime

}
