package actors

import generator.{SessionGenerator, UserGenerator}
import kafka.KafkaEventProducer
import org.apache.pekko.actor.{Actor, ActorLogging, Props, Terminated}
import utils.{EventSelector, SessionStateUpdater}

class SessionManagerActor(
    targetActiveSessions: Int,
    userGenerator: UserGenerator,
    sessionGenerator: SessionGenerator,
    eventSelector: EventSelector,
    stateUpdater: SessionStateUpdater,
    kafkaProducer: KafkaEventProducer[_]
) extends Actor
    with ActorLogging {

  override def preStart(): Unit = {
    log.info(
      s"Starting SessionManager with target concurrency: $targetActiveSessions"
    )
    for (_ <- 1 to targetActiveSessions) {
      spawnNewSession()
    }
  }

  override def receive: Receive = { case Terminated(actorRef) =>
    log.debug(s"Session actor $actorRef terminated. Spawning a new one.")
    context.unwatch(actorRef)
    spawnNewSession()
  }

  private def spawnNewSession(): Unit = {
    val user         = userGenerator.generate()
    val session      = sessionGenerator.generate(user)
    val sessionActor = context.actorOf(
      Props(
        new SessionActor(session, eventSelector, stateUpdater, kafkaProducer)
      )
    )

    context.watch(sessionActor)
  }
}
