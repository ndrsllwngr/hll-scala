package scalable.components

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.scalajs.dom.html.Div

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.timers.SetIntervalHandle
import scalable.config.Config
import scalable.diode.{AppCircuit, AppState, SetSongsForParty}
import scalable.models._
import scalable.router.AppRouter
import scalable.services.RestService

object AdminTab {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any

  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props(
                    proxy: ModelProxy[AppState],
                    ctl: RouterCtl[AppRouter.Page]
                  )


  class Backend(bs: BackendScope[Props, Unit]) {
    val host: String = Config.AppConfig.apiHost

    var timer: SetIntervalHandle = _

    var player: Option[Player] = Option.empty
    var props: Props = _


    def onPlayerReady(e: Event): js.UndefOr[(Event) => Any] = {
      e.target.whenDefined(p => { p.playVideo()
        TagMod()
      })
      (e:Event) => true
    }

    def resolveNext(player: Player): Unit = {
      println("resolve")
      val state = props.proxy.modelReader.apply()
      var songList = state.songList.filter(s => s.playState.equals("QUEUE"))
      if (songList.isEmpty) {
        songList = scala.util.Random.shuffle(state.songList)
      }
      player.hasSong = true
      if (songList.nonEmpty) props.proxy.value.partyId match {
        case Some(id) => loadSong(player, songList.head, id)
        case None => println("No Party ID")
      }
    }


    def loadSong(player: Player, next: Song, roomCode: String): Unit = {
      RestService.setSongPlaying(next.id, roomCode).onComplete(_ => getData())
      player.loadVideoById(next.streamingServiceID, 0.0, "hd720")
    }

    def onPlayerStateChange(e: Event): js.UndefOr[(Event) => Any] = {
      println("state Change")
      e.target.whenDefined(p => {
        println(s"state ${p.getPlayerState()}")
        p.getPlayerState() match {
          case 0 => resolveNext(p)
          case -1 => p.playVideo()
          case _ =>
        }

        TagMod()
      })
      (e:Event) => true
    }

    def onPlayerError(e: Event): js.UndefOr[(Event) => Any] = {
      (e:Event) => true
    }

    def mounted: Callback = Callback  {
      startUpdateInterval()
      getData()
    }

    def unmounted: Callback = Callback {
      println("Unmounted")
      js.timers.clearInterval(timer)
    }

    def startUpdateInterval(): Unit = {
      timer = js.timers.setInterval(1000) { // note the absence of () =>
        getData()
      }
    }

    def getData(): Unit = {
      props.proxy.value.partyId match {
        case Some(id) => RestService.getSongs(id).map { songs => {
          AppCircuit.dispatch(SetSongsForParty(songs))
          if (songs.nonEmpty && player.isDefined) {
            if(!player.get.hasSong) resolveNext(player.get)
          } else if (songs.isEmpty && player.isDefined){
            if(player.get.hasSong) player.get.hasSong = false
          }
        }
        }
        case None => println("NO PARTY ID")
      }
    }

    def createPlayer(): Unit = {
      println("create player")
      val tag = org.scalajs.dom.document.createElement("script").asInstanceOf[org.scalajs.dom.html.Script]
      tag.src = "https://www.youtube.com/iframe_api"
      val firstScriptTag = org.scalajs.dom.document.getElementsByTagName("script").item(0)
      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag)
      val width = org.scalajs.dom.window.innerWidth / 2
      org.scalajs.dom.window.asInstanceOf[js.Dynamic].onYouTubeIframeAPIReady = () => {
        player = Some(new Player("player", PlayerOptions(
          width = "530px",
          height = s"${width / 2}",
          videoId = "xecWX51PElI",
          events = PlayerEvents(
            onReady = onPlayerReady(_),
            onError = onPlayerError(_),
            onStateChange = onPlayerStateChange(_)
          ),
          playerVars = PlayerVars(
            playsinline = 1.0
          )
        )))
      }
    }


    def render(p: Props): VdomTagOf[Div] = {
      val proxy = p.proxy()
      props = p
      if(player.isEmpty)
        createPlayer()
      var roomCode: String = "NO PARTY ID"
      p.proxy.value.partyId match {
        case Some(id) => roomCode = id
        case None => "NO PARTY ID"

      }
      val maxCompWidth = org.scalajs.dom.window.innerWidth/2

      <.div(^.width := "parent",
          <.div(^.id := "player")
          , <.div(^.cls := "mb-5", PlaylistBox(PlaylistBox.Props(p.proxy, p.ctl, admin = true)))
          , <.div(AlreadyPlayedComp(AlreadyPlayedComp.Props(p.proxy, p.ctl)))
        )
    }
  }


  val Component = ScalaComponent.builder[Props]("AdminTab")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted)
    .componentWillUnmount(scope => scope.backend.unmounted)
    .build


  def apply(props: Props) = Component(props)

}
