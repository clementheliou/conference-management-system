package cms.domain.conference

import cms.domain.InMemoryEventSourcedRepository
import org.scalatest.{FlatSpec, Matchers}

class ConferenceAcceptanceTest extends FlatSpec with Matchers {

  "A conference" can "be created" in {

    // Given
    val conferenceCommandHandler = new ConferenceCommandHandler with InMemoryEventSourcedRepository

    // When
    conferenceCommandHandler.handle(CreateConference(name = "MixIT 2018", slug = "mix-it-18"))

    // Then
    conferenceCommandHandler.get("mix-it-18") should contain only
      ConferenceCreated(name = "MixIT 2018", slug = "mix-it-18")
  }

  it can "be updated with a name" in {

    // Given
    val conferenceCommandHandler = new ConferenceCommandHandler with InMemoryEventSourcedRepository
    val conferenceId = "mix-it-18"

    conferenceCommandHandler.setHistory(conferenceId, ConferenceCreated(name = "MixIT 2018", slug = conferenceId))

    // When
    conferenceCommandHandler.handle(UpdateConference(conferenceId, name = "MixIT 18"))

    // Then
    conferenceCommandHandler.get(conferenceId) should contain inOrderOnly(
      ConferenceCreated(name = "MixIT 2018", slug = conferenceId),
      ConferenceUpdated(id = conferenceId, name = "MixIT 18")
    )
  }
}
