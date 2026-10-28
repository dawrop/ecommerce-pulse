package utils

import generator.{EventGenerator, Utils}
import model.EventType.{AddToCart, ProductView, Search}
import model.{SessionState, UserPersona}

trait EventSelector {
  def select(session: SessionState): EventGenerator
}

class AdvancedDynamicEventSelector(
    addToCartGen: EventGenerator,
    productViewGen: EventGenerator,
    purchaseGen: EventGenerator,
    removeFromCartGen: EventGenerator,
    searchGen: EventGenerator
) extends EventSelector {

  override def select(session: SessionState): EventGenerator = {
    session.user.persona match {
      case UserPersona.RegularShopper => selectForRegularShopper(session)
      case UserPersona.ImpulseBuyer   => selectForImpulseBuyer(session)
      case UserPersona.WindowShopper  => selectForWindowShopper(session)
      case UserPersona.BargainHunter  => selectForBargainHunter(session)
      case UserPersona.ScraperBot     => selectForScraperBot()
    }
  }

  private def selectForRegularShopper(session: SessionState): EventGenerator = {
    val cartSize = session.cart.values.sum

    val purchaseWeight = if (cartSize >= 3) 25 else if (cartSize > 0) 10 else 0
    val removeWeight   = if (cartSize > 0) 5 else 0

    val weights = declareWeights(
      addToCart = 15,
      productView = 40,
      purchase = purchaseWeight,
      removeFromCart = removeWeight,
      search = 35
    )

    session.lastEvent match {
      case Some(Search) => productViewGen
      case Some(ProductView) if Utils.random.nextDouble() < 0.35 => addToCartGen
      case _                                                     =>
        Utils.selectByWeight(weights, fallback = productViewGen)
    }
  }

  private def selectForImpulseBuyer(session: SessionState) = {
    val cartSize = session.cart.values.sum

    val addToCartWeight = if (session.lastEvent.contains(ProductView)) 35 else 0
    val purchaseWeight  = if (cartSize > 0) 15 else 0

    val weights = declareWeights(
      addToCart = addToCartWeight,
      productView = 40,
      purchase = purchaseWeight,
      removeFromCart = 0,
      search = 10
    )

    Utils.selectByWeight(weights, fallback = productViewGen)
  }

  private def selectForWindowShopper(session: SessionState) = {
    val cartSize = session.cart.values.sum

    if (cartSize > 0 && Utils.random.nextDouble() < 0.05) purchaseGen
    else if (Utils.random.nextDouble() < 0.5) productViewGen
    else searchGen
  }

  private def selectForBargainHunter(session: SessionState): EventGenerator = {
    val cartSize = session.cart.values.sum

    val purchaseWeight = if (cartSize > 0) 8 else 0
    val removeWeight   = if (cartSize > 0) 2 else 0

    val weights = declareWeights(
      addToCart = 20,
      productView = 40,
      purchase = purchaseWeight,
      removeFromCart = removeWeight,
      search = 30
    )

    session.lastEvent match {
      case Some(AddToCart) if cartSize > 1 && Utils.random.nextDouble() < 0.4 =>
        removeFromCartGen
      case _ =>
        Utils.selectByWeight(weights, fallback = productViewGen)
    }
  }

  private def selectForScraperBot() = {
    if (Utils.random.nextDouble() < 0.85) productViewGen else searchGen
  }

  private def declareWeights(
      addToCart: Int,
      productView: Int,
      purchase: Int,
      removeFromCart: Int,
      search: Int
  ): List[(EventGenerator, Int)] = {
    List(
      addToCartGen      -> addToCart,
      productViewGen    -> productView,
      purchaseGen       -> purchase,
      removeFromCartGen -> removeFromCart,
      searchGen         -> search
    )
  }
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
