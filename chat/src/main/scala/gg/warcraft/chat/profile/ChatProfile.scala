package gg.warcraft.chat.profile

import java.util.UUID

import gg.warcraft.monolith.api.util.ColorCode

object ChatProfile {
  val console: ChatProfile =
    ChatProfile("Console", None, ColorCode.YELLOW + "[Server]", null)
}

case class ChatProfile(
    name: String,
    playerId: Option[UUID],
    tag: String,
    home: String,
    optOut: Set[String] = Set.empty
)
