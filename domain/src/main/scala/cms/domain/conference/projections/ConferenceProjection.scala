package cms.domain.conference.projections

import cms.domain.Projection

import scala.collection.immutable.Map.empty

case class ConferenceProjection(
  id: String,
  lastUpdate: Long,
  name: String,
  published: Boolean = false,
  seats: Map[String, Int] = empty
) extends Projection
