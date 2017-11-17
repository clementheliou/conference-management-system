package cms.domain

import scala.collection.mutable.{Map => MutableMap}

final class InMemoryEventSourcedRepository extends EventSourcedRepository {

  private[this] val eventStreams = MutableMap[String, Seq[VersionedEvent[_ <: Event]]]()

  def find[A <: EventSourcedAggregate](id: String)(implicit rehydrateFrom: (String, Seq[A#EventType]) => A) ={
    eventStreams.get(id) match {
      case Some(eventStream) => Some(rehydrateFrom(id, eventStream.map(_.event.asInstanceOf[A#EventType])))
      case None => None
    }
  }

  def save[A <: EventSourcedAggregate](aggregate: A){
    val eventStream = eventStreams.getOrElse(aggregate.id, Nil) ++ aggregate.events
    eventStreams.put(aggregate.id, eventStream)
  }

  def getEventStream(id: String) = eventStreams(id).map(_.event)

  def setHistory(aggregateId: String, events: Event*){
    val versionedEvents = events.zipWithIndex.map { case (event, index) => VersionedEvent(aggregateId, index, event) }
    eventStreams.put(aggregateId, versionedEvents)
  }

}
