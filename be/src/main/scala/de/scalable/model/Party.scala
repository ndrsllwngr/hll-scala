package de.scalable.model

import java.time.LocalDateTime

case class Party(id: String,
                 name: String,
                 password:String,
                 createdAt: LocalDateTime)

case class PartyLoginRequest(id:String,
                             password:String)
