package scalable.models

case class PartyCreateResponse
(
  id: String,
  name: String,
  password: String,
  createdAt: String
)

case class Song
(
  //id: Int,

  name: String,
  albumCoverUrl: String,
  streamingServiceID: String,
  downvotes: Int,
  upvotes: Int,
  id: Long,
  artist: String,
  album: String,
  playState: String,
  streamingServiceType: String
  //, createdAt: LocalDateTime

)

case class SongListElement
(
  name: String,
  artist: String
)

case class SendSong
(
  streamingServiceID: String,
  name: String,
  artist: String,
  album: String,
  albumCoverUrl: String,
  streamingServiceType: String
)

case class PartyVote
(
  partyID: String,
  songID: Long,
  positive: Boolean,
  voteType: String
)


case class VoteAble
(
  partyID: String,
  compId: Long,
  upvotes: Int,
  downvotes: Int,
  voteType: String,
  var url: Option[String] = Option.empty
)

case class SongPlayStateUpdate(id:Long, partyID:String, playState:String)

case class DeleteSong
(
  id: Long,
  partyID: String
)

case class PartyLoginRequest
(
  id: String,
  password: String
)



