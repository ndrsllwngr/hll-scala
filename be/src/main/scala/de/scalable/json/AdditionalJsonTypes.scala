


package de.scalable.json

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

import scala.util.{Failure, Success, Try}


trait AdditionalJsonTypes extends DefaultJsonProtocol {


  implicit object LocalDateJsonFormat extends RootJsonFormat[LocalDate] {
    override def write(date: LocalDate): JsString = JsString(date.toString)

    override def read(value: JsValue): LocalDate = value match {
      case JsString(date) => LocalDate.parse(date)
      case _ => LocalDate.MIN
    }
  }

  implicit object LocalDateTimeJsonFormat extends RootJsonFormat[LocalDateTime] {
    override def write(timestamp: LocalDateTime): JsString = JsString(timestamp.toString)

    override def read(value: JsValue): LocalDateTime = value match {
      case JsString(timestamp) => Try(LocalDateTime.parse(timestamp)) match {
        case Success(res) => res
        case Failure(_) => LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)
      }
      case _ => LocalDateTime.MIN
    }
  }

  implicit object UUIDJsonFormat extends RootJsonFormat[UUID] {
    override def write(uuid: UUID): JsString = JsString(uuid.toString)

    override def read(value: JsValue): UUID = value match {
      case JsString(uuid) => UUID.fromString(uuid)
      case _ => throw new Exception("Cannot parse uuid from string: " + value)
    }
  }

}
