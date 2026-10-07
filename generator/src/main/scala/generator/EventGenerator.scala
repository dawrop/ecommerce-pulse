package generator

import generator.Utils._
import model._

import java.time.Instant

sealed trait EventGenerator {
  def generate(session: SessionState): Event
  protected def eventId: String =
    randomUUID()
  protected def eventTime: Instant =
    Instant.now()
}

class AddToCartEventGenerator extends EventGenerator {
  override def generate(session: SessionState): Event = {
    val productId =
      if (session.viewedProducts.nonEmpty)
        randomElement(session.viewedProducts.toVector)
      else randomProduct()

    AddToCartEvent(
      eventId = eventId,
      eventTime = eventTime,
      productId = productId,
      quantity = randomQuantity(10)
    )
  }
}

class ProductViewEventGenerator extends EventGenerator {
  override def generate(session: SessionState): Event =
    ProductViewEvent(
      eventId = eventId,
      eventTime = eventTime,
      productId = randomProduct()
    )
}
class PurchaseEventGenerator(productCatalog: ProductCatalog)
    extends EventGenerator {
  override def generate(session: SessionState): Event = {
    val items = session.cart.toList.map { case (productId, quantity) =>
      val product = productCatalog.get(productId)
      PurchaseItem(
        productId,
        quantity,
        product.price
      )
    }
    val total = items.map { item =>
      item.unitPrice * item.quantity
    }.sum

    PurchaseEvent(
      eventId = eventId,
      eventTime = eventTime,
      items = items,
      totalPrice = total
    )
  }
}
class RemoveFromCartEventGenerator extends EventGenerator {
  override def generate(session: SessionState): Event = {
    val productId         = randomElement(session.cart.keys.toVector)
    val availableQuantity = session.cart(productId)

    RemoveFromCartEvent(
      eventId = eventId,
      eventTime = eventTime,
      productId = productId,
      quantity = randomQuantity(availableQuantity)
    )
  }
}
class SearchEventGenerator extends EventGenerator {
  override def generate(session: SessionState): Event =
    SearchEvent(
      eventId = eventId,
      eventTime = eventTime,
      query = randomSearchQuery()
    )
}
