package cms.domain

trait EventSourcedRepository {

  def find(id: String): Option[List[VersionedEvent]] = None

  def save(aggregate: EventSourcedAggregate){}
}
