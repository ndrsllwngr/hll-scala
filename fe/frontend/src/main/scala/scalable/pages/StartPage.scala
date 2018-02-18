package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ScalaComponent}
import org.scalajs.dom.html.Div

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scalable.config.Config
import scalable.diode._
import scalable.router.AppRouter
import scalable.services.RestService

object StartPage {

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

    def navigateToJoinPage(): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.JoinRoute)
    }

    def navigateToJoinAsAdminPage(): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.JoinAsAdminRoute)
    }

    def navigateToCreatePage(): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.CreateRoute)
    }

    def navigateToCreateInfoPage(): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.CreateInfoRoute)
    }

    def createRoom() : Callback = {
      val partyCreateResponseFuture = RestService.createParty("")
      partyCreateResponseFuture.map{x =>
        AppCircuit.dispatch(SetPartyCreateResponse(x))
        AppCircuit.dispatch(SetPartyId(x.id))
      }
      navigateToCreateInfoPage()
    }



    def render(props: Props): VdomTagOf[Div] = {
      val proxy = props.proxy()
      val dataTxtAttr = VdomAttr("data-text")
      val dataTogAttr = VdomAttr("data-toggle")
      <.div(
        ^.cls := "d-flex flex-column align-items-center justify-content-center",
        ^.height := "100vh",
        <.img( // LOGO
          ^.cls := "img-fluid mb-5 mt-0",
          ^.marginBottom := "35%",
          ^.flex := "0 0 auto",
          ^.maxWidth := 150.px,
          ^.src := "images/scalable.svg"
//          <.p(
//            ^.aria.label := "CodePen"
//            ,<.span(dataTxtAttr := "S","S")
//            ,<.span(dataTxtAttr := "C","C")
//            ,<.span(dataTxtAttr := "A","A")
//            ,<.span(dataTxtAttr := "L","L")
//            ,<.span(dataTxtAttr := "A","A")
//            ,<.span(dataTxtAttr := "B","B")
//            ,<.span(dataTxtAttr := "L","L")
//            ,<.span(dataTxtAttr := "E","E")
//          )
        ),
          <.button(^.`type` := "button", ^.cls := "btn btn-success btn-block",
            ^.onClick --> navigateToJoinPage() ,
            ^.maxWidth := 300.px,
            ^.borderRadius := "500px",
            ^.fontWeight := "700",
            ^.textTransform := "uppercase",
            ^.letterSpacing := "3px",
            ^.margin:= "0",
            "Join"
          ),
        <.div(^.cls := "mt-4 text-center",
          ^.textTransform := "uppercase",
          ^.letterSpacing := "8px",
          ^.color := "#fff",
      "Host party"),
          <.button(^.`type` := "button", ^.cls := "btn btn-light btn-block mt-2",
            ^.onClick --> navigateToJoinPage() ,
            ^.maxWidth := 300.px,
            ^.borderRadius := "500px",
            ^.fontWeight := "700",
            ^.textTransform := "uppercase",
            ^.letterSpacing := "3px",
            ^.margin:= "0",
            ^.onClick --> navigateToJoinAsAdminPage(),
            "Rejoin"
        ),
        <.button(^.`type` := "button", ^.cls := "btn btn-light btn-block mt-2",
          ^.onClick --> navigateToJoinPage() ,
          ^.maxWidth := 300.px,
          ^.borderRadius := "500px",
          ^.fontWeight := "700",
          ^.textTransform := "uppercase",
          ^.letterSpacing := "3px",
          ^.margin:= "0",
          ^.onClick --> createRoom(),
          "Create"
        )
      )
    }
  }

  val Component = ScalaComponent.builder[Props]("StartPage")
    .renderBackend[Backend]
    .build

}
