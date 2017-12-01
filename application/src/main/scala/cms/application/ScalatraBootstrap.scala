package cms.application

import javax.servlet.ServletContext

import cms.domain.conference._
import cms.domain.conference.projections.{ConferenceProjection, ConferenceProjectionGenerator}
import cms.domain.order.projections.{PlacedOrderProjection, PlacedOrderProjectionGenerator}
import cms.domain.order.{OrderCommandHandler, OrderPlaced}
import cms.infrastructure.conference.Conferences
import cms.infrastructure.order.Orders
import cms.infrastructure.{InMemoryEventPublisher, InMemoryEventSourcedRepository, InMemoryProjectionRepository, UUIDGenerator}
import org.scalatra.LifeCycle

final class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext){
    val eventPublisher = new InMemoryEventPublisher
    val eventSourcedRepository = new InMemoryEventSourcedRepository(eventPublisher)
    val idGenerator = new UUIDGenerator

    val conferenceCommandHandler = new ConferenceCommandHandler(eventSourcedRepository)
    val conferenceProjectionRepository = new InMemoryProjectionRepository[ConferenceProjection] {}
    val conferenceProjectionGenerator = new ConferenceProjectionGenerator(conferenceProjectionRepository)
    val conferencesEndpoint = new Conferences(conferenceCommandHandler, conferenceProjectionRepository)

    val placedOrderProjectionRepository = new InMemoryProjectionRepository[PlacedOrderProjection] {}
    val placedOrderProjectionGenerator = new PlacedOrderProjectionGenerator(placedOrderProjectionRepository)
    val orderCommandHandler = new OrderCommandHandler(eventSourcedRepository, idGenerator)
    val ordersEndpoint = new Orders(orderCommandHandler, placedOrderProjectionRepository)

    eventPublisher subscribe (conferenceProjectionGenerator.apply: ConferenceCreated => Unit)
    eventPublisher subscribe (conferenceProjectionGenerator.apply: ConferencePublished => Unit)
    eventPublisher subscribe (conferenceProjectionGenerator.apply: ConferenceUpdated => Unit)
    eventPublisher subscribe (conferenceProjectionGenerator.apply: SeatsAdded => Unit)

    eventPublisher subscribe (placedOrderProjectionGenerator.apply: OrderPlaced => Unit)

    context mount(conferencesEndpoint, "/api/conferences/*")
    context mount(ordersEndpoint, "/api/orders/*")
  }
}
