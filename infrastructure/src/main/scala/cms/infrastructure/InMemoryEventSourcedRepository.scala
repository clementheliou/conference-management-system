package cms.infrastructure

import cms.domain.{EventSourcedAggregate, EventSourcedRepository, VersionedEvent}

import scala.collection.mutable.{Map => MutableMap}

final class InMemoryEventSourcedRepository(eventPublisher: InMemoryEventPublisher) extends EventSourcedRepository {

  private[this] val eventStreams = MutableMap[String, Seq[VersionedEvent[_ <: EventSourcedAggregate#EventType]]]()

  def find[A <: EventSourcedAggregate](id: String)(implicit rehydrateFrom: (String, Seq[A#EventType]) => A) ={
    eventStreams.get(id)
      .map(eventStream => rehydrateFrom(id, eventStream.map(_.event.asInstanceOf[A#EventType])))
  }

  def save[A <: EventSourcedAggregate](aggregate: A){
    val eventStream = eventStreams.getOrElse(aggregate.id, Nil) ++ aggregate.events
    eventStreams.put(aggregate.id, eventStream)
    aggregate.events map { _.event } foreach { eventPublisher publish _ }
  }

}
