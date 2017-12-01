package cms.domain.order

import cms.domain.conference.ConferenceCreated
import cms.domain.{InMemoryEventSourcedRepository, SequentialPrefixedIdGenerator}
import org.scalatest.{FlatSpec, Matchers}

class OrderAcceptanceTest extends FlatSpec with Matchers {

  trait Setup {
    val eventSourcedRepository = new InMemoryEventSourcedRepository
    val identifierGenerator = new SequentialPrefixedIdGenerator
    val orderCommandHandler = new OrderCommandHandler(eventSourcedRepository, identifierGenerator)
  }

  "An order" can "be placed for a single conference seat type" in new Setup {

    // Given
    eventSourcedRepository.setHistory("mix-it-18", ConferenceCreated(name = "MixIT 18", slug = "mix-it-18"))

    // When
    orderCommandHandler.handle {
      PlaceOrder(conferenceId = "mix-it-18", Seq(OrderSeat(seatType = "Workshop", quantity = 3)))
    }

    // Then
    eventSourcedRepository.getEventStream("ID-1") should contain only
      OrderPlaced(orderId = "ID-1", conferenceId = "mix-it-18", seats = Seq(Seat(seatType = "Workshop", quantity = 3)))
  }

  it can "not be placed for a missing conference" in new Setup {

    // When
    orderCommandHandler.handle {
      PlaceOrder(conferenceId = "mix-it-18", Seq(OrderSeat(seatType = "Workshop", quantity = 3)))
    }

    // Then
    eventSourcedRepository.find[Order]("ID-1") shouldBe None
  }

  it can "not be placed without seats" in new Setup {

    // Given
    eventSourcedRepository.setHistory("mix-it-18", ConferenceCreated(name = "MixIT 18", slug = "mix-it-18"))

    // When
    orderCommandHandler handle PlaceOrder(conferenceId = "mix-it-18", Nil)

    // Then
    eventSourcedRepository.find[Order]("ID-1") shouldBe None
  }

  it can "not be placed twice" in new Setup {

    // Given
    val orderId = "ID-1"

    eventSourcedRepository.setHistory("mix-it-18", ConferenceCreated(name = "MixIT 18", slug = "mix-it-18"))
    eventSourcedRepository.setHistory(
      orderId,
      OrderPlaced(orderId, conferenceId = "mix-it-18", seats = Seq(Seat(seatType = "Workshop", quantity = 3)))
    )

    // When
    orderCommandHandler.handle {
      PlaceOrder(conferenceId = "mix-it-18", Seq(OrderSeat(seatType = "Workshop", quantity = 6)))
    }

    // Then
    eventSourcedRepository.getEventStream(orderId) should contain only
      OrderPlaced(orderId, conferenceId = "mix-it-18", seats = Seq(Seat(seatType = "Workshop", quantity = 3)))
  }
}