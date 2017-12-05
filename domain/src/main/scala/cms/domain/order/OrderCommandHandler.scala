package cms.domain.order

import cms.domain.conference.Conference
import cms.domain.{CommandHandler, EventSourcedRepository, IdGenerator}
import com.typesafe.scalalogging.Logger

final class OrderCommandHandler(repository: EventSourcedRepository, idGenerator: IdGenerator)
  extends CommandHandler[OrderCommand] {

  private val logger = Logger(classOf[OrderCommandHandler])

  def handle(command: OrderCommand): Unit = command match {
    case ConfirmSeatsReservation(orderId, seats) => confirmSeatsReservation(orderId, seats)
    case c: PlaceOrder => placeOrder(c)
  }

  private def confirmSeatsReservation(id: String, seats: (String, Int)): Unit = repository.find[Order](id) match {
    case Some(order) =>
      order.confirmSeatsReservation(seats)
      repository save order
    case None => logger.warn(s"Discard seats reservation confirmation command on missing order (id=$id)")
  }

  private def placeOrder(command: PlaceOrder): Unit = repository.find[Conference](command.conferenceId) match {
    case Some(conference) => placeOrderForConference(command, conference)
    case None => logger.warn(s"Discard order placing command on missing conference (id=${ command.conferenceId })")
  }

  private def placeOrderForConference(command: PlaceOrder, conference: Conference){
    val orderId = idGenerator.get
    val (seatType, quantity) = command.seats

    repository.find[Order](orderId) match {
      case Some(_) => logger.warn(s"Discard order placing command on existing order (id=$orderId)")
      case None => if (quantity > 0) repository save Order(orderId, conference.id, seatType -> quantity)
    }
  }
}
