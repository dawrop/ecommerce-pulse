package generator

import generator.Utils.randomSession
import model.{SessionState, User}

sealed trait SessionGenerator {
  def generate(user: User): SessionState
}

class SessionGeneratorImpl extends SessionGenerator {
  def generate(user: User): SessionState =
    SessionState(
      sessionId = randomSession(),
      user = user
    )
}
