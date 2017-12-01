package cms.domain.order.projections

import cms.domain.ProjectionRepository

trait PlacedOrderProjectionRepository extends ProjectionRepository[PlacedOrderProjection]

case class PlacedOrderProjection(
  conferenceId: String,
  id: String,
  lastUpdate: Long,
  status: String,
  requestedSeats: (String, Int)*
)
