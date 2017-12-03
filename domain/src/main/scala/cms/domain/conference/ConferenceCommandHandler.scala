package cms.domain.conference

import cms.domain.{CommandHandler, EventSourcedRepository}
import com.typesafe.scalalogging.Logger

final class ConferenceCommandHandler(repository: EventSourcedRepository) extends CommandHandler[ConferenceCommand] {

  private val logger = Logger(classOf[ConferenceCommandHandler])

  def handle(command: ConferenceCommand): Unit = command match {
    case c: AddSeatsToConference => addSeatsToConference(c)
    case c: CreateConference => createConference(c)
    case c: MakeSeatsReservation => makeSeatsReservation(c)
    case c: PublishConference => publishConference(c)
    case c: UpdateConference => updateConference(c)
  }

  private def addSeatsToConference(c: AddSeatsToConference): Unit = repository.find[Conference](c.conferenceId) match {
    case Some(conference) =>
      conference.addSeats(c.seatType, c.quota)
      repository save conference
    case None => logger.warn(s"Discard seats addition command on missing conference (slug=${ c.conferenceId })")
  }

  private def createConference(c: CreateConference): Unit = repository.find[Conference](c.slug) match {
    case Some(_) => logger.warn(s"Discard creation command on existing conference (slug=${ c.slug })")
    case None => repository save { Conference(c.name, c.slug) }
  }

  private def makeSeatsReservation(c: MakeSeatsReservation): Unit = repository.find[Conference](c.conferenceId) match {
    case Some(conference) =>
      conference.makeSeatsReservation(c.orderId, c.request: _*)
      repository save conference
    case None => logger.warn(s"Discard seats reservation command on missing conference (slug=${ c.conferenceId })")
  }

  private def publishConference(c: PublishConference): Unit = repository.find[Conference](c.id) match {
    case Some(conference) =>
      conference.publish()
      repository save conference
    case None => logger.warn(s"Discard publication command on missing conference (slug=${ c.id })")
  }

  private def updateConference(c: UpdateConference): Unit = repository.find[Conference](c.id) match {
    case Some(conference) =>
      conference.update(c.name)
      repository.save(conference)
    case None => logger.warn(s"Discard update command on missing conference (slug=${ c.id })")
  }
}
