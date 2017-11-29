package cms.application

import javax.servlet.ServletContext

import cms.domain.conference._
import cms.domain.order.OrderCommandHandler
import cms.infrastructure.conference.{Conferences, InMemoryConferenceProjectionRepository}
import cms.infrastructure.order.Orders
import cms.infrastructure.{InMemoryEventPublisher, InMemoryEventSourcedRepository, UUIDGenerator}
import org.scalatra.LifeCycle

final class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext){
    val eventPublisher = new InMemoryEventPublisher
    val eventSourcedRepository = new InMemoryEventSourcedRepository(eventPublisher)
    val idGenerator = new UUIDGenerator

    val conferenceCommandHandler = new ConferenceCommandHandler(eventSourcedRepository)
    val conferenceProjectionRepository = new InMemoryConferenceProjectionRepository
    val conferenceProjectionGenerator = new ConferenceProjectionGenerator(conferenceProjectionRepository)
    val conferencesEndpoint = new Conferences(conferenceCommandHandler, conferenceProjectionRepository)

    val orderCommandHandler = new OrderCommandHandler(eventSourcedRepository, idGenerator)
    val ordersEndpoint = new Orders(orderCommandHandler)

    eventPublisher subscribe (conferenceProjectionGenerator.apply: ConferenceCreated => Unit)
    eventPublisher subscribe (conferenceProjectionGenerator.apply: ConferencePublished => Unit)
    eventPublisher subscribe (conferenceProjectionGenerator.apply: ConferenceUpdated => Unit)
    eventPublisher subscribe (conferenceProjectionGenerator.apply: SeatsAdded => Unit)

    context mount(conferencesEndpoint, "/api/conferences/*")
    context mount(ordersEndpoint, "/api/orders/*")
  }
}
