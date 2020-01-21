package gg.warcraft.chat.profile

import java.util.UUID

object ChatProfile {
  val console: ChatProfile =
    ChatProfile(None, "Console", Some(ChatTag.console), None, Set())
}

case class ChatProfile(
    playerId: Option[UUID],
    name: String,
    tag: Option[ChatTag],
    homeChannel: Option[String],
    optedOut: Set[String]
)
