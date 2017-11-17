package cms.domain

trait EventSourcedRepository {

  def find[A <: EventSourcedAggregate](id: String)(implicit rehydrateFrom: (String, Seq[A#EventType]) => A): Option[A]

  def save[A <: EventSourcedAggregate](aggregate: A): Unit
}
