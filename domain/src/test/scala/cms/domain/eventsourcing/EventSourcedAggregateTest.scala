package cms.domain.eventsourcing

import org.scalatest.{FlatSpec, Matchers}

class EventSourcedAggregateTest extends FlatSpec with Matchers {

  "An event-sourced aggregate" should "add raised events to the pending events who are waiting for to be persisted" in {

    // Given
    val eventSourcedAggregate = new DummyAggregate

    // When
    val anEvent = new Event {}
    eventSourcedAggregate raise anEvent

    // Then
    eventSourcedAggregate.events.map(_.event) should contain(anEvent)
  }

  it should "sequentially version raised events" in {

    // Given
    val eventSourcedAggregate = new DummyAggregate

    // When
    1 to 3 foreach { _ =>
      eventSourcedAggregate raise {
        new Event {}
      }
    }

    // Then
    eventSourcedAggregate.events.map(_.version) should contain inOrderOnly(0, 1, 2)
  }

  it should "declare itself as source of the events it raised" in {

    // Given
    val eventSourcedAggregate = new DummyAggregate {
      override def id = "an aggregate id"
    }

    // When
    eventSourcedAggregate raise {
      new Event {}
    }

    // Then
    eventSourcedAggregate.events.map(_.sourceId) should contain only "an aggregate id"
  }

  private class DummyAggregate extends EventSourcedAggregate {
    override def raise(event: Event): Unit = super.raise(event)

    override def id = "a default id"
  }

}
