package gg.warcraft.chat.profile

import java.util.UUID

case class ChatProfile(
    playerId: Option[UUID],
    name: String,
    tag: Option[ChatTag],
    homeChannel: Option[String],
    optedOut: Set[String]
)
