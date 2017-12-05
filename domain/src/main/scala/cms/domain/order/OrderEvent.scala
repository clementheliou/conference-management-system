package cms.domain.order

import cms.domain.Event

sealed trait OrderEvent extends Event

case class OrderPlaced(orderId: String, conferenceId: String, seats: (String, Int)) extends OrderEvent

case class SeatsReservationConfirmed(orderId: String, seats: (String, Int)) extends OrderEvent
