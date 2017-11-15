package cms.domain

trait EventSourcedRepository[T <: Event] {

  def find(aggregateId: String): Option[List[T]]

  def save(aggregate: EventSourcedAggregate[T])
}
