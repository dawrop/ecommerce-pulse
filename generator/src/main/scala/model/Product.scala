package model

import generator.Utils.randomElement

case class PurchaseItem(
    productId: String,
    quantity: Int,
    unitPrice: BigDecimal
)

case class Product(
    productId: String,
    price: BigDecimal
)

class ProductCatalog(products: Vector[Product]) {
  def randomProduct(): Product =
    randomElement(products)

  def get(productId: String): Product =
    products.find(_.productId == productId).get
}
