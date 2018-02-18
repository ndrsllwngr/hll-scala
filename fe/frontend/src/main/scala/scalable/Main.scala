package scalable

import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.WebpackRequire.React
import org.scalajs.dom

import scalable.router.AppRouter
import scalable.services.FirebaseService

object Main {

  def require(): Unit = {
    React
    ReactDOM
  }

  def main(args: Array[String]): Unit = {
    require()
    FirebaseService.init()
    val target = dom.document.getElementById("target")
    AppRouter.router().renderIntoDOM(target)
  }
}
