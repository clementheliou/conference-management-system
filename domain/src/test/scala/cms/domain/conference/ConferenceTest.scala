package cms.domain.conference

import org.scalatest.{FlatSpec, Matchers}

class ConferenceTest extends FlatSpec with Matchers {

  "A conference" should "use its slug as identifier" in {

    // When
    val conference = Conference(name = "MixIT 2018", slug = "mix-it-18")

    // Then
    conference.id should equal("mix-it-18")
  }

  it can "be updated with a new name" in {

    // Given
    val history = List(ConferenceCreated(name = "MixIT 2018", slug = "mix-it-18"))
    val conference = Conference(id = "mix-it-18", history)

    // When
    conference.update(name = "MixIT 18'")

    // Then
    conference.events.map(_.event) should contain only ConferenceUpdated(name = "MixIT 18'")
  }

  it can "not be instantiated from an empty history" in {
    the[IllegalArgumentException] thrownBy {
      Conference(id = "mix-it-18", Nil)
    } should have message "Either create a new conference from a slug or provide an history"
  }
}
