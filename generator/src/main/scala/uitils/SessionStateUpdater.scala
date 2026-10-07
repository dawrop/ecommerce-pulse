package uitils

import model._

trait SessionStateUpdater {
  def update(session: SessionState, event: Event): SessionState
}

class DefaultSessionStateUpdater extends SessionStateUpdater {

  override def update(session: SessionState, event: Event): SessionState =
    event match {
      case AddToCartEvent(_, _, productId, quantity) =>
        session.copy(cart =
          session.cart
            .updated(
              productId,
              session.cart.getOrElse(productId, 0) + quantity
            )
        )

      case ProductViewEvent(_, _, productId) =>
        session.copy(viewedProducts = session.viewedProducts + productId)

      case PurchaseEvent(_, _, _, _) =>
        session.copy(cart = Map.empty)

      case RemoveFromCartEvent(_, _, productId, quantity) => {
        val currentQuantity =
          session.cart.getOrElse(productId, 0)

        val newQuantity =
          currentQuantity - quantity

        session.copy(
          cart =
            if (newQuantity > 0) session.cart.updated(productId, newQuantity)
            else session.cart - productId
        )
      }

      case SearchEvent(_, _, query) =>
        session.copy(lastSearch = Some(query))

    }
}
