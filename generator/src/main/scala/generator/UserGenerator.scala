package generator

import model.UserPersona._
import model.{User, UserPersona}

sealed trait UserGenerator {
  def generate(): User
}

class UserGeneratorImpl extends UserGenerator {

  private val personaWeights: List[(UserPersona, Int)] = List(
    RegularShopper -> 50,
    ImpulseBuyer   -> 20,
    WindowShopper  -> 15,
    BargainHunter  -> 10,
    ScraperBot     -> 5
  )

  override def generate(): User = {
    val userId  = Utils.randomUser()
    val persona = Utils.selectByWeight(personaWeights, RegularShopper)

    User(userId, persona)
  }
}
