package cms.domain.order

import cms.domain.EventSourcedAggregate

final class Order private(val id: String, history: Seq[OrderEvent] = Nil) extends EventSourcedAggregate {

  type EventType = OrderEvent

  protected[this] val state = OrderDecisionProjection().computeFrom(history)

  private def this(id: String, conferenceId: String, seats: (String, Int)){
    this(id)
    raise { OrderPlaced(id, conferenceId, seats) }
  }

  def confirmSeatsReservation(seats: (String, Int)): Unit = raise { SeatsReservationConfirmed(id, seats) }

  def reject(): Unit = raise { OrderRejected(id, state.conferenceId) }

  case class OrderDecisionProjection(conferenceId: String = "") extends DecisionProjection {
    def apply(event: OrderEvent) = event match {
      case OrderPlaced(_, conference, _) => copy(conferenceId = conference)
      case _ => this
    }
  }
}

object Order {

  implicit val rehydrateFrom: (String, Seq[OrderEvent]) => Order = Order.apply

  def apply(id: String, conferenceId: String, seats: (String, Int)) = new Order(id, conferenceId, seats)

  def apply(id: String, history: Seq[OrderEvent]) ={
    if (history.isEmpty) {
      throw new IllegalArgumentException("Either create a new order or provide an history")
    }

    new Order(id, history)
  }
}