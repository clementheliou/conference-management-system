package cms.domain.eventsourcing

case class VersionedEvent(sourceId: String, version: Long, event: Event) {}
