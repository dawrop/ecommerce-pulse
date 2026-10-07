package serialization

import io.circe.Encoder
import io.circe.generic.semiauto.{deriveCodec, deriveEncoder}
import model._

import java.time.Instant

object JsonFormats {
  implicit val instantEncoder: Encoder[Instant] =
    Encoder.encodeString.contramap[Instant](_.toString)

  implicit val eventEncoder: Encoder[Event] =
    deriveEncoder[Event]

  implicit val addToCartEventEncoder: Encoder[AddToCartEvent] =
    deriveEncoder[AddToCartEvent]

  implicit val productViewEventEncoder: Encoder[ProductViewEvent] =
    deriveEncoder[ProductViewEvent]

  implicit val purchaseEventEncoder: Encoder[PurchaseEvent] =
    deriveEncoder[PurchaseEvent]

  implicit val removeFromCartEventEncoder: Encoder[RemoveFromCartEvent] =
    deriveEncoder[RemoveFromCartEvent]

  implicit val searchEventEncoder: Encoder[SearchEvent] =
    deriveEncoder[SearchEvent]

  implicit val purchaseItemEncoder: Encoder[PurchaseItem] =
    deriveEncoder[PurchaseItem]

  implicit val eventEnvelopeEncoder: Encoder[EventEnvelope] =
    deriveEncoder[EventEnvelope]
}
