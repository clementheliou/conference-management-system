package cms.domain.features.stages

import cms.domain.conference.{ConferenceCreated, ConferencePublished, SeatsAdded}
import cms.domain.{Event, InMemoryEventPublisher, InMemoryEventSourcedRepository}
import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.{ProvidedScenarioState, Quoted}

class GivenAConference extends Stage[GivenAConference] {

  @ProvidedScenarioState var conferenceId = "mix-it-18"

  @ProvidedScenarioState val eventPublisher = new InMemoryEventPublisher

  @ProvidedScenarioState val eventSourcedRepository = new InMemoryEventSourcedRepository(eventPublisher)

  @ProvidedScenarioState var history: Seq[Event] = _

  def a_published_conference_with_a_quota_of_$_$_seats(quota: Int, @Quoted seatType: String){
    history = Seq(
      ConferenceCreated(name = "MixIT", conferenceId),
      SeatsAdded(conferenceId, seatType, quota),
      ConferencePublished(conferenceId)
    )

    eventSourcedRepository.setHistory("mix-it-18", history: _*)
  }

}
