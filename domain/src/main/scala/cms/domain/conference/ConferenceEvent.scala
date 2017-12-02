package cms.domain.conference

import cms.domain.Event

sealed trait ConferenceEvent extends Event

case class ConferenceCreated(name: String, slug: String) extends ConferenceEvent

case class ConferencePublished(id: String) extends ConferenceEvent

case class ConferenceUpdated(id: String, name: String) extends ConferenceEvent

case class SeatsAdded(conferenceId: String, seatType: String, quota: Int) extends ConferenceEvent

case class SeatsReservationRejected(conferenceId: String, orderId: String, request: (String, Int)*)
  extends ConferenceEvent
