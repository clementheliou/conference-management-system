package cms.domain.features.stages

import cms.domain.conference.{ConferenceCommandHandler, ConferenceEventHandler, SeatsReservationRejected, SeatsReserved}
import cms.domain.order.{OrderCommandHandler, OrderEventHandler, PlaceOrder}
import cms.domain.{InMemoryEventPublisher, InMemoryEventSourcedRepository, SequentialPrefixedIdGenerator}
import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.{BeforeStage, ExpectedScenarioState, Quoted}

class WhenRegistrantPlaceOrder extends Stage[WhenRegistrantPlaceOrder] {

  @ExpectedScenarioState var conferenceId: String = _

  @ExpectedScenarioState var eventPublisher: InMemoryEventPublisher = _

  @ExpectedScenarioState var eventSourcedRepository: InMemoryEventSourcedRepository = _

  var orderCommandHandler: OrderCommandHandler = _

  @BeforeStage def setUp(){
    orderCommandHandler = new OrderCommandHandler(eventSourcedRepository, new SequentialPrefixedIdGenerator)

    val conferenceEventHandler = new ConferenceEventHandler(new ConferenceCommandHandler(eventSourcedRepository))
    val orderEventHandler = new OrderEventHandler(orderCommandHandler)

    eventPublisher subscribe conferenceEventHandler.apply
    eventPublisher subscribe (orderEventHandler.apply: SeatsReservationRejected => Unit)
    eventPublisher subscribe (orderEventHandler.apply: SeatsReserved => Unit)
  }

  def a_registrant_place_an_order_for_$_$_seats(seatsRequest: Int, @Quoted seatType: String){
    orderCommandHandler handle PlaceOrder(conferenceId, seatType -> seatsRequest)
  }

}
