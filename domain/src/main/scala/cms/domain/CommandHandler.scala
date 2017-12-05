package cms.domain

import com.typesafe.scalalogging.Logger

abstract class CommandHandler[T <: Command, A <: EventSourcedAggregate]
(repository: EventSourcedRepository)(implicit rehydrateFrom: (String, Seq[A#EventType]) => A) {

  private val logger = Logger(getClass)

  def handle(command: T): Unit

  protected def handle(id: String, command: T)(decide: A => Unit){
    repository.find[A](id) match {
      case Some(aggregate) => decide(aggregate); repository save aggregate
      case None => logger.warn(s"Discard command $command on missing aggregate (id=$id)")
    }
  }

  protected def handleFirstCommand(id: String, command: T)(decide: () => A){
    repository.find[A](id) match {
      case Some(_) => logger.warn(s"Discard command $command on existing aggregate (id=$id)")
      case None => repository save { decide() }
    }
  }
}
