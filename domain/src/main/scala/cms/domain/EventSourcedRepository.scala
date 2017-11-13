package cms.domain

trait EventSourcedRepository[T <: Event] {

  def find(aggregateId: String): Option[List[T]] = None

  def save(aggregate: EventSourcedAggregate[T]){}
}
