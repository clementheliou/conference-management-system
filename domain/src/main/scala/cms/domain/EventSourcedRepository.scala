package cms.domain

trait EventSourcedRepository[A <: EventSourcedAggregate] {

  def find(aggregateId: String)(implicit rehydrateFrom: (String, Seq[A#EventType]) => A): Option[A]

  def save(aggregate: A): Unit
}
