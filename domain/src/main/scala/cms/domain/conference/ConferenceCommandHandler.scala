package cms.domain.conference

import cms.domain.{CommandHandler, EventSourcedRepository}

final class ConferenceCommandHandler(repository: EventSourcedRepository)
  extends CommandHandler[ConferenceCommand, Conference](repository) {

  def handle(command: ConferenceCommand): Unit = command match {
    case AddSeatsToConference(id, seatType, quota) => handle(id, command) { _ addSeats(seatType, quota) }
    case CreateConference(name, slug) => handleFirstCommand(slug, command) { () => Conference(name, slug) }
    case MakeSeatsReservation(orderId, id, request) => handle(id, command) { _ makeSeatsReservation(orderId, request) }
    case PublishConference(id) => handle(id, command) { _ publish() }
    case UpdateConference(id, name) => handle(id, command) { _ update name }
  }

}
