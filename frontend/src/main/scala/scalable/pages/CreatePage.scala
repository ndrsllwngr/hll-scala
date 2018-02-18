package scalable.pages

import diode.react.ModelProxy
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{<, ^, _}
import japgolly.scalajs.react.{BackendScope, Callback, ReactEventFromInput, ScalaComponent}
import org.scalajs.dom.html.Div

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scalable.config.Config
import scalable.diode.{AppCircuit, AppState, SetPartyCreateResponse, SetPartyId}
import scalable.router.AppRouter
import scalable.services.RestService

object CreatePage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any

  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props(
                    proxy: ModelProxy[AppState],
                    ctl: RouterCtl[AppRouter.Page]
                  )

  case class CreateState(var input: String)


  class Backend(bs: BackendScope[Props, CreateState]) {
    val host: String = Config.AppConfig.apiHost




    def createRoom(input: String) : Callback = {
      val partyCreateResponseFuture = RestService.createParty(input)
      partyCreateResponseFuture.map{x =>
        AppCircuit.dispatch(SetPartyCreateResponse(x))
        AppCircuit.dispatch(SetPartyId(x.id))
      }
      navigateToCreateInfoPage()
    }

    def onTextChange(roomCodeState: CreateState)(e: ReactEventFromInput) = Callback {
      roomCodeState.input = e.target.value

    }

    def navigateToCreateInfoPage(): Callback = bs.props.flatMap { props =>
      props.ctl.set(AppRouter.CreateInfoRoute)
    }

    def render(p: Props, s: CreateState): VdomTagOf[Div] = {
      val proxy = p.proxy()
      val dataTxtAttr = VdomAttr("data-text")
      val placeHolderAttr = VdomAttr("placeholder")
      <.div(
        ^.cls := "d-flex flex-column align-items-center justify-content-center",
        ^.height := "100vh",
        <.div( // LOGO
          ^.cls := "peeledLogo d-flex flex-row align-items-center mb-5 mt-0",
          ^.flex := "0 0 auto",
          <.p(
            ^.aria.label := "CodePen"
            ,<.span(dataTxtAttr := "H","H")
            ,<.span(dataTxtAttr := "O","O")
            ,<.span(dataTxtAttr := "S","S")
            ,<.span(dataTxtAttr := "T","T")
          )
        ),
        <.input(^.`type` := "text", placeHolderAttr := "Enter a room name",  ^.cls := "form-control",
          ^.maxWidth := 300.px,
          ^.borderRadius := "500px",
          ^.onChange ==> onTextChange(s)),
        <.button(^.`type` := "button", ^.cls := "btn btn-success btn-block mt-2",
          ^.onClick --> createRoom(s.input),
          ^.maxWidth := 300.px,
          ^.borderRadius := "500px",
          ^.fontWeight := "700",
          ^.textTransform := "uppercase",
          ^.letterSpacing := "3px",
          ^.margin:= "0",
          "Create"
        )
      )
    }
  }

  val Component = ScalaComponent.builder[Props]("CreatePage")
    .initialState(CreateState(input = ""))
    .renderBackend[Backend]
    .build

}
