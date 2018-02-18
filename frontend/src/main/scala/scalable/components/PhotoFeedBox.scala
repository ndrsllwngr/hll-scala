package scalable.components

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.timers.SetIntervalHandle
import scalable.diode.{AppCircuit, AppState, SetPhotosForParty}
import scalable.models._
import scalable.router.AppRouter
import scalable.services.RestService

object PhotoFeedBox {

  case class Props (
                     proxy: ModelProxy[AppState],
                     ctl: RouterCtl[AppRouter.Page],
                     admin: Boolean
                   )

  class Backend(bs: BackendScope[Props, Unit]) {

    var timer: SetIntervalHandle = _
    var props: Props = _

    def mounted: Callback = Callback{
      startUpdateInterval()
      getData()
    }
    def startUpdateInterval(): Unit ={
      timer = js.timers.setInterval(1000) { // note the absence of () =>
        getData()
      }
    }

    def getData(): Unit = {
      props.proxy.value.partyId match {
        case Some(id) => RestService.getPhotos(id).map { photos =>
          println("Getting Data")
          AppCircuit.dispatch(SetPhotosForParty(photos))
        }
        case None => println("NO PARTY ID")
      }

    }


    def unmounted: Callback = Callback {
      println("Unmounted")
      js.timers.clearInterval(timer)
    }
    def render(p: Props): VdomElement = {
      props = p
      <.div(getFeed(props).toTagMod)
    }
  }

  def getFeed(props: Props) ={
    val proxy                        = props.proxy()
    val feed                        = proxy.photoFeed
    val partyId                      = proxy.partyId
    partyId match {
      case Some(id) =>
        if (feed.nonEmpty) {
          for (photo <- feed) yield photoView(photo, id, props)
        } else {
          Seq(<.p("No photos uploaded yet.", ^.cls := "text-white", ^.fontSize := "30", ^.textAlign := "center"),
            <.p("Dear guests, feel free adding some :-)", ^.cls := "text-white", ^.fontSize := "30", ^.textAlign := "center"))
        }
      case None => Seq(<.p("No party ID set"))
    }
    }


  def photoView(photo: PhotoReturn, partyID:String ,props: Props) ={
    val customStyle = VdomStyle("background-image")
    val id = photo.id
    val pUrl = photo.url

    <.div( // Playlist Row (Parent)
      ^.cls := "d-flex flex-row align-items-center bg-white text-dark p-2",
      <.img(
        ^.cls := "mr-2",
        ^.flex := "1 1 auto",
        ^.width := "80%",
        ^.height := "auto",
        ^.src := pUrl
      ),
      <.div( // Child 3 VoteComp
        ^.flex := "0 0 auto",
        VoteComp(VoteComp.Props(VoteAble(partyID = partyID, compId = photo.id, voteType = "PHOTO" ,upvotes = photo.upvotes, downvotes = photo.downvotes, url = Some(pUrl)), admin = props.admin)))
    )
  }

  val Component = ScalaComponent.builder[Props]("PhotoFeedBox")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted)
    .componentWillUnmount(scope => scope.backend.unmounted)
    .build

  def apply(props: Props) = Component(props)

}
