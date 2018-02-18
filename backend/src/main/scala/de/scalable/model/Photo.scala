package de.scalable.model

case class Photo(id:Long, url:String){
  def toReturn(upvotes:Int, downvotes: Int): PhotoReturn =
    PhotoReturn(id, url, upvotes, downvotes)
}

case class PhotoToAdd(url:String){

  def toPhoto(): Photo =
    Photo(0L,url)
}
case class PhotoReturn(id:Long,
                       url:String,
                       upvotes: Int,
                       downvotes: Int){

  def voteDiff(): Int = upvotes - downvotes
}

case class PhotoToDelete(id:Long,
                        partyID:String){
}