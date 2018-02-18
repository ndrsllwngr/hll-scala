# Routes
## create party
Methode: PUT

URL: localhost:5000/party

Body: String mit namen der Party 

Response: JSON mit (id: String,
                 name: String,
                 password:String,
                 createdAt: LocalDateTime)
            
## login to party
Methode: POST

URL: localhost:5000/party/login/

Body: PartyLoginRequest(id:String,
                        password:String)

Response: true oder false            
            
## check if party exists
Methode: GET

URL: localhost:5000/party/PARTY_ID

Response: true oder false

## add song to party
Methode: PUT

URL: localhost:5000/party/song/PARTY_ID

Body: SongToAdd(streamingServiceID: String,
                     name: String,
                     artist: String,
                     album: String,
                     albumCoverUrl: String)
                     
Response: SongReturn(id:Long,
                streamingServiceID: String,
                name: String,
                artist: String,
                album: String,
                albumCoverUrl: String,
                upvotes: Int,
                downvotes: Int,
                played: Boolean)
                
Wichtig: header "Content-Type" muss den Wert "application/json" haben
                
## get songs for party
Methode: GET

URL: localhost:5000/party/song/PARTY_ID

                     
Response: liste aus SongReturn(id:Long,
                streamingServiceID: String,
                name: String,
                artist: String,
                album: String,
                albumCoverUrl: String,
                upvotes: Int,
                downvotes: Int,
                played: Boolean)
                
## add photos to party
Methode: PUT

URL: localhost:5000/party/photo/PARTY_ID

Body: PhotoToAdd(url:String)
                     
Response: PhotoReturn(id:Long,
                       url:String,
                       upvotes: Int,
                       downvotes: Int)
                
Wichtig: header "Content-Type" muss den Wert "application/json" haben

## get photos for party
Methode: GET

URL: localhost:5000/party/photo/PARTY_ID

                     
Response: liste aus PhotoReturn(id:Long,
                       url:String,
                       upvotes: Int,
                       downvotes: Int)
                
## vote song/photo for party
Methode: POST

URL: localhost:5000/vote

Body: PartyVote(partyID: String,
                     songID: Long,
                     positive:Boolean  //positive = true wenn upvote, false wenn downvote
                     votType: String) //entweder SONG oder PHOTO

                     
Response: 1 wenn geklappt, ansonsten 0 (wenn kein Song für die Daten existiert) oder ein Fehler

Wichtig: header "Content-Type" muss den Wert "application/json" haben
                
## Song status updaten
Methode: POST

URL: localhost:5000/party/song/

Body: SongPlayStateUpdate(id:Long,
                              partyID:String,
                              playState:String)
                              
PlayStates: val QUEUE = "QUEUE oder val PLAYING = "PLAYING" oder val PLAYED = "PLAYED"
                     
Response: Int oder ein Fehler

Wichtig: header "Content-Type" muss den Wert "application/json" haben
                
 ## Song Löschen
Methode: DELETE

URL: localhost:5000/party/song/

Body: SongPlayedOrDeleted(id:Long,
                      partyID:String)
                     
Response: Song ${songToDelete.id} for party ${songToDelete.partyID} deleted oder ein Fehler

Wichtig: header "Content-Type" muss den Wert "application/json" haben
                               
 ## Foto Löschen
Methode: DELETE

URL: localhost:5000/party/photo/

Body: PhotoDeleted(id:Long,
                      partyID:String)
                     
Response: Photo ${songToDelete.id} for party ${songToDelete.partyID} deleted oder ein Fehler

Wichtig: header "Content-Type" muss den Wert "application/json" haben
                               
