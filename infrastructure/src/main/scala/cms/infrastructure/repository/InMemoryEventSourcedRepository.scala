package cms.infrastructure.repository

import cms.domain.{EventSourcedAggregate, EventSourcedRepository, VersionedEvent}

import scala.collection.mutable.{Map => MutableMap}

final class InMemoryEventSourcedRepository[A <: EventSourcedAggregate] extends EventSourcedRepository[A] {

  private[this] val eventStreams = MutableMap[String, List[VersionedEvent[_ <: A#EventType]]]()

  def find(aggregateId: String)(implicit rehydrateFrom: (String, List[A#EventType]) => A)= {
    eventStreams.get(aggregateId) match {
      case Some(eventStream) => Some(rehydrateFrom(aggregateId, eventStream.map(_.event)))
      case None => None
    }
  }

  def save(aggregate: A){
    val eventStream = eventStreams.getOrElse(aggregate.id, Nil) ++ aggregate.events
    eventStreams.put(aggregate.id, eventStream)
  }

}
