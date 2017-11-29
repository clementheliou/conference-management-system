package cms.domain.order

import org.scalatest.{FlatSpec, Matchers}

class OrderTest extends FlatSpec with Matchers {

  "An order" should "not be instantiated from an empty history" in {
    the[IllegalArgumentException] thrownBy {
      Order(id = "ID-1", Nil)
    } should have message "Either create a new order or provide an history"
  }
}
