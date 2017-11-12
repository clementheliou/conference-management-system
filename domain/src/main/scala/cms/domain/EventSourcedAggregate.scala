package cms.domain

import scala.collection.mutable

trait EventSourcedAggregate[T <: Event] {

  private[this] val pendingEvents = mutable.MutableList[VersionedEvent[T]]()
  private[this] var version = 0L

  protected[this] def raise(event: T){
    pendingEvents += VersionedEvent(id, version, event)
    version += 1
  }

  def events = pendingEvents.toList

  def id: String
}
