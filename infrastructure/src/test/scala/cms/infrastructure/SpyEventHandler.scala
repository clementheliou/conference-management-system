package cms.infrastructure

import org.scalatest.matchers.{MatchResult, Matcher}

final class SpyEventHandler[T](var callCount: Int = 0) extends (T => Unit) {
  def apply(event: T): Unit = callCount += 1
}

object SpyEventHandler {

  def assertCallCount(expected: Int) = Matcher { (handler: SpyEventHandler[_]) =>
    MatchResult(
      handler.callCount == expected,
      s"Event handler should have been called $expected times, current ${ handler.callCount } times",
      s"Event handler not expected to have been called $expected times, current ${ handler.callCount } times"
    )
  }

  val haveBeenCalled = assertCallCount(1)
  val haveBeenCalledOnce = haveBeenCalled
  val haveBeenCalledTwice = assertCallCount(2)
}