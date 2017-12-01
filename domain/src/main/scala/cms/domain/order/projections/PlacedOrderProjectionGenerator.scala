package cms.domain.order.projections

import cms.domain.order.OrderPlaced

final class PlacedOrderProjectionGenerator(repository: PlacedOrderProjectionRepository) {

  def apply(event: OrderPlaced){
    repository save PlacedOrderProjection(
      event.conferenceId,
      event.orderId,
      lastUpdate = event.creationDate,
      status = "WAITING_FOR_RESERVATION",
      requestedSeats = event.seats.map(seat => (seat.seatType, seat.quantity)): _*
    )
  }
}
