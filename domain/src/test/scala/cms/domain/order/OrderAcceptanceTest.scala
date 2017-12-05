package cms.domain.order

import cms.domain.{InMemoryEventSourcedRepository, SequentialPrefixedIdGenerator}
import org.scalatest.{FlatSpec, Matchers}

class OrderAcceptanceTest extends FlatSpec with Matchers {

  trait Setup {
    val eventSourcedRepository = new InMemoryEventSourcedRepository
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

    eventSourcedRepository.setHistory(
      orderId,
      OrderPlaced(orderId, conferenceId = "mix-it-18", seats = "Workshop" -> 3)
    )

    // When
    orderCommandHandler.handle { PlaceOrder(conferenceId = "mix-it-18", "Workshop" -> 6) }

    // Then
    eventSourcedRepository.getEventStream(orderId) should contain only
      OrderPlaced(orderId, conferenceId = "mix-it-18", seats = "Workshop" -> 3)
  }

  "An order seats reservation" can "be confirmed" in new Setup {

    // Given
    eventSourcedRepository.setHistory(
      "ID-1",
      OrderPlaced(orderId = "ID-1", conferenceId = "mix-it-18", seats = "Workshop" -> 3)
    )

    // When
    orderCommandHandler handle ConfirmSeatsReservation(orderId = "ID-1", seats = "Workshop" -> 3)

    // Then
    eventSourcedRepository.getEventStream("ID-1") should contain inOrderOnly(
      OrderPlaced(orderId = "ID-1", conferenceId = "mix-it-18", seats = "Workshop" -> 3),
      SeatsReservationConfirmed(orderId = "ID-1", seats = "Workshop" -> 3)
    )
  }

  it can "not be confirmed for a missing order" in new Setup {

    // When
    orderCommandHandler handle ConfirmSeatsReservation(orderId = "ID-1", seats = "Workshop" -> 3)

    // Then
    eventSourcedRepository.find[Order]("ID-1") shouldBe None
  }
}
