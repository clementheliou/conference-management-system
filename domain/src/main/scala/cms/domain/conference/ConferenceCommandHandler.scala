package cms.domain.conference

import cms.domain.{CommandHandler, EventSourcedRepository}
import com.typesafe.scalalogging.Logger

class ConferenceCommandHandler extends CommandHandler[ConferenceCommand] with EventSourcedRepository[ConferenceEvent] {

  private val logger = Logger(classOf[ConferenceCommandHandler])

  def handle(command: ConferenceCommand): Unit = command match {
    case c: CreateConference => createConference(c)
    case c: UpdateConference => updateConference(c)
  }

  private def createConference(c: CreateConference): Unit = find(c.slug) match {
    case Some(_) => logger.warn(s"Discard creation command on existing conference (slug=${ c.slug })")
    case None => save { Conference(c.name, c.slug) }
  }

  private def updateConference(c: UpdateConference): Unit = find(c.id) match {
    case Some(history) =>
      val conference = Conference(c.id, history)
      conference.update(c.name)
      save(conference)
    case None => logger.warn(s"Discard update command on missing conference (slug=${ c.id })")
  }
}
