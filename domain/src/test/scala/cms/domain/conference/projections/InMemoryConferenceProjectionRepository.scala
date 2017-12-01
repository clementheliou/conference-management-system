package cms.domain.conference.projections

import scala.collection.mutable.{Map => MutableMap}

final class InMemoryConferenceProjectionRepository extends ConferenceProjectionRepository {

  private[this] val projections = MutableMap[String, ConferenceProjection]()

  def get(id: String) = projections.get(id)

  def getAll = projections.values.toSeq

  def save(projection: ConferenceProjection): Unit = projections(projection.slug) = projection
}
