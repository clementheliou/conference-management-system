package cms.infrastructure

import cms.domain.{Projection, ProjectionRepository}

import scala.collection.mutable.{Map => MutableMap}

trait InMemoryProjectionRepository[T <: Projection] extends ProjectionRepository[T] {

  private[this] val projections = MutableMap[String, T]()

  def get(id: String) = projections.get(id)

  def getAll = projections.values.toSeq

  def save(projection: T): Unit = projections(projection.id) = projection
}
