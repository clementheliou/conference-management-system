package cms.domain

trait ProjectionRepository[T] {

  def get(id: String): Option[T]

  def getAll: Seq[T]

  def save(projection: T): Unit
}
