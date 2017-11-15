package cms.domain.conference

import cms.domain.{CommandHandler, EventSourcedRepository}
import com.typesafe.scalalogging.Logger

final class ConferenceCommandHandler(repository: EventSourcedRepository[ConferenceEvent])
  extends CommandHandler[ConferenceCommand] {

  private val logger = Logger(classOf[ConferenceCommandHandler])

  def handle(command: ConferenceCommand): Unit = command match {
    case c: CreateConference => createConference(c)
    case c: UpdateConference => updateConference(c)
  }

  private def createConference(c: CreateConference): Unit = repository.find(c.slug) match {
    case Some(_) => logger.warn(s"Discard creation command on existing conference (slug=${ c.slug })")
    case None => repository save { Conference(c.name, c.slug) }
  }

  private def updateConference(c: UpdateConference): Unit = repository.find(c.id) match {
    case Some(history) =>
      val conference = Conference(c.id, history)
      conference.update(c.name)
      repository.save(conference)
    case None => logger.warn(s"Discard update command on missing conference (slug=${ c.id })")
  }
}
