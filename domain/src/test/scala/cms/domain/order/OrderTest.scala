package cms.domain.order

import cms.domain.{InMemoryEventPublisher, InMemoryEventSourcedRepository, SequentialPrefixedIdGenerator}
import org.scalatest.{FlatSpec, Matchers}

class OrderTest extends FlatSpec with Matchers {

  trait Setup {
    val eventPublisher = new InMemoryEventPublisher
    val eventSourcedRepository = new InMemoryEventSourcedRepository(eventPublisher)
    val identifierGenerator = new SequentialPrefixedIdGenerator
    val orderCommandHandler = new OrderCommandHandler(eventSourcedRepository, identifierGenerator)
  }

  "An order" can "be placed for a single conference seat type" in new Setup {

    // When
    orderCommandHandler.handle { PlaceOrder(conferenceId = "mix-it-18", "Workshop" -> 3) }

    // Then
    eventSourcedRepository.getEventStream("ID-1") should contain only
      OrderPlaced(orderId = "ID-1", conferenceId = "mix-it-18", seats = "Workshop" -> 3)
  }

  it can "not be placed without seats" in new Setup {

    // When
    orderCommandHandler handle PlaceOrder(conferenceId = "mix-it-18", "Workshop" -> 0)

    // Then
    eventSourcedRepository.find[Order]("ID-1") shouldBe None
  }

  it can "not be placed twice" in new Setup {

    // Given
    val orderId = "ID-1"
    val history = OrderPlaced(orderId, conferenceId = "mix-it-18", seats = "Workshop" -> 3)

    eventSourcedRepository.setHistory(orderId, history)

    // When
    orderCommandHandler.handle { PlaceOrder(conferenceId = "mix-it-18", "Workshop" -> 6) }

    // Then
    eventSourcedRepository.getEventStream(orderId) should contain only history
  }

  it can "be rejected" in new Setup {

    // Given
    val orderId = "ID-1"
    val history = Seq(OrderPlaced(orderId, conferenceId = "mix-it-18", seats = "Workshop" -> 3))

    eventSourcedRepository.setHistory(orderId, history: _*)

    // When
    orderCommandHandler handle RejectOrder(orderId)

    // Then
    eventSourcedRepository.getEventStream(orderId) should contain theSameElementsInOrderAs
      history :+ OrderRejected(orderId, conferenceId = "mix-it-18")
  }

  it should "not be instantiated from an empty history" in {
    the[IllegalArgumentException] thrownBy {
      Order(id = "ID-1", Nil)
    } should have message "Either create a new order or provide an history"
  }

  "An order seats reservation" can "be confirmed" in new Setup {

    // Given
    val history = Seq(OrderPlaced(orderId = "ID-1", conferenceId = "mix-it-18", seats = "Workshop" -> 3))
    eventSourcedRepository.setHistory("ID-1", history: _*)

    // When
    orderCommandHandler handle ConfirmSeatsReservation(orderId = "ID-1", seats = "Workshop" -> 3)

    // Then
    eventSourcedRepository.getEventStream("ID-1") should contain theSameElementsInOrderAs
      history :+ SeatsReservationConfirmed(orderId = "ID-1", seats = "Workshop" -> 3)
  }

  it can "not be confirmed for a missing order" in new Setup {

    // When
    orderCommandHandler handle ConfirmSeatsReservation(orderId = "ID-1", seats = "Workshop" -> 3)

    // Then
    eventSourcedRepository.find[Order]("ID-1") shouldBe None
  }
}
