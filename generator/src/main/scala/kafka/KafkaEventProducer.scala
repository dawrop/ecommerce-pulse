package kafka

import model.EventEnvelope
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}
import org.apache.pekko.Done
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.kafka.ProducerSettings
import org.apache.pekko.kafka.scaladsl.Producer
import org.apache.pekko.stream.scaladsl.{Keep, Source}
import org.apache.pekko.stream.{
  Materializer,
  OverflowStrategy,
  QueueOfferResult
}

import scala.concurrent.Future

class KafkaEventProducer[T](
    system: ActorSystem,
    serializer: EventSerializer[T],
    kafkaSerializer: Serializer[T],
    topic: String
) {
  private implicit val materializer: Materializer =
    Materializer(system)

  private val producerSettings =
    ProducerSettings(
      system,
      new StringSerializer,
      kafkaSerializer
    )

  private val (queue, completion) =
    Source
      .queue[EventEnvelope](
        bufferSize = 1_000,
        overflowStrategy = OverflowStrategy.backpressure
      )
      .map { event =>
        val payload: T = serializer.serialize(event)

        new ProducerRecord[String, T](
          topic,
          event.sessionId,
          payload
        )
      }
      .toMat(Producer.plainSink(producerSettings))(Keep.both)
      .run()

  def send(eventEnvelope: EventEnvelope): Future[QueueOfferResult] =
    queue.offer(eventEnvelope)

  def close(): Future[Done] = {
    queue.complete()
    completion
  }

  def fail(cause: Throwable): Future[Done] = {
    queue.fail(cause)
    completion
  }
}
