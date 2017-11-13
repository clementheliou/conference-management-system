package cms.infrastructure.repository

import cms.domain.{Event, EventSourcedAggregate}
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class InMemoryEventSourcedRepositoryTest extends FlatSpec with Matchers with OptionValues {

  trait Setup {
    val repository = new InMemoryEventSourcedRepository[DummyEvent] {}

    def setExistingEventStream(aggregateId: String, eventStream: DummyEvent*){
      val aggregate = new DummyAggregate {override def id = aggregateId }
      eventStream.foreach(aggregate.raise)
      repository.save(aggregate)
    }
  }

  "An in-memory event-sourced repository" should "mark the absence of an event stream" in new Setup {

    // When
    val maybeEventStream = repository.find("an id")

    // Then
    maybeEventStream shouldBe None
  }

  it should "get an existing event stream from its source id" in new Setup {

    // Given
    setExistingEventStream("an id", DummyEvent(1), DummyEvent(2))

    // When
    val maybeEventStream = repository.find("an id")

    // Then
    maybeEventStream.value should contain inOrderOnly(DummyEvent(1), DummyEvent(2))
  }

  it should "create an event stream from the aggregate pending events if not exists" in new Setup {

    // Given
    val aggregate = new DummyAggregate {override def id = "an id" }
    aggregate raise DummyEvent(1)
    aggregate raise DummyEvent(2)

    // When
    repository.save(aggregate)

    // Then
    repository.find("an id").value should contain inOrderOnly(DummyEvent(1), DummyEvent(2))
  }

  it should "append the pending events to the existing event stream" in new Setup {

    // Given
    setExistingEventStream("an id", DummyEvent(1), DummyEvent(2))

    val aggregate = new DummyAggregate {override def id = "an id" }
    aggregate raise DummyEvent(3)
    aggregate raise DummyEvent(4)

    // When
    repository.save(aggregate)

    // Then
    repository.find("an id").value should contain inOrderOnly(
      DummyEvent(1),
      DummyEvent(2),
      DummyEvent(3),
      DummyEvent(4)
    )

  }

  trait DummyAggregate extends EventSourcedAggregate[DummyEvent] {
    override def raise(event: DummyEvent): Unit = super.raise(event)
  }

  case class DummyEvent(id: Int) extends Event

}
