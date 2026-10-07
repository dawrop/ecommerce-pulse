package model

import java.time.Instant

sealed trait Event {
  def eventType: String
  def eventId: String
  def eventTime: Instant
}

case class AddToCartEvent(
    eventId: String,
    eventTime: Instant,
    productId: String,
    quantity: Int
) extends Event {

  override def eventType: String = "add_to_cart"
}

case class ProductViewEvent(
    eventId: String,
    eventTime: Instant,
    productId: String
) extends Event {

  override def eventType: String = "product_view"
}

case class PurchaseEvent(
    eventId: String,
    eventTime: Instant,
    items: List[PurchaseItem],
    totalPrice: BigDecimal
) extends Event {

  override def eventType: String = "purchase"
}

case class RemoveFromCartEvent(
    eventId: String,
    eventTime: Instant,
    productId: String,
    quantity: Int
) extends Event {

  override def eventType: String = "remove_from_cart"
}

case class SearchEvent(
    eventId: String,
    eventTime: Instant,
    query: String
) extends Event {

  override def eventType: String = "search"
}
