package cms.infrastructure

import java.util.UUID.randomUUID

import cms.domain.IdGenerator

final class UUIDGenerator extends IdGenerator {
  def get = randomUUID().toString
}
