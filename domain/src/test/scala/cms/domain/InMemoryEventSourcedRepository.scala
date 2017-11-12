package cms.domain

import scala.collection.mutable.{Map => MutableMap}

trait InMemoryEventSourcedRepository extends EventSourcedRepository {

  private[this] val eventStreams = MutableMap[String, List[VersionedEvent]]()

  override def find(id: String) = eventStreams.get(id)

  override def save(aggregate: EventSourcedAggregate){
    val eventStream = eventStreams.getOrElse(aggregate.id, Nil) ++ aggregate.events
    eventStreams.put(aggregate.id, eventStream)
  }

  def get(id: String) = eventStreams(id).map(_.event)
}
