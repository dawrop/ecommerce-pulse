package model

case class SessionState(
    sessionId: String,
    user: User,
    viewedProducts: Set[String] = Set.empty,
    cart: Map[String, Int] = Map.empty,
    lastSearch: Option[String] = None
)
