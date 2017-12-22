package cms.domain.features

import cms.domain.conference._
import cms.domain.order._
import cms.domain.{InMemoryEventPublisher, InMemoryEventSourcedRepository, SequentialPrefixedIdGenerator}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

class PlaceOrderForConference extends FeatureSpec with Matchers with GivenWhenThen {

  trait Setup {
    val eventPublisher = new InMemoryEventPublisher
    val eventSourcedRepository = new InMemoryEventSourcedRepository(eventPublisher)
    val idGenerator = new SequentialPrefixedIdGenerator

    val conferenceCommandHandler = new ConferenceCommandHandler(eventSourcedRepository)
    val conferenceEventHandler = new ConferenceEventHandler(conferenceCommandHandler)

    val orderCommandHandler = new OrderCommandHandler(eventSourcedRepository, idGenerator)
    val orderEventHandler = new OrderEventHandler(orderCommandHandler)

    eventPublisher subscribe conferenceEventHandler.apply
    eventPublisher subscribe orderEventHandler.apply
  }

  feature("Place an order for a conference") {
    scenario("Successfully place an order for a single conference seat type with enough quota") {
      new Setup {

        val quota = 10
        val seatType = "Workshop"

        Given(s"""a conference with a quota of $quota "$seatType" seats""")
        eventSourcedRepository.setHistory(
          "mix-it-18",
          ConferenceCreated(name = "MixIT", slug = "mix-it-18"),
          SeatsAdded(conferenceId = "mix-it-18", seatType, quota),
          ConferencePublished(id = "mix-it-18")
        )

        val seatsRequest = 8

        When(s"""a registrant place an order for ${ seatsRequest } "${ seatType }" seats""")
        orderCommandHandler handle PlaceOrder(conferenceId = "mix-it-18", seatType -> seatsRequest)

        Then(s"""$seatsRequest "$seatType" seats are reserved""")
        eventSourcedRepository.getEventStream("mix-it-18") should contain inOrderOnly(
          ConferenceCreated(name = "MixIT", slug = "mix-it-18"),
          SeatsAdded(conferenceId = "mix-it-18", seatType, quota),
          ConferencePublished(id = "mix-it-18"),
          SeatsReserved(conferenceId = "mix-it-18", orderId = "ID-1", seatType -> seatsRequest)
        )

        eventSourcedRepository.getEventStream("ID-1") should contain inOrderOnly(
          OrderPlaced(orderId = "ID-1", conferenceId = "mix-it-18", seatType -> seatsRequest),
          SeatsReservationConfirmed(orderId = "ID-1", seatType -> seatsRequest)
        )
      }
    }
  }

}
