package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.scalajs.dom.html.Div

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scalable.components.{AdminTab, PhotoFeedBox}
import scalable.config.Config
import scalable.diode.AppState
import scalable.router.AppRouter

object AdminPage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any

  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]

  case class Props( proxy: ModelProxy[AppState], ctl: RouterCtl[AppRouter.Page])


  class Backend(bs: BackendScope[Props, Unit]) {
    val host: String = Config.AppConfig.apiHost


    def logout(props: Props): Callback ={
      props.proxy.value.partyId = Option.empty
      props.ctl.set(AppRouter.StartRoute)
    }


    def render(p: Props): VdomTagOf[Div] = {

      <.div(
        <.div(
          <.div(^.cls := "d-flex flex-row", ^.maxHeight := "50px", ^.width := "parent",
            <.img(^.cls := "img-fluid"
              ,^.borderRadius := 0.px
              ,^.width := "10%"
              ,^.src := "/images/scalable.svg"
              ,^.backgroundColor := "#C93A51"
              ,^.padding := "2px"),
            <.div( ^.height := "parent"
              ,^.width := "80%", // Playlist Row (Parent)
              ^.cls := "d-flex flex-column align-items-center justify-content-center"
              ,^.borderRadius := 0.px
              ,^.backgroundColor := "#C93A51",
              <.div(
               ^.textAlign := "center"
              ,^.cls := "h3 mb-0 align-middle"
              ,^.fontWeight := "700"
              , s"Enter room code to join: ${p.proxy.value.partyId.getOrElse("")}")),
            <.img(^.cls := "img-fluid"
              ,^.borderRadius := 0.px
              ,^.width := "10%"
              ,^.onClick --> logout(p)
              ,^.src := "/images/ic_exit_to_app_black_24px.svg"
              ,^.backgroundColor := "#C93A51"
              ,^.padding := "2px")
          ),
        <.div(^.cls := "d-flex flex-row mt-2",
          <.div(^.width := "48%", AdminTab(AdminTab.Props(p.proxy, p.ctl))),
          <.div(^.width := "4%"),
          <.div(^.width := "48%",PhotoFeedBox(PhotoFeedBox.Props(p.proxy, p.ctl, admin = true)))
        )
      )
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("AdminPage")
    .renderBackend[Backend]
    .build

}
