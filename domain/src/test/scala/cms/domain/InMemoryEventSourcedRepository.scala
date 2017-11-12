package cms.domain

import scala.collection.mutable.{Map => MutableMap}

trait InMemoryEventSourcedRepository extends EventSourcedRepository {

  private[this] val eventStreams = MutableMap[String, List[VersionedEvent[_ <: Event]]]()

  override def find[T <: Event](aggregateId: String) = eventStreams.get(aggregateId).map(_.map(_.event.asInstanceOf[T]))

  override def save[T <: Event](aggregate: EventSourcedAggregate[T]){
    val eventStream = eventStreams.getOrElse(aggregate.id, Nil) ++ aggregate.events
    eventStreams.put(aggregate.id, eventStream)
  }

  def get(id: String) = eventStreams(id).map(_.event)

  def setHistory(aggregateId: String, events: Event*){
    val versionedEvents = events.zipWithIndex.map { case (event, index) => VersionedEvent(aggregateId, index, event) }
    eventStreams.put(aggregateId, versionedEvents.toList)
  }
}
