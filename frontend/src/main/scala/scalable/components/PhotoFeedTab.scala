package scalable.components

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ReactEventFromInput, ScalaComponent}
import org.scalajs.dom.html
import org.scalajs.dom.html.Div

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scalable.config.Config
import scalable.diode.AppState
import scalable.models.PhotoReturn
import scalable.router.AppRouter
import scalable.services.{FirebaseService, RestService}


object PhotoFeedTab {


  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any

  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props(
                    proxy: ModelProxy[AppState],
                    ctl: RouterCtl[AppRouter.Page],
                    admin: Boolean
                  )


  class Backend(bs: BackendScope[Props, Unit]) {
    val host: String = Config.AppConfig.apiHost

    var fileChooser: html.Input = _
    var photoFeed: html.Div = _
    var props: Props = _




    def publishLink(url: String, roomCode: String): Unit = {
      RestService.addPhoto(url, roomCode)
    }

    def onPhotoChanged()(e: ReactEventFromInput) = Callback {
      val choosenFile = e.target.files.item(0)
      props.proxy.value.partyId match {
          case Some(pId) => FirebaseService.uploadPhoto(file = choosenFile, id = pId, success => {
            publishLink(success.downloadURL.toString, pId)}, _ => {})
          case None => println("NO PARTY ID")
        }

      fileChooser.select()
    }

    def getPhotofeed(partyId: String): Future[List[PhotoReturn]] = {
      RestService.getPhotos(partyId)
    }

    def render(p: Props): VdomTagOf[Div] = {
      val proxy = p.proxy()
      props = p

      <.div(^.cls := "form-group",
        <.div(
          <.input(^.`type` := "file", ^.cls := "form-control mb-2 mt-2", ^.id := "files", ^.width :="100%",
            ^.onChange ==> onPhotoChanged())
            .ref(fileChooser = _)
        ), <.div(
          PhotoFeedBox(PhotoFeedBox.Props(p.proxy, p.ctl, admin = p.admin))
        )
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("PhotoFeedPage")
    .renderBackend[Backend]
    .build


  def apply(props: Props) = Component(props)

}
