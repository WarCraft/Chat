package gg.warcraft.chat.profile

import java.util.UUID

case class ChatProfile(
    name: String,
    playerId: UUID,
    tag: String,
    home: String
)
