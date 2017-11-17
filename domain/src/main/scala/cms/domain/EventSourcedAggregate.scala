package cms.domain

import scala.collection.mutable

trait EventSourcedAggregate extends Aggregate {

  private[this] val pendingEvents = mutable.MutableList[VersionedEvent[EventType]]()
  private[this] var version = 0L

  protected[this] def raise(event: EventType){
    pendingEvents += VersionedEvent(id, version, event)
    version += 1
  }

  def events = pendingEvents.toList

}