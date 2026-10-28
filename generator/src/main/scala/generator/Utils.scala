package generator

import com.typesafe.config.{Config, ConfigFactory}

import java.util.UUID
import scala.math.BigDecimal.RoundingMode
import scala.util.Random

object Utils {
  val generatorConfig: Config =
    ConfigFactory.load().getConfig("generator")
  val productsNumber: Int =
    generatorConfig.getInt("products")
  val random                   = new Random()
  val products: Vector[String] =
    (1 to productsNumber)
      .map(i => s"product-$i")
      .toVector
  private val seed =
    generatorConfig.getInt("seed")
  private val usersNumber =
    generatorConfig.getInt("users")
  private val randomWithSeed =
    new Random(seed)
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

  def randomUUID(): UUID =
    UUID.randomUUID()

  def randomUser(): String =
    randomElement(users)

  def randomElement[T](items: Vector[T]): T =
    items(randomWithSeed.nextInt(items.length))

  def randomSession(): String =
    randomElement(sessions)

  def randomProduct(): String =
    randomElement(products)

  def randomQuantity(max: Int): Int =
    1 + randomWithSeed.nextInt(max)

  def randomPrice(maxPrice: Double = 250.00): BigDecimal = {
    val randomDouble = randomWithSeed.nextDouble() * maxPrice
    BigDecimal(randomDouble.toString)
      .setScale(2, RoundingMode.HALF_UP)
  }

  def randomSearchQuery(): String =
    randomElement(searchQueries)

  def selectByWeight[T](weightedItems: List[(T, Int)], fallback: T): T = {
    if (weightedItems.isEmpty) return fallback

    val (items, weights) = weightedItems.unzip
    val totalWeight      = weights.sum

    if (totalWeight <= 0) return fallback

    val cumulativeWeights = items.zip(weights.scanLeft(0)(_ + _).tail)
    val roll              = random.nextInt(totalWeight)

    cumulativeWeights
      .collectFirst {
        case (item, cumulative) if roll < cumulative => item
      }
      .getOrElse(fallback)
  }
}
