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
import scalable.diode._
import scalable.router.AppRouter
import scalable.services.RestService

object JoinPage {

  @js.native
  @JSImport("lodash.throttle", JSImport.Default)
  private object _throttle extends js.Any
  def throttle: js.Dynamic = _throttle.asInstanceOf[js.Dynamic]


  case class Props (
                     proxy: ModelProxy[AppState],
                     ctl: RouterCtl[AppRouter.Page]
                   )

  case class JoinState( var input : String)


  class Backend(bs: BackendScope[Props, JoinState]) {
    val host: String = Config.AppConfig.apiHost


    def join (partyID: String) : Callback = {
      if(!partyID.isEmpty) {
        val partyExistsBooleanFuture = RestService.joinParty(partyID)
        val futureCallback = partyExistsBooleanFuture.map { exists =>
          if (exists) {
            AppCircuit.dispatch(SetPartyId(partyID))
            navigateToHomePage()
          } else
            Callback.alert("PartyCode does not exist")
        }
        Callback.future(futureCallback)
      } else
      Callback.alert("Room Code may not be empty")
    }

    def onTextChange(roomCodeState: JoinState)(e: ReactEventFromInput) = Callback {
      roomCodeState.input = e.target.value
    }

    def navigateToHomePage(): Callback = bs.props.flatMap {
      props =>{
      RestService.getSongs(props.proxy.value.partyId.get).map{songs =>
        AppCircuit.dispatch(SetSongsForParty(songs))
      }
        props.ctl.set(AppRouter.GuestRoute( props.proxy.value.partyId.get))
      }
    }

    def render(p: Props, s: JoinState): VdomTagOf[Div] = {
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
            ,<.span(dataTxtAttr := "G","G")
            ,<.span(dataTxtAttr := "U","U")
            ,<.span(dataTxtAttr := "E","E")
            ,<.span(dataTxtAttr := "S","S")
            ,<.span(dataTxtAttr := "T","T")
          )
        ),
        <.input(^.`type` := "text", placeHolderAttr := "Enter room code",  ^.cls := "form-control",
          ^.maxWidth := 300.px,
        ^.borderRadius := "500px",
              ^.onChange ==> onTextChange(s)),
            <.button(^.`type` := "button", ^.cls := "btn btn-success btn-block mt-2",
              ^.onClick --> join(s.input),
              ^.maxWidth := 300.px,
              ^.borderRadius := "500px",
              ^.fontWeight := "700",
              ^.textTransform := "uppercase",
              ^.letterSpacing := "3px",
              ^.margin:= "0",
              "Join"
        )
      )
    }

  }

  val Component = ScalaComponent.builder[Props]("JoinPage")
    .initialState(JoinState(input = ""))
    .renderBackend[Backend]
    .build

}
