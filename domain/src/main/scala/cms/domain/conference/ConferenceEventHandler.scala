package cms.domain.conference

import cms.domain.order.OrderPlaced
import com.typesafe.scalalogging.Logger

final class ConferenceEventHandler(conferenceCommandHandler: ConferenceCommandHandler) {

  private val logger = Logger(getClass)

  def apply(event: OrderPlaced){
    logger.info(s"Triggering a seats reservation from $event")
    conferenceCommandHandler handle MakeSeatsReservation(event.orderId, event.conferenceId, event.seats)
  }
}
