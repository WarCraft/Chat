package gg.warcraft.chat.profile

import java.util.UUID

object ChatProfile {
  val console: ChatProfile =
    ChatProfile("Console", None, Some(ChatTag.console), null, Set())
}

case class ChatProfile(
    name: String,
    playerId: Option[UUID],
    tag: Option[ChatTag],
    homeChannel: String,
    optedOut: Set[String]
)
