package cms.domain

trait EventSourcedRepository {

  def find[T <: Event](aggregateId: String): Option[List[T]] = None

  def save[T <: Event](aggregate: EventSourcedAggregate[T]){}
}
