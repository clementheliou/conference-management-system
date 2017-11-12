package cms.domain

import scala.collection.mutable.{Map => MutableMap}

trait InMemoryEventSourcedRepository extends EventSourcedRepository {

  private[this] val eventStreams = MutableMap[String, List[VersionedEvent]]()

  override def find(aggregateId: String) = eventStreams.get(aggregateId)

  override def save(aggregate: EventSourcedAggregate){
    val eventStream = eventStreams.getOrElse(aggregate.id, Nil) ++ aggregate.events
    eventStreams.put(aggregate.id, eventStream)
  }

  def get(id: String) = eventStreams(id).map(_.event)

  def setHistory(aggregateId: String, events: Event*){
    val versionedEvents = events.zipWithIndex.map { case (event, index) => VersionedEvent(aggregateId, index, event) }
    eventStreams.put(aggregateId, versionedEvents.toList)
  }
}
