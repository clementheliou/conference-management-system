package cms.domain.order

import cms.domain.{CommandHandler, EventSourcedRepository, IdGenerator}

final class OrderCommandHandler(repository: EventSourcedRepository, idGenerator: IdGenerator)
  extends CommandHandler[OrderCommand, Order](repository) {

  def handle(command: OrderCommand): Unit = command match {
    case ConfirmSeatsReservation(orderId, seats) => handle(orderId, command) { _ confirmSeatsReservation seats }
    case PlaceOrder(conferenceId, (seatType, quantity)) => if (quantity > 0) {
      val orderId = idGenerator.get
      handleFirstCommand(orderId, command) { () => Order(orderId, conferenceId, seatType -> quantity) }
    }
    case RejectOrder(orderId) => handle(orderId, command) { _ reject() }
  }

}
