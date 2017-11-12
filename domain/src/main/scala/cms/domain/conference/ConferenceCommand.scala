package cms.domain.conference

import cms.domain.Command

sealed trait ConferenceCommand extends Command

case class CreateConference(name: String, slug: String) extends ConferenceCommand
