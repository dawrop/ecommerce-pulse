package kafka

import io.circe.Json
import io.circe.syntax.EncoderOps
import model.EventEnvelope
import serialization.JsonFormats._

trait EventSerializer {
  def serialize(event: EventEnvelope): Json
}

class JsonEventSerializer extends EventSerializer {
  override def serialize(event: EventEnvelope): Json = {
    event.asJson
  }
}
