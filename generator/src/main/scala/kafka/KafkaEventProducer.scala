package kafka

import model.EventEnvelope

class KafkaEventProducer(serializer: EventSerializer) {

  def publish(event: EventEnvelope): Unit = {
    val json = serializer.serialize(event)

    println(json.spaces2)
  }
}
