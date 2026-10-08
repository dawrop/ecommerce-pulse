import actors.SessionActor
import generator._
import kafka.{JsonEventSerializer, KafkaEventProducer}
import org.apache.pekko.actor.{ActorSystem, Props}
import uitils.{DefaultSessionStateUpdater, RandomEventSelector}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.io.StdIn

object GeneratorApp {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem =
      ActorSystem("GeneratorSystem")

    val userGenerator    = new UserGeneratorImpl
    val sessionGenerator = new SessionGeneratorImpl

    val user           = userGenerator.generate()
    val session        = sessionGenerator.generate(user)
    val productCatalog = new ProductCatalogGenerator().generate()

    val eventSelector = new RandomEventSelector(
      new AddToCartEventGenerator,
      new ProductViewEventGenerator,
      new PurchaseEventGenerator(productCatalog),
      new RemoveFromCartEventGenerator,
      new SearchEventGenerator
    )

    val stateUpdater =
      new DefaultSessionStateUpdater

    val kafkaProducer =
      new KafkaEventProducer(serializer = new JsonEventSerializer)

    system.actorOf(
      Props(
        new SessionActor(session, eventSelector, stateUpdater, kafkaProducer)
      ),
      "session-actor"
    )

    println("Pekko App is running. Press ENTER to stop...")
    StdIn.readLine()
    println("Shutting down Pekko System gracefully...")
    system.terminate()
    Await.result(system.whenTerminated, 10.seconds)
    println("Pekko App stopped. Returning to sbt.")
  }
}
