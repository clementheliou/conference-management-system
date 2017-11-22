package cms.domain.conference

import cms.domain.ProjectionRepository

trait ConferenceProjectionRepository extends ProjectionRepository[ConferenceProjection]

case class ConferenceProjection(slug: String, name: String, lastUpdate: Long)
