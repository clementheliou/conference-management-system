package cms.domain

trait ProjectionRepository[T <: Projection] {

  def get(id: String): Option[T]

  def getAll: Seq[T]

  def save(projection: T): Unit
}

trait Projection {
  def id: String
}
