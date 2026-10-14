package model

import model.EventType._
import java.time.Instant
import java.util.UUID

sealed trait Event {
  def eventId: UUID
  def eventType: EventType
  def eventTime: Instant
}

case class AddToCartEvent(
    eventId: UUID,
    eventTime: Instant,
    productId: String,
    quantity: Int
) extends Event {

  override def eventType: EventType = AddToCart
}

case class ProductViewEvent(
    eventId: UUID,
    eventTime: Instant,
    productId: String
) extends Event {

  override def eventType: EventType = ProductView
}

case class PurchaseEvent(
    eventId: UUID,
    eventTime: Instant,
    items: List[PurchaseItem],
    totalPrice: BigDecimal
) extends Event {

  override def eventType: EventType = Purchase
}

case class RemoveFromCartEvent(
    eventId: UUID,
    eventTime: Instant,
    productId: String,
    quantity: Int
) extends Event {

  override def eventType: EventType = RemoveFromCart
}

case class SearchEvent(
    eventId: UUID,
    eventTime: Instant,
    query: String
) extends Event {

  override def eventType: EventType = Search
}
