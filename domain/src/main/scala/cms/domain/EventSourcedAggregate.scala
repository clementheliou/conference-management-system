package cms.domain

trait EventSourcedAggregate extends Aggregate {

  private[this] var pendingEvents = Seq[VersionedEvent[EventType]]()
  private[this] var version = 0L

  protected[this] val state: DecisionProjection

  protected[this] def raise(event: EventType){
    pendingEvents = pendingEvents :+ VersionedEvent(id, version, event)
    version += 1
  }

  def events = pendingEvents

  trait DecisionProjection {
    def applyEvent(event: EventType): Unit

    private def apply(event: EventType): this.type ={
      applyEvent(event)
      version += 1
      this
    }

    def computeFrom(history: Seq[EventType]) = history.foldLeft(this)((state, event) => state.apply(event))
  }
}