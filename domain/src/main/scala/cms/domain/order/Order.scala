package cms.domain.order

import cms.domain.EventSourcedAggregate

final class Order private(val id: String, history: Seq[OrderEvent] = Nil) extends EventSourcedAggregate {

  type EventType = OrderEvent

  protected[this] val state = null

  private def this(id: String, conferenceId: String, seats: Seq[OrderSeat]){
    this(id)
    raise { OrderPlaced(id, conferenceId, seats.map(seat => Seat(seat.seatType, seat.quantity))) }
  }
}

object Order {

  implicit val rehydrateFrom: (String, Seq[OrderEvent]) => Order = Order.apply

  def apply(id: String, conferenceId: String, seats: Seq[OrderSeat]) = new Order(id, conferenceId, seats)

  def apply(id: String, history: Seq[OrderEvent]) ={
    if (history.isEmpty) {
      throw new IllegalArgumentException("Either create a new order or provide an history")
    }

    new Order(id, history)
  }
}