package cms.domain.order

import cms.domain.conference.{SeatsReservationRejected, SeatsReserved}
import com.typesafe.scalalogging.Logger

final class OrderEventHandler(orderCommandHandler: OrderCommandHandler) {

  private val logger = Logger(getClass)

  def apply(event: SeatsReserved){
    logger.info(s"Triggering seats reservation confirmation from $event")
    orderCommandHandler handle ConfirmSeatsReservation(event.orderId, event.seats)
  }

  def apply(event: SeatsReservationRejected){
    logger.info(s"Triggering order rejection from $event")
    orderCommandHandler handle RejectOrder(event.orderId)
  }
}
