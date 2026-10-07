package generator

import generator.Utils.randomUser
import model.User

sealed trait UserGenerator {
  def generate(): User
}

class UserGeneratorImpl extends UserGenerator {
  override def generate(): User = User(randomUser())
}
