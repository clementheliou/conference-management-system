package cms.domain.conference

import com.typesafe.scalalogging.Logger

final class ConferenceProjectionGenerator(repository: ConferenceProjectionRepository) {

  private val logger = Logger(classOf[ConferenceProjectionGenerator])

  def apply(event: ConferenceCreated){
    repository save ConferenceProjection(event.slug, event.name, event.creationDate)
  }

  def apply(event: ConferenceUpdated): Unit = repository.get(event.id) match {
    case Some(projection) => repository save projection.copy(name = event.name, lastUpdate = event.creationDate)
    case None => logger.info(s"Discard $event due to missing projection")
  }
}
