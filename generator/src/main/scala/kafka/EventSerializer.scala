package kafka

import io.circe.Json
import io.circe.syntax.EncoderOps
import model.EventEnvelope
import serialization.JsonFormats._

import java.nio.charset.StandardCharsets

trait EventSerializer[T] {
  def serialize(event: EventEnvelope): T
}

class JsonEventSerializer extends EventSerializer[Json] {
  override def serialize(event: EventEnvelope): Json =
    event.asJson
}

class StringEventSerializer extends EventSerializer[String] {
  override def serialize(event: EventEnvelope): String =
    event.asJson.noSpaces
}

class ByteArrayEventSerializer extends EventSerializer[Array[Byte]] {
  override def serialize(event: EventEnvelope): Array[Byte] =
    event.asJson.noSpaces.getBytes(StandardCharsets.UTF_8)
}
