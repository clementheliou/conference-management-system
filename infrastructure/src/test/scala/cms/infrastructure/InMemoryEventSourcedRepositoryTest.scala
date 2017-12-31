package cms.infrastructure

import cms.domain.EventSourcedAggregate
import cms.infrastructure.SpyEventHandler.haveBeenCalledTwice
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class InMemoryEventSourcedRepositoryTest extends FlatSpec with Matchers with OptionValues {

  trait Setup {
    val eventPublisher = new InMemoryEventPublisher
    val repository = new InMemoryEventSourcedRepository(eventPublisher)

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
    val history = Seq(DummyEvent(1), DummyEvent(2))
    setExistingEventStream("an id", history: _*)

    // When
    val maybeAggregate = repository.find[DummyAggregate]("an id")

    // Then
    maybeAggregate.value.rehydratedEvents should contain theSameElementsInOrderAs history
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
    val history = Seq(DummyEvent(1), DummyEvent(2))
    setExistingEventStream("an id", history: _*)

    val aggregate = new DummyAggregate(id = "an id")
    aggregate raise DummyEvent(3)
    aggregate raise DummyEvent(4)

    // When
    repository.save(aggregate)

    // Then
    repository.find[DummyAggregate]("an id").value.rehydratedEvents should contain theSameElementsInOrderAs
      history :+ DummyEvent(3) :+ DummyEvent(4)
  }

  it should "publish the pending events to make them available to subscribers" in new Setup {

    // Given
    val aggregate = new DummyAggregate(id = "an id")
    aggregate raise DummyEvent(1)
    aggregate raise DummyEvent(2)

    val anEventHandler = new SpyEventHandler[DummyEvent]
    eventPublisher subscribe anEventHandler

    // When
    repository.save(aggregate)

    // Then
    anEventHandler should haveBeenCalledTwice
  }

  final class DummyAggregate(val id: String, val rehydratedEvents: Seq[DummyEvent] = Nil)
    extends EventSourcedAggregate {

    override type EventType = DummyEvent

    override def raise(event: DummyEvent): Unit = super.raise(event)

    protected[this] val state = DummyAggregateDecisionProjection().computeFrom(rehydratedEvents)

    case class DummyAggregateDecisionProjection() extends DecisionProjection {
      def apply(event: DummyEvent) = this
    }
  }

  implicit val rehydrateFrom: (String, Seq[DummyEvent]) => DummyAggregate = {
    (id, history) => new DummyAggregate(id, history)
  }

}
