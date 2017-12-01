package cms.domain.conference.projections

import cms.domain.ProjectionRepository
import cms.domain.conference._
import com.typesafe.scalalogging.Logger

final class ConferenceProjectionGenerator(repository: ProjectionRepository[ConferenceProjection]) {
  private val logger = Logger(classOf[ConferenceProjectionGenerator])

  def apply(event: ConferenceCreated){
    repository save ConferenceProjection(event.slug, event.creationDate, event.name)
  }

  def apply(event: ConferencePublished): Unit = repository.get(event.id) match {
    case Some(projection) => repository save projection.copy(lastUpdate = event.creationDate, published = true)
    case None => logger.info(s"Discard $event due to missing projection")
  }

  def apply(event: ConferenceUpdated): Unit = repository.get(event.id) match {
    case Some(projection) => repository save projection.copy(name = event.name, lastUpdate = event.creationDate)
    case None => logger.info(s"Discard $event due to missing projection")
  }

  def apply(event: SeatsAdded): Unit = repository.get(event.conferenceId) match {
    case Some(projection) => repository save projection.copy(
      lastUpdate = event.creationDate,
      seats = projection.seats + (event.seatType -> event.quota)
    )
    case None => logger.info(s"Discard $event due to missing projection")
  }
}
