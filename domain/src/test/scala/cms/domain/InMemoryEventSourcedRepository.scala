package cms.domain

import scala.collection.mutable.{Map => MutableMap}

final class InMemoryEventSourcedRepository[T <: Event] extends EventSourcedRepository[T] {

  private[this] val eventStreams = MutableMap[String, List[VersionedEvent[T]]]()

  override def find(aggregateId: String) = eventStreams.get(aggregateId).map(_.map(_.event))

  override def save(aggregate: EventSourcedAggregate[T]){
    val eventStream = eventStreams.getOrElse(aggregate.id, Nil) ++ aggregate.events
    eventStreams.put(aggregate.id, eventStream)
  }

  def get(id: String) = eventStreams(id).map(_.event)

  def setHistory(aggregateId: String, events: T*){
    val versionedEvents = events.zipWithIndex.map { case (event, index) => VersionedEvent(aggregateId, index, event) }
    eventStreams.put(aggregateId, versionedEvents.toList)
  }
}
