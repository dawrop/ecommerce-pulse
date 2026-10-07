import actors.SessionActor
import generator._
import org.apache.pekko.actor.{ActorSystem, Props}
import uitils.{DefaultSessionStateUpdater, RandomEventSelector}

object GeneratorApp {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem =
      ActorSystem("GeneratorSystem")

    val userGenerator    = new UserGeneratorImpl
    val sessionGenerator = new SessionGeneratorImpl

    val user    = userGenerator.generate()
    val session = sessionGenerator.generate(user)
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

    system.actorOf(
      Props(new SessionActor(session, eventSelector, stateUpdater)),
      "session-actor"
    )
  }
}
