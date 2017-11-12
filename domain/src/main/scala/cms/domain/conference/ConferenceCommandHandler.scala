package cms.domain.conference

import cms.domain.{CommandHandler, EventSourcedRepository}

class ConferenceCommandHandler extends CommandHandler[ConferenceCommand] with EventSourcedRepository {

  def handle(command: ConferenceCommand): Unit = command match {
    case c: CreateConference => createConference(c)
    case c: UpdateConference => updateConference(c)
  }

  private def createConference(c: CreateConference): Unit = find(c.slug) match {
    case Some(_) => throw new UnsupportedOperationException("Not tested yet")
    case None => save { Conference(c.name, c.slug) }
  }

  private def updateConference(c: UpdateConference): Unit = find(c.id) match {
    case Some(history) =>
      val conference = Conference(c.id, history)
      conference.update(c.name)
      save(conference)
    case None => throw new UnsupportedOperationException("Not tested yet")
  }
}
