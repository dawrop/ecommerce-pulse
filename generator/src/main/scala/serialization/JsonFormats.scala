package serialization

import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax.EncoderOps
import io.circe.{Encoder, JsonObject}
import model._

import java.time.Instant
import java.util.UUID

object JsonFormats {
  // encoders
  implicit val UuidEncoder: Encoder[UUID] =
    Encoder.encodeString.contramap[UUID](_.toString)

  implicit val instantEncoder: Encoder[Instant] =
    Encoder.encodeString.contramap[Instant](_.toString)

  implicit val purchaseItemEncoder: Encoder[PurchaseItem] =
    deriveEncoder[PurchaseItem]

  implicit val eventTypeEncoder: Encoder[EventType] =
    Encoder.encodeString.contramap {
      case EventType.AddToCart      => "add_to_cart"
      case EventType.ProductView    => "product_view"
      case EventType.Purchase       => "purchase"
      case EventType.RemoveFromCart => "remove_from_cart"
      case EventType.Search         => "search"
    }

  implicit val eventEncoder: Encoder[Event] =
    Encoder.instance { e =>
      eventJsonObject(e).asJson
    }

  implicit val eventEnvelopeEncoder: Encoder[EventEnvelope] =
    Encoder.instance { envelope =>
      eventJsonObject(envelope.event)
        .deepMerge(sessionMetadata(envelope))
        .asJson
    }

  // private helpers
  private def eventJsonObject(e: Event): JsonObject = {
    specificFields(e).deepMerge(commonFields(e))
  }

  private def commonFields(e: Event): JsonObject =
    JsonObject(
      "event_id"   -> e.eventId.asJson,
      "event_type" -> e.eventType.asJson,
      "event_time" -> e.eventTime.asJson
    )

  private def specificFields(e: Event): JsonObject =
    e match {
      case ev: AddToCartEvent =>
        JsonObject(
          "product_id" -> ev.productId.asJson,
          "quantity"   -> ev.quantity.asJson
        )

      case ev: ProductViewEvent =>
        JsonObject(
          "product_id" -> ev.productId.asJson
        )

      case ev: PurchaseEvent =>
        JsonObject(
          "items"       -> ev.items.asJson,
          "total_price" -> ev.totalPrice.asJson
        )

      case ev: RemoveFromCartEvent =>
        JsonObject(
          "product_id" -> ev.productId.asJson,
          "quantity"   -> ev.quantity.asJson
        )

      case ev: SearchEvent =>
        JsonObject(
          "query" -> ev.query.asJson
        )
    }

  private def sessionMetadata(envelope: EventEnvelope): JsonObject =
    JsonObject(
      "user_id"    -> envelope.userId.asJson,
      "session_id" -> envelope.sessionId.asJson
    )
}
