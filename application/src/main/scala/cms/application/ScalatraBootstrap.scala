package cms.application

import javax.servlet.ServletContext

import cms.domain.conference._
import cms.infrastructure.conference.{Conferences, InMemoryConferenceProjectionRepository}
import cms.infrastructure.{InMemoryEventPublisher, InMemoryEventSourcedRepository}
import org.scalatra.LifeCycle

final class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext){
    val eventPublisher = new InMemoryEventPublisher
    val eventSourcedRepository = new InMemoryEventSourcedRepository(eventPublisher)

    val conferenceCommandHandler = new ConferenceCommandHandler(eventSourcedRepository)
    val conferenceProjectionRepository = new InMemoryConferenceProjectionRepository
    val conferenceProjectionGenerator = new ConferenceProjectionGenerator(conferenceProjectionRepository)
    val conferencesEndpoint = new Conferences(conferenceCommandHandler, conferenceProjectionRepository)

    eventPublisher subscribe (conferenceProjectionGenerator.apply: ConferenceCreated => Unit)
    eventPublisher subscribe (conferenceProjectionGenerator.apply: ConferenceUpdated => Unit)
    eventPublisher subscribe (conferenceProjectionGenerator.apply: SeatsAdded => Unit)

    context mount(conferencesEndpoint, "/api/conferences/*")
  }
}
