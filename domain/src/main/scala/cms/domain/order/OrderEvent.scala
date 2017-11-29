package cms.domain.order

import cms.domain.Event

sealed trait OrderEvent extends Event

case class OrderPlaced(orderId: String, conferenceId: String, seats: Seq[Seat]) extends OrderEvent

case class Seat(seatType: String, quantity: Int)
