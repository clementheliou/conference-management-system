package cms.domain

case class VersionedEvent(sourceId: String, version: Long, event: Event) {}
