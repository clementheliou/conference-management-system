package cms.domain

trait EventSourcedAggregate extends Aggregate {

  private[this] var pendingEvents = Seq[VersionedEvent[EventType]]()
  private[this] var version = 0L

  protected[this] def raise(event: EventType){
    pendingEvents = pendingEvents :+ VersionedEvent(id, version, event)
    version += 1
  }

  def events = pendingEvents
}