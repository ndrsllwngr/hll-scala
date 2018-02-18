package de.scalable.model

case class PartyQueueEntry(id: Long,
                           partyID: String,
                           songID: Long,
                           upvotes:Int,
                           downvotes:Int,
                           playState:String)

case class PhotoFeedEntry(id: Long,
                          partyID: String,
                          photoID: Long,
                          upvotes:Int,
                          downvotes:Int)