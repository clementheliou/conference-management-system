package cms.domain.conference

import cms.domain.InMemoryEventSourcedRepository
import org.scalatest.{FlatSpec, Matchers}

class ConferenceAcceptanceTest extends FlatSpec with Matchers {

  private val conferenceCommandHandler = new ConferenceCommandHandler with InMemoryEventSourcedRepository

  "A conference" can "be created" in {

    // When
    conferenceCommandHandler.handle(CreateConference(name = "MixIT 2018", slug = "mix-it-18"))

    // Then
    conferenceCommandHandler.get("mix-it-18") should contain only
      ConferenceCreated(name = "MixIT 2018", slug = "mix-it-18")
  }
}
