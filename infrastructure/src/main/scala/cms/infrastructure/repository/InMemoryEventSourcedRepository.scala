package cms.infrastructure.repository

import cms.domain.{Event, EventSourcedAggregate, EventSourcedRepository, VersionedEvent}

import scala.collection.mutable.{Map => MutableMap}

final class InMemoryEventSourcedRepository[T <: Event] extends EventSourcedRepository[T] {

  private[this] val eventStreams = MutableMap[String, List[VersionedEvent[T]]]()

  override def find(aggregateId: String) = eventStreams.get(aggregateId).map(_.map(_.event))

  override def save(aggregate: EventSourcedAggregate[T]){
    val eventStream = eventStreams.getOrElse(aggregate.id, Nil) ++ aggregate.events
    eventStreams.put(aggregate.id, eventStream)
  }
}
