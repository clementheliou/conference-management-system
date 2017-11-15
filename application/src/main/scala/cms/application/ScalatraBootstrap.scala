package cms.application

import javax.servlet.ServletContext

import cms.domain.conference.{ConferenceCommandHandler, ConferenceEvent}
import cms.infrastructure.api.conference.Conferences
import cms.infrastructure.repository.InMemoryEventSourcedRepository
import org.scalatra.LifeCycle

final class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext){
    val conferenceEventRepository = new InMemoryEventSourcedRepository[ConferenceEvent]
    val conferenceCommandHandler = new ConferenceCommandHandler(conferenceEventRepository)
    val conferencesEndpoint = new Conferences(conferenceCommandHandler)

    context mount(conferencesEndpoint, "/api/conferences/*")
  }
}
