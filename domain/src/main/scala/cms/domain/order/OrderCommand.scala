package cms.domain.order

import cms.domain.Command

sealed trait OrderCommand extends Command

case class ConfirmSeatsReservation(orderId: String, seats: (String, Int)) extends OrderCommand

case class PlaceOrder(conferenceId: String, seats: (String, Int)) extends OrderCommand
