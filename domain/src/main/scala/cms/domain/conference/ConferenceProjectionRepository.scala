package cms.domain.conference

import cms.domain.ProjectionRepository

import scala.collection.immutable.Map.empty

trait ConferenceProjectionRepository extends ProjectionRepository[ConferenceProjection]

case class ConferenceProjection(
  lastUpdate: Long,
  name: String,
  slug: String,
  published: Boolean = false,
  seats: Map[String, Int] = empty
)
