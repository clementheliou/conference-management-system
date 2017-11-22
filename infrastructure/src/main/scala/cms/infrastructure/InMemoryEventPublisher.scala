package cms.infrastructure

import cms.domain.Event

import scala.collection.mutable.{Map => MutableMap}
import scala.reflect.ClassTag

final class InMemoryEventPublisher {

  private[this] val subscriptions = MutableMap[Class[_], Seq[Event => Unit]]()

  def publish[T <: Event](event: T): Unit = subscriptions.getOrElse(event.getClass, Nil) foreach (_ apply event)

  def subscribe[T <: Event](handler: T => Unit)(implicit classTag: ClassTag[T]){
    val existingSubscriptions = subscriptions.getOrElse(classTag.runtimeClass, Nil)
    subscriptions(classTag.runtimeClass) = existingSubscriptions :+ handler.asInstanceOf[Event => Unit]
  }
}
