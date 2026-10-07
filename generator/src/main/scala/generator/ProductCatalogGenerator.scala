package generator

import generator.Utils._
import model.{Product, ProductCatalog}

class ProductCatalogGenerator {

  def generate(): ProductCatalog = {
    val products =
      (1 to productsNumber).map { i =>
        Product(
          productId = s"product-$i",
          price = randomPrice()
        )
      }.toVector

    new ProductCatalog(products)
  }
}
