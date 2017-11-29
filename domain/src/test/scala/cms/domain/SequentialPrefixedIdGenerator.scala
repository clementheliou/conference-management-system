package cms.domain

final class SequentialPrefixedIdGenerator extends IdGenerator {
  private var cpt = 0

  def get ={
    cpt += 1
    s"ID-$cpt"
  }
}
