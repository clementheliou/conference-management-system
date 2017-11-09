package cms.domain.conference

import cms.domain.eventsourcing.{Event, EventSourcedAggregate}

final class Conference(val id: String, history: List[ConferenceEvent] = Nil) extends EventSourcedAggregate {

  private[this] val state = new DecisionProjection
  history foreach state.apply

  def this(name: String, slug: String) {
    this(slug)
    raise {
      ConferenceCreated(name, slug)
    }
  }

  def update(name: String): Unit = raise {
    ConferenceUpdated(name)
  }

  private class DecisionProjection {
    var name, slug: String = _

    def apply(event: ConferenceEvent): Unit = event match {
      case e: ConferenceCreated =>
        name = e.name
        slug = e.slug

      case e: ConferenceUpdated => name = e.name
    }
  }
}

sealed trait ConferenceEvent extends Event

case class ConferenceCreated(name: String, slug: String) extends ConferenceEvent

case class ConferenceUpdated(name: String) extends ConferenceEvent
