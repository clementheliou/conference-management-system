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

    val eventSourcedAggregate = new DummyAggregate(history = Seq(new Event {}, new Event {}))

    // When
    eventSourcedAggregate raise { new Event {} }

    // Then
    eventSourcedAggregate.events.map(_.version) should contain only 2
  }

  it should "project every event from the history against its aggregate" in {

    // Given
    val history = Seq(new Event {}, new Event {})

    // When
    val eventSourcedAggregate = new DummyAggregate(history)

    // Then
    eventSourcedAggregate.state.projectedEvents should contain theSameElementsInOrderAs history
  }

  private class DummyAggregate(history: Seq[Event] = Seq.empty) extends EventSourcedAggregate {

    override type EventType = Event

    override def raise(event: Event): Unit = super.raise(event)

    def id = "a default id"

    val state = DummyAggregateDecisionProjection().computeFrom(history)

    case class DummyAggregateDecisionProjection(projectedEvents: Seq[Event] = Seq.empty) extends DecisionProjection {
      def apply(event: Event) = copy(projectedEvents = projectedEvents :+ event)
    }
  }

}
