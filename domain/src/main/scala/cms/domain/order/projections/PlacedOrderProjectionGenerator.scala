package cms.domain.order.projections

import cms.domain.ProjectionRepository
import cms.domain.order.{OrderPlaced, SeatsReservationConfirmed}
import com.typesafe.scalalogging.Logger

final class PlacedOrderProjectionGenerator(repository: ProjectionRepository[PlacedOrderProjection]) {

  private val logger = Logger(getClass)

  def apply(event: OrderPlaced){
    repository save PlacedOrderProjection(
      event.conferenceId,
      event.orderId,
      lastUpdate = event.creationDate,
      status = "WAITING_FOR_RESERVATION",
      requestedSeats = event.seats
    )
  }

  def apply(event: SeatsReservationConfirmed): Unit = repository.get(event.orderId) match {
    case Some(projection) => repository save projection.copy(
      lastUpdate = event.creationDate,
      reservedSeats = Some(event.seats),
      status = "WAITING_FOR_PAYMENT"
    )
    case None => logger.info(s"Discard $event due to missing projection")
  }
}
