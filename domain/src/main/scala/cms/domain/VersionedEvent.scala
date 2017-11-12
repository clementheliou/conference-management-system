package cms.domain

case class VersionedEvent[T <: Event](sourceId: String, version: Long, event: T)
