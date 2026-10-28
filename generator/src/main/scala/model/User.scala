package model

sealed trait UserPersona

object UserPersona {
  case object RegularShopper extends UserPersona
  case object ImpulseBuyer   extends UserPersona
  case object WindowShopper  extends UserPersona
  case object BargainHunter  extends UserPersona
  case object ScraperBot     extends UserPersona
}

case class User(userId: String, persona: UserPersona)
