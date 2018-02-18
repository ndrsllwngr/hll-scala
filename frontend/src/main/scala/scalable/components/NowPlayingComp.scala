package scalable.components

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._

import scalable.config.Config
import scalable.diode._
import scalable.models._
import scalable.router.AppRouter

object NowPlayingComp {

  case class Props (
                     proxy: ModelProxy[AppState],
                     ctl: RouterCtl[AppRouter.Page]
                   )

  class Backend(bs: BackendScope[Props, Unit]) {
    def mounted: Callback = Callback.log("Mounted!")

    val host: String = Config.AppConfig.apiHost

    def render(props: Props): VdomElement = {
      <.div(getSongs(props))
    }
  }

  def getSongs(props: Props) : VdomTag ={
    val proxy                        = props.proxy()
    //val dispatch: Action => Callback = props.proxy.dispatchCB
    val songs                        = proxy.songList
    val nowPlaying = proxy.songList.find(x => x.playState.equalsIgnoreCase("PLAYING"))
    nowPlaying match {
      case Some(song) => {
        val partyId = proxy.partyId
        partyId match {
          case Some(id) => songView(song,id)
          case None => noView("No party id set.")
        }
      }
      case None => noView("At the moment no song is playing.")
    }

  }

  def noView(message: String) = {
    <.div(
    <.p(message))
  }

  def songView(song:Song, partyID:String ) ={
    val customStyle = VdomStyle("background-image")
    val id = song.id
    val name = song.name
    val artist = song.artist
    val albumCoverUrl = song.albumCoverUrl
    <.div( // Playlist Row (Parent)
      ^.cls := "d-flex flex-row align-items-center bg-white text-dark p-2 mt-2",
      ^.borderColor := "black",
      <.div( // Child 1 AlbumCover
        ^.cls := "mr-2",
        ^.flex := "0 0 auto",
        ^.width := 100.px,
        ^.height := 100.px,
        ^.borderRadius := "50%",
        ^.backgroundClip := "padding-box",
        ^.backgroundImage := s"url($albumCoverUrl)",
        ^.backgroundSize := "cover",
        ^.backgroundPosition := "center center"
      ),
      <.div( // Child 2 Song title
        <.pre(
          ^.cls := "h6 mb-0 text-success",
          "NOW PLAYING"),
        ^.flex := "1 1 auto",
        ^.cls := "h3 mb-0 mr-2 text-truncate",
        name,<.pre(
          ^.cls := "h6 mb-0 text-muted",
          artist)
      ),
      <.div( // Child 3 VoteComp
        ^.flex := "0 0 auto"
      )
    )
  }

  val Component = ScalaComponent.builder[Props]("PlaylistBox")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted)
    .build

  def apply(props: Props) = Component(props)
}
