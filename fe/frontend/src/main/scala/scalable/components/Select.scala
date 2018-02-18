package scalable.components

import japgolly.scalajs.react._

import scala.scalajs.js
import scala.scalajs.js.annotation._

object Select {
  @JSImport("react-select", JSImport.Default)
  @js.native
  object JsComp extends js.Any

  @JSImport("react-select/dist/react-select.css", JSImport.Namespace)
  @js.native
  object CSS extends js.Any
  CSS


  trait Options extends js.Object {
    val value: String
    val label: String
  }

  object Options {
    def apply(value: String, label: String) =  js.Dynamic.literal(value = value, label = label).asInstanceOf[Options]
  }


  trait Props extends js.Object {
    val name: String
    val options: js.Array[Options]
    val value: String
    val onInputChange: js.Function1[String, Unit]
    val onChange: js.Function1[Options, Unit]
    val placeholder : String
    val isLoading: Boolean
    val backspaceRemoves: Boolean
  }

  val Component = JsFnComponent[Props, Children.Varargs](JsComp)

  def props(
    pName: String,
    pOptions: js.Array[Options],
    pValue: String,
    pOnInputChange: js.Function1[String, Unit],
    pOnChange: js.Function1[Options, Unit],
    pPlaceholder : String,
    pIsLoading: Boolean = false
  ): Props = {
    new Props {
      val name: String = pName
      val options: js.Array[Options] = pOptions
      val value: String = pValue
      val onInputChange: js.Function1[String, Unit] = pOnInputChange
      val onChange: js.Function1[Options, Unit] = pOnChange
      val isLoading: Boolean = pIsLoading
      val placeholder: String = pPlaceholder
      val backspaceRemoves = false
    }
  }

  def apply(    pName: String,
                pOptions: js.Array[Options],
                pValue: String,
                pOnInputChange: js.Function1[String, Unit],
                pOnChange: js.Function1[Options, Unit],
                pPlaceholder : String,
                pIsLoading: Boolean = false
           ) = Component(props(pName, pOptions, pValue, pOnInputChange, pOnChange, pPlaceholder, pIsLoading))()
}
