package cms.infrastructure

import cms.domain.Event
import cms.infrastructure.SpyEventHandler.{haveBeenCalled, haveBeenCalledOnce}
import org.scalatest.{FlatSpec, Matchers}

class InMemoryEventPublisherTest extends FlatSpec with Matchers {

  "An in-memory event publisher" should "forward published event to subscribers" in {

    // Given
    val eventPublisher = new InMemoryEventPublisher
    val anEventHandler = new SpyEventHandler[DummyEvent]
    val anotherEventHandler = new SpyEventHandler[DummyEvent]

    eventPublisher.subscribe(anEventHandler)
    eventPublisher.subscribe(anotherEventHandler)

    // When
    eventPublisher.publish(DummyEvent(1))

    // Then
    anEventHandler should haveBeenCalledOnce
    anotherEventHandler should haveBeenCalledOnce
  }

  it should "not forward published event to subscribers of other event types" in {

    // Given
    val eventPublisher = new InMemoryEventPublisher
    val anEventHandler = new SpyEventHandler[DummyEvent]

    eventPublisher.subscribe(anEventHandler)

    // When
    eventPublisher.publish(new Event {})

    // Then
    anEventHandler should not(haveBeenCalled)
  }
}
