package scalable.diode

import diode.Action

import scalable.models._

case class AppState (
                    var partyId:Option[String],
                    partyCreateResponse: Option[PartyCreateResponse],
                    songList:List[Song],
                    photoFeed:List[PhotoReturn],
                    videoSuggestions: List[VideoResponse],
                    selectedVideo: Option[VideoResponse],
                    isLoading: Boolean
                    )

case class AppModel(
                     state: AppState
                   )


case class SetPartyId(partyId: String) extends Action

case class SetPartyCreateResponse(createResponse: PartyCreateResponse) extends Action

case class SetSongsForParty(songList: List[Song]) extends Action

case class SetPhotosForParty(photoFeed: List[PhotoReturn]) extends Action

case class GetVideoSuggestions(suggestions: List[VideoResponse]) extends Action

case class SelectVideo(video: Option[VideoResponse]) extends Action

case class SetLoadingState() extends Action

case class ClearLoadingState() extends Action

case class AddSongForParty(partyID: String,
                           song: Song) extends Action

case class VoteSongForParty(partyID: String,
                            songID: Long,
                            positive:Boolean) extends Action

case class CreateParty(partyName: String) extends Action
