package model

case class EventEnvelope(
    userId: String,
    sessionId: String,
    event: Event
)
