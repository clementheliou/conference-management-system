package cms.domain.order

import cms.domain.conference.SeatsReserved
import com.typesafe.scalalogging.Logger

final class OrderEventHandler(orderCommandHandler: OrderCommandHandler) {

  private val logger = Logger(getClass)

  def apply(event: SeatsReserved){
    logger.info(s"Triggering seats reservation confirmation from $event")
    orderCommandHandler handle ConfirmSeatsReservation(event.orderId, event.seats)
  }
}
