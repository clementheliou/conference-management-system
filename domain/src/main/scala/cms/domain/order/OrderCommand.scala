package cms.domain.order

import cms.domain.Command

sealed trait OrderCommand extends Command

case class PlaceOrder(conferenceId: String, seats: Seq[OrderSeat]) extends OrderCommand

case class OrderSeat(seatType: String, quantity: Int)

