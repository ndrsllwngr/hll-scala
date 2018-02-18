package scalable.router

import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.html_<^._

import scalable.components.Layout
import scalable.diode.AppCircuit
import scalable.pages._

object AppRouter {
  sealed trait Page
  case object StartRoute extends Page
  case object JoinRoute extends Page
  case object CreateRoute extends Page
  case object CreateInfoRoute extends Page
  case object JoinAsAdminRoute extends Page
  case class AdminRoute(roomCode: String) extends Page
  case class GuestRoute(roomCode: String) extends Page

  val connection = AppCircuit.connect(_.state)

  val routerConfig = RouterConfigDsl[Page].buildConfig { dsl =>
    import dsl._
    (trimSlashes
      | staticRoute(root, StartRoute) ~> renderR(renderStartPage)
      | staticRoute("join", JoinRoute) ~> renderR(renderJoinPage)
      | staticRoute("adminjoin", JoinAsAdminRoute) ~> renderR(renderAdminJoinPage)
      | staticRoute("create", CreateRoute) ~> renderR(renderCreateRoomPage)
      | staticRoute("createInfo", CreateInfoRoute) ~> renderR(renderCreateInfoPage)
      | dynamicRouteCT(("admin"/ string(".*")).caseClass[AdminRoute]) ~> dynRenderR(renderAdminPage)
      | dynamicRouteCT(("guest"/ string(".*")).caseClass[GuestRoute]) ~> dynRenderR(renderGuestPage)

    )
      .notFound(redirectToPage(StartRoute)(Redirect.Replace))
      .renderWith(layout)
  }

  def renderStartPage(ctl: RouterCtl[Page]) = {
    connection(proxy => StartPage.Component(StartPage.Props(proxy, ctl)))
  }

  def renderJoinPage(ctl: RouterCtl[Page]) = {
    connection(proxy => JoinPage.Component(JoinPage.Props(proxy, ctl)))
  }

  def renderAdminJoinPage(ctl: RouterCtl[Page]) = {
    connection(proxy => AdminJoinPage.Component(AdminJoinPage.Props(proxy, ctl)))
  }

  def renderCreateRoomPage(ctl: RouterCtl[Page]) = {

    connection(proxy => CreatePage.Component(CreatePage.Props(proxy, ctl)))
  }

  def renderCreateInfoPage(ctl: RouterCtl[Page]) = {
    connection(proxy => CreateInfoPage.Component(CreateInfoPage.Props(proxy, ctl)))
  }

  def renderAdminPage(p: AdminRoute, ctl: RouterCtl[Page]) = {
    connection( proxy => {
      proxy.value.partyId = Some(p.roomCode)
      AdminPage.Component(AdminPage.Props(proxy, ctl))
    })
  }

  def renderGuestPage(p: GuestRoute, ctl: RouterCtl[Page]) = {
    connection(proxy => {
      proxy.value.partyId = Some(p.roomCode)
      GuestPage.Component(GuestPage.Props(proxy, ctl))
    })
  }


  def layout (c: RouterCtl[Page], r: Resolution[Page]) = connection(proxy => Layout(Layout.Props(proxy, c, r)))

  val baseUrl = BaseUrl.fromWindowOrigin_/

  val router = Router(baseUrl, routerConfig.logToConsole)
}
