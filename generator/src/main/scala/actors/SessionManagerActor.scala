package actors

import io.circe.syntax.EncoderOps
import model.{EventEnvelope, SessionState}
import org.apache.pekko.actor.Actor
import serialization.JsonFormats._
import uitils.{EventSelector, SessionStateUpdater}

import scala.concurrent.duration.DurationInt

case object GenerateEvent

class SessionActor(
    initialState: SessionState,
    eventSelector: EventSelector,
    stateUpdater: SessionStateUpdater
) extends Actor {

  override def preStart(): Unit = self ! GenerateEvent

  override def receive: Receive = running(initialState)

  private def running(state: SessionState): Receive = { case GenerateEvent =>
    val generator     = eventSelector.select(state)
    val event         = generator.generate(state)
    val eventEnvelope =
      EventEnvelope(
        userId = state.user.userId,
        sessionId = state.sessionId,
        event = event
      )

    val newState = stateUpdater.update(state, event)

    val json = eventEnvelope.asJson.spaces2
    println(json)

    context.system.scheduler.scheduleOnce(
      1.second,
      self,
      GenerateEvent
    )(context.dispatcher)

    context.become(running(newState))
  }
}
