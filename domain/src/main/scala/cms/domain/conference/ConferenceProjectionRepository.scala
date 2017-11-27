package cms.domain.conference

import cms.domain.ProjectionRepository

import scala.collection.immutable.Map.empty

trait ConferenceProjectionRepository extends ProjectionRepository[ConferenceProjection]

case class ConferenceProjection(slug: String, name: String, lastUpdate: Long, seats: Map[String, Int] = empty)
