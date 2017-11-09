package cms.domain.eventsourcing

trait Event {
  final val creationDate = System.currentTimeMillis()
}
