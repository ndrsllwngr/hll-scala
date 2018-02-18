package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.scalajs.dom.html.Div

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scalable.components.{PhotoFeedTab, PlaylistTab}
import scalable.config.Config
import scalable.diode.AppState
import scalable.router.AppRouter

object GuestPage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any

  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props( proxy: ModelProxy[AppState], ctl: RouterCtl[AppRouter.Page])

  case class State( var pageIndex: Int )

  class Backend(bs: BackendScope[Props, State]) {
    val host: String = Config.AppConfig.apiHost

    def navigateToTab(index: Int): Callback = {
      bs.modState(s =>{
        s.pageIndex = index
        s
      }).runNow()
      Callback.empty
    }

    def logout(props: Props): Callback ={
      props.proxy.value.partyId = Option.empty
      props.ctl.set(AppRouter.StartRoute)
    }


    def render(p: Props, state: State): VdomTagOf[Div] = {
      var tabContent : VdomTagOf[Div] = <.div(PlaylistTab(PlaylistTab.Props(p.proxy, p.ctl, admin = false)))
      state.pageIndex match{
        case 0 => tabContent = <.div(PlaylistTab(PlaylistTab.Props(p.proxy, p.ctl, admin = false)))
        case 1 => tabContent = <.div(PhotoFeedTab(PhotoFeedTab.Props(p.proxy, p.ctl, admin = false)))
      }

      <.div(
          <.div(^.cls := "d-flex flex-row", ^.maxHeight := "50px", ^.width := "parent",
            <.img(^.cls := "img-fluid"
              ,^.borderRadius := 0.px
              ,^.width := "10vw"
              ,^.src := "/images/scalable.svg"
              ,^.backgroundColor := "#C93A51"
              ,^.padding := "2px"),
            <.button(
              ^.`type` := "button"
              ,^.fontWeight := "700"
              ,^.textTransform := "uppercase"
              ,^.letterSpacing := "3px"
              ,^.margin:= "0"
              ,^.borderRadius := 0.px
              ,^.border := "none"
              ,^.width := "40vw"
              ,^.onClick --> navigateToTab(0)
              ,^.backgroundColor := "#C93A51"
              ,"Music"),
            <.button(^.`type` := "button"
              ,^.fontWeight := "700"
              ,^.textTransform := "uppercase"
              ,^.letterSpacing := "3px"
              ,^.margin:= "0"
              ,^.borderRadius := 0.px
              ,^.border := "none"
              ,^.width := "40vw"
              ,^.backgroundColor := "#C93A51"
              ,^.onClick --> navigateToTab(1)
              ,"Photos"),
            <.img(^.cls := "img-fluid"
              ,^.borderRadius := 0.px
              ,^.width := "10vw"
              ,^.onClick --> logout(p)
              ,^.src := "/images/ic_exit_to_app_black_24px.svg"
              ,^.backgroundColor := "#C93A51"
              ,^.padding := "2px")
          ), tabContent
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("GuestPage")
    .initialState(State(0))
    .renderBackend[Backend]
    .build

}
