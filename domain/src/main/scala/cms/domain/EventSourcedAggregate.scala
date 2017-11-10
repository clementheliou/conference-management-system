package cms.domain

import scala.collection.mutable

trait EventSourcedAggregate {

  private[this] val pendingEvents = mutable.MutableList[VersionedEvent]()
  private[this] var version = 0L

  protected[this] def raise(event: Event): Unit = {
    pendingEvents += VersionedEvent(id, version, event)
    version += 1
  }

  def events = pendingEvents

  def id: String
}
