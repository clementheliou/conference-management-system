package cms.domain

trait Aggregate {

  type EventType <: Event

  def id: String
}
