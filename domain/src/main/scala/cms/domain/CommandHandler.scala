package cms.domain

trait CommandHandler[T <: Command] {
  def handle(command: T): Unit
}
