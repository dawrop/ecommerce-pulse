package uitils

import generator.{EventGenerator, Utils}
import model.SessionState

trait EventSelector {
  def select(session: SessionState): EventGenerator
}

class RandomEventSelector(
    addToCartGen: EventGenerator,
    productViewGen: EventGenerator,
    purchaseGen: EventGenerator,
    removeFromCartGen: EventGenerator,
    searchGen: EventGenerator
) extends EventSelector {

  override def select(session: SessionState): EventGenerator = {
    val generators =
      Vector(
        addToCartGen,
        productViewGen,
        searchGen
      ) ++
        Option.when(session.cart.nonEmpty)(removeFromCartGen) ++
        Option.when(session.cart.nonEmpty)(purchaseGen)

    Utils.randomElement(generators)
  }
}
