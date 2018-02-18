package scalable.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scalable.models._
import scalable.services._


object VoteComp {

  case class Props (voteAble: VoteAble,  admin: Boolean)

  case class State (var voted: Boolean)

  class Backend(bs: BackendScope[Props, State]) {


    def vote(props: Props, positive: Boolean, state: State) : Callback = Callback {
      if(!state.voted) {
        state.voted = true
        RestService.addPartyVote(props.voteAble.partyID, props.voteAble.compId, positive, props.voteAble.voteType).onComplete{
          case Success(_) => state.voted = true; createCookie(props.voteAble, state);
          case Failure(_) => state.voted = false
        }
      }
    }

    def delete(props: Props) : Callback = Callback{
        DeleteService.delete(props.voteAble)
    }

    def colorGreen(voteAble: VoteAble) : Boolean = {
        if(calcTotal(voteAble) > 0){
          true
        } else {
          false
        }
    }
    def colorRed(voteAble: VoteAble) : Boolean = {
      if(calcTotal(voteAble) < 0){
        true
      } else {
        false
      }
    }
    def calcTotal(voteAble: VoteAble) : Int = {
      voteAble.upvotes - voteAble.downvotes
    }


    def checkForExistingCookie(voteAble: VoteAble, state: State) : Unit = {
      val cookie = dom.document.cookie
      state.voted = cookie.contains(s"${voteAble.partyID}/${voteAble.voteType}/${voteAble.compId}") &&  cookie.contains("true")

    }

    def createCookie(voteAble: VoteAble, state: State) : Unit = {
      dom.document.cookie = s"${voteAble.compId}=${voteAble.partyID}/${voteAble.voteType}/${voteAble.compId}/${state.voted}"
    }

    def renderVoteCompAsAdminOrGuest(props: Props, state: State) : VdomElement = {
      if(props.admin){
        <.div(
        <.div( ^.key := props.voteAble.compId,
          ^.classSet(
            "p-2 text-center align-self-center" -> true),
          calcTotal(props.voteAble).toString
        ),
          <.button(
            ^.cls := "btn btn-link align-self-center",
            ^.onClick --> delete(props),
            <.img(
              ^.alt := "upvote",
              ^.src := "/images/ic_clear_black_24px.svg"
            )
        )
        )
      } else {
        <.div(
        <.div(
          ^.cls := "align-self-center",
          <.button(
            ^.cls := "btn btn-link",
            ^.onClick --> vote(props, positive = true, state),
            ^.disabled := state.voted,
            <.img(
              ^.alt := "upvote",
              ^.src := "/images/ic_expand_less_black_24px.svg"
            )
          )
        ),
        <.div(
          ^.classSet(
            "p-2 text-center align-self-center" -> true),
          ^.classSet(
            "text-success" -> colorGreen(props.voteAble),
            "text-danger" -> colorRed(props.voteAble)),
          calcTotal(props.voteAble).toString
        ),
        <.div(
          ^.cls := "align-self-center",
          <.button(
            ^.cls := "btn btn-link",
            ^.onClick --> vote(props, positive = false, state),
            ^.disabled := state.voted,
            <.img(
              ^.alt := "downvote",
              ^.src := "/images/ic_expand_more_black_24px.svg"
            )
          )
        )
        )
      }
    }

    def render(props: Props, state: State): VdomElement = {
      checkForExistingCookie(props.voteAble, state)
      <.div(
        ^.cls := "d-flex flex-column align-items-center align-self-center",
        renderVoteCompAsAdminOrGuest(props,state)
        )
    }
  }

  val Component = ScalaComponent.builder[Props]("VoteComp")
    .initialState(State(false))
    .renderBackend[Backend]
    .build

  def apply(props: Props) = Component(props)
}
