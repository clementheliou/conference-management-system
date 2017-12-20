package cms.domain.features.steps

import cms.domain.conference._
import cms.domain.order._
import cms.domain.{Event, InMemoryEventPublisher, InMemoryEventSourcedRepository, SequentialPrefixedIdGenerator}
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers

final class FeaturesStepDefinitions extends ScalaDsl with EN with Matchers {

  val eventPublisher = new InMemoryEventPublisher
  val eventSourcedRepository = new InMemoryEventSourcedRepository(eventPublisher)
  val conferenceEventHandler = new ConferenceEventHandler(new ConferenceCommandHandler(eventSourcedRepository))

  val orderCommandHandler = new OrderCommandHandler(eventSourcedRepository, new SequentialPrefixedIdGenerator)
  val orderEventHandler = new OrderEventHandler(orderCommandHandler)

  eventPublisher subscribe conferenceEventHandler.apply
  eventPublisher subscribe orderEventHandler.apply

  val conferenceId = "mix-it-18"
  var conferenceEvents: Seq[Event] = _

  Given("""^a published conference with a quota of (\d+) ([a-zA-Z]+) seats$""") { (quota: Int, seatType: String) =>
    this.conferenceEvents = Seq(
      ConferenceCreated(name = "MixIT", slug = conferenceId),
      SeatsAdded(conferenceId = conferenceId, seatType, quota),
      ConferencePublished(id = conferenceId)
    )

    eventSourcedRepository.setHistory(conferenceId, conferenceEvents: _*)
  }

  When("""^a registrant place an order for (\d+) ([a-zA-Z]+) seats$""") { (seatsRequest: Int, seatType: String) =>
    orderCommandHandler handle PlaceOrder(conferenceId, seatType -> seatsRequest)
  }

  Then("""^the (\d+) ([a-zA-Z]+) seats are successfully reserved$""") { (seatsRequest: Int, seatType: String) =>
    eventSourcedRepository.getEventStream(conferenceId) should contain theSameElementsInOrderAs (
      conferenceEvents :+ SeatsReserved(conferenceId, orderId = "ID-1", seatType -> seatsRequest)
    )

    eventSourcedRepository.getEventStream("ID-1") should contain inOrderOnly(
      OrderPlaced(orderId = "ID-1", conferenceId, seatType -> seatsRequest),
      SeatsReservationConfirmed(orderId = "ID-1", seatType -> seatsRequest)
    )
  }
}
