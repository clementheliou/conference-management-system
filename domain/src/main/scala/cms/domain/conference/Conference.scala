package cms.domain.conference

import cms.domain.eventsourcing.{Event, EventSourcedAggregate}

final class Conference(val id: String, history: List[ConferenceEvent] = Nil) extends EventSourcedAggregate {

  def this(name: String, slug: String) {
    this(slug)
    raise {
      ConferenceCreated(name, slug)
    }
  }

}

sealed trait ConferenceEvent extends Event

case class ConferenceCreated(name: String, slug: String) extends ConferenceEvent
