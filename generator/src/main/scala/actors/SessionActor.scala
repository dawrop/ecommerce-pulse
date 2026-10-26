package actors

import kafka.KafkaEventProducer
import model.{EventEnvelope, SessionState}
import org.apache.pekko.actor.{Actor, ActorLogging}
import org.apache.pekko.stream.QueueOfferResult
import utils.{EventSelector, SessionStateUpdater}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

case object GenerateEvent

class SessionActor(
    initialState: SessionState,
    eventSelector: EventSelector,
    stateUpdater: SessionStateUpdater,
    kafkaEventProducer: KafkaEventProducer[_],
    maxEvents: Int = 10
) extends Actor
    with ActorLogging {

  implicit val ec: ExecutionContext = context.dispatcher

  override def preStart(): Unit = self ! GenerateEvent

  override def receive: Receive = running(initialState, eventCount = 0)

  private def running(state: SessionState, eventCount: Int): Receive = {
    case GenerateEvent =>
      if (eventCount >= maxEvents) {
        log.info(
          s"Session ${state.sessionId} for user ${state.user.userId} completed."
        )
        context.stop(self)
      } else {
        val generator     = eventSelector.select(state)
        val event         = generator.generate(state)
        val eventEnvelope =
          EventEnvelope(
            userId = state.user.userId,
            sessionId = state.sessionId,
            event = event
          )

        val newState = stateUpdater.update(state, event)

        kafkaEventProducer.send(eventEnvelope).onComplete {
          case Success(QueueOfferResult.Enqueued) =>
            log.debug(
              s"Event successfully enqueued for session ${state.sessionId}"
            )

          case Success(QueueOfferResult.Dropped) =>
            log.warning(
              s"Kafka queue buffer is FULL! Event dropped for session ${state.sessionId}"
            )

          case Success(QueueOfferResult.QueueClosed) =>
            log.error("Kafka event queue was closed!")

          case Success(QueueOfferResult.Failure(cause)) =>
            log.error(cause, "Failed to enqueue event to Kafka")

          case Failure(ex) =>
            log.error(
              ex,
              "Exception occurred while offering event to Kafka queue"
            )
        }

        context.system.scheduler.scheduleOnce(
          1.second,
          self,
          GenerateEvent
        )(context.dispatcher)

        context.become(running(newState, eventCount + 1))
      }

  }
}
