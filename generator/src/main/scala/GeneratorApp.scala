import actors.SessionManagerActor
import generator._
import kafka.{KafkaEventProducer, StringEventSerializer}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.pekko.actor.{ActorSystem, Props}
import utils.{
  AdvancedDynamicEventSelector,
  DefaultSessionStateUpdater,
}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}

object GeneratorApp {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem =
      ActorSystem("GeneratorSystem")

    val userGenerator    = new UserGeneratorImpl
    val sessionGenerator = new SessionGeneratorImpl
    val productCatalog   = new ProductCatalogGenerator().generate()

    val eventSelector = new AdvancedDynamicEventSelector(
      new AddToCartEventGenerator,
      new ProductViewEventGenerator,
      new PurchaseEventGenerator(productCatalog),
      new RemoveFromCartEventGenerator,
      new SearchEventGenerator
    )

    val stateUpdater = new DefaultSessionStateUpdater

    val kafkaTopic =
      Utils.generatorConfig.getConfig("kafka").getString("events-topic")
    val kafkaProducer =
      new KafkaEventProducer[String](
        system = system,
        serializer = new StringEventSerializer(),
        kafkaSerializer = new StringSerializer,
        topic = kafkaTopic
      )

    system.actorOf(
      Props(
        new SessionManagerActor(
          targetActiveSessions = 15,
          userGenerator,
          sessionGenerator,
          eventSelector,
          stateUpdater,
          kafkaProducer
        )
      ),
      "session-manager-actor"
    )

    sys.addShutdownHook {
      println("Closing the application. Flushing the Kafka buffer.")
      val closed = kafkaProducer.close()
      Await.result(closed, 10.seconds)
      println("Kafka producer closed successfully.")
    }

    println("Pekko App is running.")
    Await.result(system.whenTerminated, Duration.Inf)
  }
}
