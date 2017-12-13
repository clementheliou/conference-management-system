package cms.domain.features.stages

import cms.domain.conference.SeatsReserved
import cms.domain.order.{OrderPlaced, SeatsReservationConfirmed}
import cms.domain.{Event, InMemoryEventSourcedRepository}
import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.{ExpectedScenarioState, Quoted}
import org.scalatest.Matchers

class ThenSeatsAreReserved extends Stage[ThenSeatsAreReserved] with Matchers {

  @ExpectedScenarioState var conferenceHistory: Seq[Event] = _

  @ExpectedScenarioState var conferenceId: String = _

  @ExpectedScenarioState var eventSourcedRepository: InMemoryEventSourcedRepository = _

  def $_$_seats_are_reserved(seatsRequest: Int, @Quoted seatType: String){
    eventSourcedRepository.getEventStream(conferenceId) should contain theSameElementsInOrderAs
      conferenceHistory :+ SeatsReserved(conferenceId, orderId = "ID-1", seatType -> seatsRequest)

    eventSourcedRepository.getEventStream("ID-1") should contain inOrderOnly(
      OrderPlaced(orderId = "ID-1", conferenceId, seatType -> seatsRequest),
      SeatsReservationConfirmed(orderId = "ID-1", seatType -> seatsRequest)
    )
  }

}
