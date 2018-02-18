package scalable.components

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.{Resolution, RouterCtl}
import japgolly.scalajs.react.vdom.html_<^._

import scalable.config.Config
import scalable.diode.AppCircuit.connect
import scalable.diode._
import scalable.router.AppRouter.Page

object Layout {
  val connection = connect(_.state)

  case class Props(
                    proxy: ModelProxy[AppState],
                    ctl: RouterCtl[Page],
                    resolution: Resolution[Page]
                  )

  class Backend(bs: BackendScope[Props, Unit]) {
    val host: String = Config.AppConfig.apiHost


    def mounted: Callback = Callback.log("Mounted PlayListPage!")

    def render(props: Props): VdomElement = {
      <.div(
        <.div(
          ^.cls := "blurBg"
          //TODO Background
        ),
        <.div(^.cls := "container", props.resolution.render())
        //TODO
      )
    }
  }

  val Component = ScalaComponent.builder[Props]("Layout")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted)
    .build

  def apply(props: Props) = Component(props)
}
