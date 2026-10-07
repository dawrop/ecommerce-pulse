package generator

import com.typesafe.config.{Config, ConfigFactory}

import java.util.UUID
import scala.math.BigDecimal.RoundingMode
import scala.util.Random

object Utils {
  private val generatorConfig: Config =
    ConfigFactory.load().getConfig("generator")

  private val seed =
    generatorConfig.getInt("seed")

  private val usersNumber =
    generatorConfig.getInt("users")

  val productsNumber: Int =
    generatorConfig.getInt("products")

  private val random =
    new Random(seed)

  val products: Vector[String] =
    (1 to productsNumber)
      .map(i => s"product-$i")
      .toVector

  private val users: Vector[String] =
    (1 to usersNumber)
      .map(i => s"user-$i")
      .toVector

  private val sessions: Vector[String] =
    (1 to 1000)
      .map(i => s"session-$i")
      .toVector

  private val searchQueries = Vector(
    "laptop",
    "phone",
    "headphones",
    "keyboard",
    "monitor",
    "gaming mouse"
  )

  def randomUUID(): String =
    UUID.randomUUID().toString

  def randomUser(): String =
    randomElement(users)

  def randomSession(): String =
    randomElement(sessions)

  def randomElement[T](items: Vector[T]): T =
    items(random.nextInt(items.length))

  def randomProduct(): String =
    randomElement(products)

  def randomQuantity(max: Int): Int =
    1 + random.nextInt(max)

  def randomPrice(maxPrice: Double = 250.00): BigDecimal = {
    val randomDouble = random.nextDouble() * maxPrice
    BigDecimal(randomDouble.toString)
      .setScale(2, RoundingMode.HALF_UP)
  }

  def randomSearchQuery(): String =
    randomElement(searchQueries)
}
