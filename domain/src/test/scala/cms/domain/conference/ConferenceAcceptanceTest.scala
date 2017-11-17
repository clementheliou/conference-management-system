package cms.domain.conference

import cms.domain.InMemoryEventSourcedRepository
import org.scalatest.{FlatSpec, Matchers}

class ConferenceAcceptanceTest extends FlatSpec with Matchers {

  trait Setup {
    val conferenceEventRepository = new InMemoryEventSourcedRepository
    val conferenceCommandHandler = new ConferenceCommandHandler(conferenceEventRepository)
  }

  "A conference" can "be created" in new Setup {

    // When
    conferenceCommandHandler.handle(CreateConference(name = "MixIT 2018", slug = "mix-it-18"))

    // Then
    conferenceEventRepository.getEventStream("mix-it-18") should contain only
      ConferenceCreated(name = "MixIT 2018", slug = "mix-it-18")
  }

  it can "be updated with a name" in new Setup {

    // Given
    val conferenceId = "mix-it-18"

    conferenceEventRepository.setHistory(conferenceId, ConferenceCreated(name = "MixIT 2018", slug = conferenceId))

    // When
    conferenceCommandHandler.handle(UpdateConference(conferenceId, name = "MixIT 18"))

    // Then
    conferenceEventRepository.getEventStream(conferenceId) should contain inOrderOnly(
      ConferenceCreated(name = "MixIT 2018", slug = conferenceId),
      ConferenceUpdated(id = conferenceId, name = "MixIT 18")
    )
  }

  it can "not be created if its the slug is already in use" in new Setup {

    // Given
    val conferenceId = "mix-it-18"

    conferenceEventRepository.setHistory(conferenceId, ConferenceCreated(name = "MixIT 2018", slug = conferenceId))

    // When
    conferenceCommandHandler.handle(CreateConference(name = "MixIT 18", slug = conferenceId))

    // Then
    conferenceEventRepository.getEventStream(conferenceId) should contain only
      ConferenceCreated(name = "MixIT 2018", slug = conferenceId)
  }

  it can "not be updated if not created before" in new Setup {

    // When
    conferenceCommandHandler.handle(UpdateConference("mix-it-18", name = "MixIT 18"))

    // Then
    conferenceEventRepository.find[Conference]("mix-it-18")(Conference.apply) shouldBe None
  }
}
