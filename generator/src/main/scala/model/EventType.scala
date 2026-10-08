package model

sealed trait EventType

object EventType {
  case object AddToCart extends EventType
  case object ProductView extends EventType
  case object Purchase extends EventType
  case object RemoveFromCart extends EventType
  case object Search extends EventType
}
