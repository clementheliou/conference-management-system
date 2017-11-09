package cms.domain.conference

import org.scalatest.{FlatSpec, Matchers}

class ConferenceTest extends FlatSpec with Matchers {

  "A conference" can "be created" in {

    // When
    val conference = new Conference(name = "MixIT 2018", slug = "mix-it-18")

    // Then
    conference.events.map(_.event) should contain only ConferenceCreated(name = "MixIT 2018", slug = "mix-it-18")
  }

  it should "use its slug as identifier" in {

    // When
    val conference = new Conference(name = "MixIT 2018", slug = "mix-it-18")

    // Then
    conference.id should equal("mix-it-18")
  }
}
