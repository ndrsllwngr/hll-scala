package scalable.config

import scala.scalajs.js
import scala.scalajs.js.annotation._

object Config {
  @js.native
  @JSGlobal("appConfig")
  object AppConfig extends js.Object {
    val apiHost: String = js.native
  }
}
