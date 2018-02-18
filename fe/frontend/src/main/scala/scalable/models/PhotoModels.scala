package scalable.models

case class AddPhotosToParty
(
  url: String
)

case class PhotoReturn
(
  id: Long,
  url: String,
  upvotes: Int,
  downvotes: Int
)

case class PhotoToDelete(id:Long,
                         partyID:String){
}
