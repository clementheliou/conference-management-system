package cms.domain.order.projections

import cms.domain.ProjectionRepository
import cms.domain.order.OrderPlaced

final class PlacedOrderProjectionGenerator(repository: ProjectionRepository[PlacedOrderProjection]) {

  def apply(event: OrderPlaced){
    repository save PlacedOrderProjection(
      event.conferenceId,
      event.orderId,
      lastUpdate = event.creationDate,
      status = "WAITING_FOR_RESERVATION",
      requestedSeats = event.seats
    )
  }
}
