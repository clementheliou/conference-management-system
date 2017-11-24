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

    def apply(event: EventType): DecisionProjection

    def computeFrom(history: Seq[EventType]): this.type ={
      history.foldLeft(this)((state, event) => { version += 1; state apply event }).asInstanceOf[this.type]
    }
  }
}