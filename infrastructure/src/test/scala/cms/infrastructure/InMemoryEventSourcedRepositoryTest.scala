package cms.infrastructure

import cms.domain.{Event, EventSourcedAggregate}
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class InMemoryEventSourcedRepositoryTest extends FlatSpec with Matchers with OptionValues {

  trait Setup {
    val repository = new InMemoryEventSourcedRepository

    def setExistingEventStream(aggregateId: String, eventStream: DummyEvent*){
      val aggregate = new DummyAggregate(id = aggregateId)
      eventStream.foreach(aggregate.raise)
      repository.save(aggregate)
    }
  }

  "An in-memory event-sourced repository" should "mark the absence of an aggregate" in new Setup {

    // When
    val maybeAggregate = repository.find[DummyAggregate]("an id")

    // Then
    maybeAggregate shouldBe None
  }

  it should "get a rehydrated aggregate from its existing event stream" in new Setup {

    // Given
    setExistingEventStream("an id", DummyEvent(1), DummyEvent(2))

    // When
    val maybeAggregate = repository.find[DummyAggregate]("an id")

    // Then
    maybeAggregate.value.rehydratedEvents should contain inOrderOnly(DummyEvent(1), DummyEvent(2))
  }

  it should "create an event stream from the aggregate pending events if not exists" in new Setup {

    // Given
    val aggregate = new DummyAggregate(id = "an id")
    aggregate raise DummyEvent(1)
    aggregate raise DummyEvent(2)

    // When
    repository.save(aggregate)

    // Then
    repository.find[DummyAggregate]("an id").value.rehydratedEvents should contain inOrderOnly(
      DummyEvent(1),
      DummyEvent(2)
    )
  }

  it should "append the pending events to the existing event stream" in new Setup {

    // Given
    setExistingEventStream("an id", DummyEvent(1), DummyEvent(2))

    val aggregate = new DummyAggregate(id = "an id")
    aggregate raise DummyEvent(3)
    aggregate raise DummyEvent(4)

    // When
    repository.save(aggregate)

    // Then
    repository.find[DummyAggregate]("an id").value.rehydratedEvents should contain inOrderOnly(
      DummyEvent(1),
      DummyEvent(2),
      DummyEvent(3),
      DummyEvent(4)
    )

  }

  final class DummyAggregate(val id: String, val rehydratedEvents: Seq[DummyEvent] = Nil)
    extends EventSourcedAggregate {

    override type EventType = DummyEvent

    override def raise(event: DummyEvent): Unit = super.raise(event)

    protected[this] val state = new DecisionProjection {
      def applyEvent(event: DummyEvent): Unit = {}
    }
  }

  implicit val rehydrateFrom: (String, Seq[DummyEvent]) => DummyAggregate = {
    (id, history) => new DummyAggregate(id, history)
  }

  case class DummyEvent(id: Int) extends Event

}
