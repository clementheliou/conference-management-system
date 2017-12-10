package cms.domain.conference.projections

import cms.domain.InMemoryProjectionRepository
import cms.domain.conference._
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class ConferenceProjectionGeneratorTest extends FlatSpec with Matchers with OptionValues {

  trait Setup {
    val repository = new InMemoryProjectionRepository[ConferenceProjection]{}
    val conferenceProjectionGenerator = new ConferenceProjectionGenerator(repository)
  }

  "A conference projection generator" should "create a projection on ConferenceCreated" in new Setup {

    // Given
    val conferenceCreated = ConferenceCreated(name = "MixIT 18", slug = "mix-it-18")

    // When
    conferenceProjectionGenerator.apply(conferenceCreated)

    // Then
    repository.get("mix-it-18").value shouldBe ConferenceProjection(
      lastUpdate = conferenceCreated.creationDate,
      name = "MixIT 18",
      id = "mix-it-18"
    )
  }

  it should "update the projection name on ConferenceUpdated" in new Setup {

    // Given
    conferenceProjectionGenerator.apply(ConferenceCreated(name = "MixIT 2018", slug = "mix-it-18"))
    val conferenceUpdated = ConferenceUpdated(id = "mix-it-18", name = "MixIT 18")

    // When
    conferenceProjectionGenerator.apply(conferenceUpdated)

    // Then
    repository.get("mix-it-18").value shouldBe ConferenceProjection(
      lastUpdate = conferenceUpdated.creationDate,
      name = "MixIT 18",
      id = "mix-it-18"
    )
  }

  it should "update the projection publication status on ConferencePublished" in new Setup {

    // Given
    conferenceProjectionGenerator.apply(ConferenceCreated(name = "MixIT 2018", slug = "mix-it-18"))
    val conferencePublished = ConferencePublished("mix-it-18")

    // When
    conferenceProjectionGenerator apply conferencePublished

    // Then
    repository.get("mix-it-18").value shouldBe ConferenceProjection(
      lastUpdate = conferencePublished.creationDate,
      name = "MixIT 2018",
      published = true,
      id = "mix-it-18"
    )
  }

  it should "update the projection seats on SeatsAdded" in new Setup {

    // Given
    conferenceProjectionGenerator apply ConferenceCreated(name = "MixIT 2018", slug = "mix-it-18")
    val seatsAdded = SeatsAdded(conferenceId = "mix-it-18", seatType = "Workshop", quota = 100)

    // When
    conferenceProjectionGenerator apply seatsAdded

    // Then
    repository.get("mix-it-18").value shouldBe ConferenceProjection(
      lastUpdate = seatsAdded.creationDate,
      name = "MixIT 2018",
      id = "mix-it-18",
      seats = Map("Workshop" -> 100)
    )
  }

  it should "update the projection seats on SeatsReserved" in new Setup {

    // Given
    conferenceProjectionGenerator apply ConferenceCreated(name = "MixIT 2018", slug = "mix-it-18")
    conferenceProjectionGenerator apply SeatsAdded(conferenceId = "mix-it-18", seatType = "Workshop", quota = 100)
    val seatsReserved = SeatsReserved(conferenceId = "mix-it-18", orderId = "ID-1", "Workshop" -> 10)

    // When
    conferenceProjectionGenerator apply seatsReserved

    // Then
    repository.get("mix-it-18").value shouldBe ConferenceProjection(
      lastUpdate = seatsReserved.creationDate,
      name = "MixIT 2018",
      id = "mix-it-18",
      seats = Map("Workshop" -> 90)
    )
  }
}
