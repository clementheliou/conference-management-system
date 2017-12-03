package cms.domain.conference

import cms.domain.EventSourcedAggregate
import com.typesafe.scalalogging.Logger

import scala.collection.immutable.Map.empty

final class Conference private(val id: String, history: Seq[ConferenceEvent] = Nil) extends EventSourcedAggregate {

  override type EventType = ConferenceEvent

  private[this] val logger = Logger(classOf[Conference])
  protected val state = ConferenceDecisionProjection().computeFrom(history)

  private def this(name: String, slug: String){
    this(slug)
    raise { ConferenceCreated(name, slug) }
  }

  def addSeats(seatType: String, quota: Int): Unit = state.seats.get(seatType) match {
    case None => raise { SeatsAdded(id, seatType, quota) }
    case Some(_) => logger.warn(s"Discard seats addition on existing seats (type: $seatType, conference: $id)")
  }

  def makeSeatsReservation(orderId: String, seatsRequest: (String, Int)){
    if (state.published) {
      val (seatType, _) = seatsRequest

      if (!state.seats.contains(seatType)) {
        logger.warn(s"Reject seats reservation due to a missing seat type (id: $id)")
        raise { SeatsReservationRejected(id, orderId, seatsRequest) }
      }

    } else {
      logger.warn(s"Reject seats reservation for a conference that is not published yet (id: $id)")
      raise { SeatsReservationRejected(id, orderId, seatsRequest) }
    }
  }

  def publish(){
    if (!state.published) {
      raise { ConferencePublished(id) }
    }
  }

  def update(name: String): Unit = raise { ConferenceUpdated(id, name) }

  case class ConferenceDecisionProjection(
    name: String = "",
    slug: String = "",
    published: Boolean = false,
    seats: Map[String, Int] = empty
  ) extends DecisionProjection {

    def apply(event: ConferenceEvent) = event match {
      case e: ConferenceCreated => copy(name = e.name, slug = e.slug)
      case _: ConferencePublished => copy(published = true)
      case e: ConferenceUpdated => copy(name = e.name)
      case e: SeatsAdded => copy(seats = seats + (e.seatType -> e.quota))
      case _: SeatsReservationRejected => this
    }
  }
}

object Conference {

  implicit val rehydrateAggregate: (String, Seq[ConferenceEvent]) => Conference = apply

  def apply(name: String, slug: String) = new Conference(name, slug)

  def apply(id: String, history: Seq[ConferenceEvent]) = {
    if (history.isEmpty) {
      throw new IllegalArgumentException("Either create a new conference from a slug or provide an history")
    }

    new Conference(id, history)
  }
}
