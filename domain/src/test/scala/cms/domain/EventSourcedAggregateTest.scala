package cms.domain

import org.scalatest.{FlatSpec, Matchers}

class EventSourcedAggregateTest extends FlatSpec with Matchers {

  "An event-sourced aggregate" should "add raised events to the pending events which are waiting for to be persisted" in {

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
    1 to 3 foreach { _ => eventSourcedAggregate raise { new Event {} } }

    // Then
    eventSourcedAggregate.events.map(_.version) should contain inOrderOnly(0, 1, 2)
  }

  it should "declare itself as source of the events it raised" in {

    // Given
    val eventSourcedAggregate = new DummyAggregate {
      override def id = "an aggregate id"
    }

    // When
    eventSourcedAggregate raise { new Event {} }

    // Then
    eventSourcedAggregate.events.map(_.sourceId) should contain only "an aggregate id"
  }

  "A decision projection" should "increment aggregate version each time an event is projected against it" in {

    // Given
    val eventSourcedAggregate = new DummyAggregate
    val history = Seq(new Event {}, new Event {})

    // When
    eventSourcedAggregate.state.computeFrom(history)
    eventSourcedAggregate raise { new Event {} }

    // Then
    eventSourcedAggregate.events.map(_.version) should contain only 2
  }

  it should "project every event from the history against its aggregate" in {

    // Given
    val eventSourcedAggregate = new DummyAggregate
    val history = Seq(new Event {}, new Event {})

    // When
    eventSourcedAggregate.state.computeFrom(history)

    // Then
    eventSourcedAggregate.projectedEvents should contain theSameElementsInOrderAs history
  }

  private class DummyAggregate extends EventSourcedAggregate {

    override type EventType = Event

    override def raise(event: Event): Unit = super.raise(event)

    def id = "a default id"

    var projectedEvents = Seq[Event]()

    val state = new DecisionProjection {
      def applyEvent(event: Event): Unit = projectedEvents = projectedEvents :+ event
    }
  }

}
