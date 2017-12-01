package cms.domain.order.projections

import scala.collection.mutable.{Map => MutableMap}

final class InMemoryPlacedOrderProjectionRepository extends PlacedOrderProjectionRepository {

  private[this] val projections = MutableMap[String, PlacedOrderProjection]()

  def get(id: String) = projections.get(id)

  def getAll = projections.values.toSeq

  def save(projection: PlacedOrderProjection): Unit = projections(projection.id) = projection
}
