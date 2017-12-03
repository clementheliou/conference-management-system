package cms.domain.conference

import cms.domain.Command

sealed trait ConferenceCommand extends Command

case class AddSeatsToConference(conferenceId: String, seatType: String, quota: Int) extends ConferenceCommand

case class CreateConference(name: String, slug: String) extends ConferenceCommand

case class MakeSeatsReservation(orderId: String, conferenceId: String, request: (String, Int)) extends ConferenceCommand

case class PublishConference(id: String) extends ConferenceCommand

case class UpdateConference(id: String, name: String) extends ConferenceCommand