

package de.scalable.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import de.scalable.model.{SongPlayStateUpdate, _}
import spray.json.RootJsonFormat

import scala.language.implicitConversions


trait ModelJsonSupport extends SprayJsonSupport with AdditionalJsonTypes {

  implicit val songFormat: RootJsonFormat[Song] = jsonFormat7(Song)
  implicit val songToCreateFormat: RootJsonFormat[SongToAdd] = jsonFormat6(SongToAdd)
  implicit val songReturnFormat: RootJsonFormat[SongReturn] = jsonFormat10(SongReturn)
  implicit val songToDeleteFormat: RootJsonFormat[SongToDelete] = jsonFormat2(SongToDelete)
  implicit val songPlayStateUpdate: RootJsonFormat[SongPlayStateUpdate] = jsonFormat3(SongPlayStateUpdate)
  implicit val photoToCreateFormat: RootJsonFormat[PhotoToAdd] = jsonFormat1(PhotoToAdd)
  implicit val photoToDeleteFormat: RootJsonFormat[PhotoToDelete] = jsonFormat2(PhotoToDelete)
  implicit val photoReturnFormat: RootJsonFormat[PhotoReturn] = jsonFormat4(PhotoReturn)
  implicit val partyFormat: RootJsonFormat[Party] = jsonFormat4(Party)
  implicit val partyLoginFormat: RootJsonFormat[PartyLoginRequest] = jsonFormat2(PartyLoginRequest)
  implicit val partyQueueEntryFormat: RootJsonFormat[PartyQueueEntry] = jsonFormat6(PartyQueueEntry)
  implicit val partyVoteFormat: RootJsonFormat[Vote] = jsonFormat4(Vote)

}
