package cms.domain.conference

import cms.domain.{CommandHandler, EventSourcedRepository}

class ConferenceCommandHandler extends CommandHandler[ConferenceCommand] with EventSourcedRepository {

  def handle(command: ConferenceCommand): Unit = command match {
    case c: CreateConference => if (find(c.slug).isEmpty) save { Conference(c.name, c.slug) }
  }
}
